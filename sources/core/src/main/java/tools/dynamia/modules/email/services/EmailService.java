/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.dynamia.modules.email.services;

import tools.dynamia.modules.email.EmailMessage;
import tools.dynamia.modules.email.domain.EmailAccount;
import tools.dynamia.modules.email.domain.EmailTemplate;

/**
 *
 * @author ronald
 */
public interface EmailService {

	public void send(EmailMessage message);

	public void send(String to, String subject, String content);

	public void setPreferredEmailAccount(EmailAccount account);

	public EmailAccount getPreferredEmailAccount();

	public EmailTemplate getTemplateByName(String name);

}
