
package tools.dynamia.modules.email.services.impl;

/*-
 * #%L
 * DynamiaModules - Email
 * %%
 * Copyright (C) 2018 - 2019 Dynamia Soluciones IT SAS
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

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

import tools.dynamia.commons.StringUtils;
import tools.dynamia.commons.logger.LoggingService;
import tools.dynamia.commons.logger.SLF4JLoggingService;
import tools.dynamia.domain.contraints.Email;
import tools.dynamia.domain.contraints.EmailValidator;
import tools.dynamia.domain.query.QueryParameters;
import tools.dynamia.domain.services.CrudService;
import tools.dynamia.domain.util.CrudServiceListenerAdapter;
import tools.dynamia.integration.Containers;
import tools.dynamia.integration.scheduling.SchedulerUtil;
import tools.dynamia.integration.scheduling.Task;
import tools.dynamia.integration.scheduling.TaskWithResult;
import tools.dynamia.modules.email.*;
import tools.dynamia.modules.email.domain.EmailAccount;
import tools.dynamia.modules.email.domain.EmailTemplate;
import tools.dynamia.modules.email.services.EmailService;
import tools.dynamia.modules.saas.api.AccountServiceAPI;

/**
 * @author Mario Serrano Leones
 */
@Service
public class EmailServiceImpl extends CrudServiceListenerAdapter<EmailAccount> implements EmailService {

    private final Map<Long, MailSender> MAIL_SENDERS = new HashMap<>();

    @Autowired
    private CrudService crudService;

    @Autowired
    private AccountServiceAPI accountServiceAPI;

    private VelocityEngine velocityEngine = new VelocityEngine();
    private EmailValidator emailValidator = new EmailValidator();

    private final LoggingService logger = new SLF4JLoggingService(EmailService.class);

    @Override
    public Future<EmailSendResult> send(String to, String subject, String content) {
        return send(new EmailMessage(to, subject, content));
    }

    @Override
    public Future<EmailSendResult> send(final EmailMessage mailMessage) {

        EmailAccount account = mailMessage.getMailAccount();
        if (account == null) {
            account = getPreferredEmailAccount();
        }

        if (account == null) {
            logger.warn("No email account to send " + mailMessage);
            return CompletableFuture.completedFuture(new EmailSendResult(mailMessage, false, "No email account to sende"));
        }

        if (mailMessage.getTemplate() == null && mailMessage.getTemplateName() != null
                && !mailMessage.getTemplateName().isEmpty()) {
            mailMessage.setTemplate(getTemplateByName(mailMessage.getTemplateName()));
        }

        if (mailMessage.getTemplate() != null && !mailMessage.getTemplate().isEnabled()) {
            String msg = "Template " + mailMessage.getTemplate().getName() + " is not Enabled";
            logger.warn(msg);
            return CompletableFuture.completedFuture(new EmailSendResult(mailMessage, false, msg));
        }

        final EmailAccount finalAccount = account;
        return SchedulerUtil.runWithResult(new TaskWithResult<EmailSendResult>() {

            @Override
            public EmailSendResult doWorkWithResult() {

                try {
                    if (mailMessage.getTemplate() != null) {
                        processTemplate(mailMessage);
                    }
                    JavaMailSenderImpl jmsi = (JavaMailSenderImpl) createMailSender(finalAccount);
                    MimeMessage mimeMessage = jmsi.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
                    String[] tosAsArray = valideArrayEmails(mailMessage.getTosAsArray());
                    if (mailMessage.getTo() != null && !mailMessage.getTo().isEmpty()) {
                        helper.setTo(mailMessage.getTo().split(","));
                    } else {
                        if (!mailMessage.getTos().isEmpty())
                            helper.setTo(tosAsArray[0].toString());
                    }

                    if (!mailMessage.getTos().isEmpty()) {
                        helper.setTo(valideArrayEmails(tosAsArray));
                    }
                    String from = finalAccount.getFromAddress();
                    String personal = finalAccount.getName();
                    if (from != null && personal != null) {
                        helper.setFrom(from, personal);
                    }

                    if (!mailMessage.getBccs().isEmpty()) {
                        helper.setBcc(valideArrayEmails(mailMessage.getBccsAsArray()));
                    }

                    if (!mailMessage.getCcs().isEmpty()) {
                        helper.setCc(valideArrayEmails(mailMessage.getCcsAsArray()));
                    }

                    helper.setSubject(mailMessage.getSubject());
                    if (mailMessage.getPlainText() != null && mailMessage.getContent() != null) {
                        helper.setText(mailMessage.getPlainText(), mailMessage.getContent());
                    } else {
                        helper.setText(mailMessage.getContent(), true);
                    }

                    if (mailMessage.getReplyTo() != null && !mailMessage.getReplyTo().isEmpty()) {
                        helper.setReplyTo(mailMessage.getReplyTo());
                    }

                    for (EmailAttachment archivo : mailMessage.getAttachments()) {
                        helper.addAttachment(archivo.getName(), archivo.getFile());
                    }

                    fireOnMailSending(mailMessage);
                    logger.error("Sending e-mail " + mailMessage);
                    jmsi.send(mimeMessage);

                    logger.info("Email sended succesfull!");
                    fireOnMailSended(mailMessage);
                    return new EmailSendResult(mailMessage, true, "ok");
                } catch (Exception me) {
                    logger.error("Error sending e-mail " + mailMessage, me);
                    fireOnMailSendFail(mailMessage, me);
                    return new EmailSendResult(mailMessage, new EmailServiceException("Error sending mail message " + mailMessage, me));
                }
            }
        });
    }

    private String[] valideArrayEmails(String[] bccsAsArray) {
        String[] array = Arrays.asList(bccsAsArray).stream().flatMap(e -> Arrays.stream(e.split(",")))
                .map(e -> e.trim()).filter(e -> emailValidator.isValid(e, null)).toArray(String[]::new);
        return array;
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
    public EmailTemplate getTemplateByName(String name, boolean autocreate) {
        EmailTemplate template = crudService.findSingle(EmailTemplate.class, "name", name);
        if (template == null) {
            logger.warn("There is not a template with name " + name + ", trying to get System Account template ");
            Long systemAccountId = accountServiceAPI.getSystemAccountId();
            if (systemAccountId != null) {
                template = crudService.findSingle(EmailTemplate.class,
                        QueryParameters.with("accountId", systemAccountId).add("name", name));
            }
        }

        if (template == null && autocreate) {
            template = new EmailTemplate();
            template.setName(name);
            template.setAccountId(accountServiceAPI.getCurrentAccountId());
            template.setEnabled(false);
            template.setContent("<empty>");
            template.setSubject(name);
            template.setDescription("autocreated template");
            template = crudService.create(template);
        }

        return template;
    }

    @Override
    public EmailTemplate getTemplateByName(String name) {
        return getTemplateByName(name, true);
    }

    private MailSender createMailSender(EmailAccount account) {
        JavaMailSenderImpl mailSender = (JavaMailSenderImpl) MAIL_SENDERS.get(account.getId());
        if (mailSender == null) {
            mailSender = new JavaMailSenderImpl();
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

            MAIL_SENDERS.put(account.getId(), mailSender);
        }

        return mailSender;

    }

    public void processTemplate(EmailMessage message) {
        if (velocityEngine == null) {
            throw new EmailServiceException("There is not a VelocityEngine configured to process any template");
        }

        if (message.getTemplate() == null) {
            throw new EmailServiceException(message + " has no template to process");
        }

        EmailTemplate template = message.getTemplate();
        VelocityContext context = new VelocityContext();

        // Load model from providers
        if (message.getSource() != null && !message.getSource().isEmpty()) {
            Containers.get().findObjects(EmailTemplateModelProvider.class, object -> object.equals(message.getSource()))
                    .forEach(p -> {
                        Map<String, Object> model = p.getModel(message);
                        if (model != null) {
                            for (Entry<String, Object> entry : model.entrySet()) {
                                context.put(entry.getKey(), entry.getValue());
                            }
                        }
                    });
            ;
        }

        // Load message models, can override providers models
        if (message.getTemplateModel() != null) {
            for (Entry<String, Object> entry : message.getTemplateModel().entrySet()) {
                context.put(entry.getKey(), entry.getValue());
            }
        }

        message.setSubject(parse(template.getSubject(), context));

        String content = parse(template.getContent(), context);
        if (template.getParent() != null) {
            context.put("TEMPLATE_CONTENT", content);
            content = parse(template.getParent().getContent(), context);
        }

        message.setContent(content);

        String[] tos = parseDestination(template.getTo(), context);
        if (tos != null) {
            for (String to : tos) {
                if (to != null && !to.isEmpty()) {
                    message.addTo(to);
                }
            }
        }

        String[] ccs = parseDestination(template.getCc(), context);
        if (ccs != null) {
            for (String cc : ccs) {
                if (cc != null && !cc.isEmpty()) {
                    message.addCc(cc);
                }
            }
        }

        String[] bccs = parseDestination(template.getBcc(), context);
        if (bccs != null) {
            for (String bcc : bccs) {
                if (bcc != null && !bcc.isEmpty()) {
                    message.addBcc(bcc);
                }
            }
        }
    }

    private String parse(String templateString, VelocityContext context) {
        StringWriter writer = new StringWriter();
        velocityEngine.evaluate(context, writer, "log", templateString);
        return writer.toString();
    }

    private String[] parseDestination(String destination, VelocityContext context) {
        if (destination != null && !destination.isEmpty()) {
            destination = parse(destination, context);
            if (destination.contains(",")) {
                return StringUtils.split(destination, ",");
            } else {
                return new String[]{destination};
            }
        }
        return null;
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

    @Override
    public void afterUpdate(EmailAccount entity) {
        if (entity != null) {
            MAIL_SENDERS.remove(entity.getId());
        }
    }
}
