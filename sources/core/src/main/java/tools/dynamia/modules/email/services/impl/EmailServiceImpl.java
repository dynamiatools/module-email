/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.dynamia.modules.email.services.impl;

import java.io.File;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Properties;

import javax.mail.internet.MimeMessage;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import tools.dynamia.commons.logger.LoggingService;
import tools.dynamia.commons.logger.SLF4JLoggingService;
import tools.dynamia.domain.query.QueryParameters;
import tools.dynamia.domain.services.CrudService;
import tools.dynamia.integration.Containers;
import tools.dynamia.modules.email.EmailMessage;
import tools.dynamia.modules.email.EmailServiceException;
import tools.dynamia.modules.email.EmailServiceListener;
import tools.dynamia.modules.email.domain.EmailAccount;
import tools.dynamia.modules.email.domain.EmailTemplate;
import tools.dynamia.modules.email.services.EmailService;
import tools.dynamia.modules.saas.api.AccountServiceAPI;

/**
 *
 * @author ronald
 */
@Service
public class EmailServiceImpl implements EmailService {

	@Autowired
	private CrudService crudService;

	@Autowired
	private AccountServiceAPI accountServiceAPI;

	private VelocityEngine velocityEngine = new VelocityEngine();

	private final LoggingService logger = new SLF4JLoggingService(EmailService.class);

	@Override
	public void send(String to, String subject, String content) {
		send(new EmailMessage(to, subject, content));
	}

	@Override
	public void send(final EmailMessage mailMessage) {

		EmailAccount account = mailMessage.getMailAccount();
		if (account == null) {
			account = getPreferredEmailAccount();
		}

		if (account == null) {
			return;
		}

		if (mailMessage.getTemplate() != null && !mailMessage.getTemplate().isEnabled()) {
			return;
		}

		final EmailAccount finalAccount = account;

		Thread thread = new Thread(() -> {
			try {

				if (mailMessage.getTemplate() != null) {
					processTemplate(mailMessage);
				}

				JavaMailSenderImpl jmsi = (JavaMailSenderImpl) createMailSender(finalAccount);
				MimeMessage mimeMessage = jmsi.createMimeMessage();

				MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
				helper.setTo(mailMessage.getTo());
				if (!mailMessage.getTos().isEmpty()) {
					helper.setTo(mailMessage.getTosAsArray());
				}
				String from = finalAccount.getFromAddress();
				String personal = finalAccount.getName();
				if (from != null && personal != null) {
					helper.setFrom(from, personal);
				}

				if (!mailMessage.getBccs().isEmpty()) {
					helper.setBcc(mailMessage.getBccsAsArray());
				}

				if (!mailMessage.getCcs().isEmpty()) {
					helper.setCc(mailMessage.getCcsAsArray());
				}

				helper.setSubject(mailMessage.getSubject());
				if (mailMessage.getPlainText() != null && mailMessage.getContent() != null) {
					helper.setText(mailMessage.getPlainText(), mailMessage.getContent());
				} else {
					helper.setText(mailMessage.getContent(), true);
				}

				for (File archivo : mailMessage.getAttachtments()) {
					helper.addAttachment(archivo.getName(), archivo);
				}

				fireOnMailSending(mailMessage);
				logger.info("Sending e-mail " + mailMessage);
				jmsi.send(mimeMessage);

				logger.info("Email sended succesfull!");
				fireOnMailSended(mailMessage);
			} catch (Exception me) {
				logger.error("Error sending e-mail " + mailMessage, me);
				fireOnMailSendFail(mailMessage, me);
				throw new EmailServiceException("Error sending mail message " + mailMessage, me);

			}
		});
		thread.start();

	}

	@Override
	public EmailAccount getPreferredEmailAccount() {
		EmailAccount account = crudService.findSingle(EmailAccount.class, "preferred", true);
		if (account == null) {
			logger.warn("There is not a preferred email account, trying to get System Account email account ");
			Long systemAccountId = accountServiceAPI.getSystemAccountId();
			if (systemAccountId != null) {
				account = crudService.findSingle(EmailAccount.class,
						QueryParameters.with("accountId", systemAccountId).add("preferred", true));
			}
		}
		return account;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void setPreferredEmailAccount(EmailAccount account) {
		crudService.batchUpdate(EmailAccount.class, "preferred", false, new QueryParameters());
		crudService.updateField(account, "preferred", true);
	}

	@Override
	public EmailTemplate getTemplateByName(String name) {
		EmailTemplate template = crudService.findSingle(EmailTemplate.class, "name", name);
		if (template == null) {
			logger.warn("There is not a template with name " + name + ", trying to get System Account template ");
			Long systemAccountId = accountServiceAPI.getSystemAccountId();
			if (systemAccountId != null) {
				template = crudService.findSingle(EmailTemplate.class,
						QueryParameters.with("accountId", systemAccountId).add("name", name));
			}
		}
		return template;
	}

	private MailSender createMailSender(EmailAccount account) {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(account.getServerAddress());
		mailSender.setPort(account.getPort());
		mailSender.setUsername(account.getUsername());
		mailSender.setPassword(account.getPassword());
		mailSender.setProtocol("smtp");
		if (account.getEnconding() != null && !account.getEnconding().isEmpty()) {
			mailSender.setDefaultEncoding(account.getEnconding());
		}

		Properties jmp = new Properties();
		jmp.setProperty("mail.smtp.auth", String.valueOf(account.isLoginRequired()));
		jmp.setProperty("mail.smtp.from", account.getFromAddress());
		jmp.setProperty("mail.smtp.port", String.valueOf(account.getPort()));
		jmp.setProperty("mail.smtp.starttls.enable", String.valueOf(account.isUseTTLS()));
		jmp.setProperty("mail.smtp.host", account.getServerAddress());
		jmp.setProperty("mail.from", account.getFromAddress());
		jmp.setProperty("mail.personal", account.getName());

		mailSender.setJavaMailProperties(jmp);

		return mailSender;

	}

	public void processTemplate(EmailMessage message) {
		if (velocityEngine == null) {
			throw new EmailServiceException("There is not a VelocityEngine configured to process any template");
		}

		if (message.getTemplate() == null) {
			throw new EmailServiceException(message + " has no template to process");
		}

		StringWriter contentWriter = new StringWriter();
		StringWriter subjectWriter = new StringWriter();
		EmailTemplate template = message.getTemplate();
		VelocityContext context = new VelocityContext();
		if (message.getTemplateModel() != null) {
			for (Entry<String, Object> entry : message.getTemplateModel().entrySet()) {
				context.put(entry.getKey(), entry.getValue());
			}
		}

		velocityEngine.evaluate(context, subjectWriter, "log", template.getSubject());
		velocityEngine.evaluate(context, contentWriter, "log", template.getContent());
		message.setSubject(subjectWriter.toString());
		message.setContent(contentWriter.toString());
	}

	private void fireOnMailSending(EmailMessage message) {
		Collection<EmailServiceListener> listeners = Containers.get().findObjects(EmailServiceListener.class);
		for (EmailServiceListener listener : listeners) {
			listener.onMailSending(message);
		}
	}

	private void fireOnMailSended(EmailMessage message) {
		Collection<EmailServiceListener> listeners = Containers.get().findObjects(EmailServiceListener.class);
		for (EmailServiceListener listener : listeners) {
			listener.onMailSended(message);
		}
	}

	private void fireOnMailSendFail(EmailMessage message, Throwable cause) {
		Collection<EmailServiceListener> listeners = Containers.get().findObjects(EmailServiceListener.class);
		for (EmailServiceListener listener : listeners) {
			listener.onMailSendFail(message, cause);
		}
	}
}
