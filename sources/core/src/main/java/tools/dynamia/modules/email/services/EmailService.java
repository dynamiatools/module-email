
package tools.dynamia.modules.email.services;

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

import org.springframework.transaction.annotation.Transactional;
import tools.dynamia.modules.email.EmailMessage;
import tools.dynamia.modules.email.EmailSendResult;
import tools.dynamia.modules.email.domain.EmailAccount;
import tools.dynamia.modules.email.domain.EmailAddress;
import tools.dynamia.modules.email.domain.EmailTemplate;

import java.util.concurrent.Future;

/**
 * Email service for sending emails
 *
 * @author Mario Serrano Leones
 */
public interface EmailService {

    /**
     * Send email message asynchronously
     *
     * @param message
     */
    Future<EmailSendResult> send(EmailMessage message);

    /**
     * Build and send email message asynchronously
     *
     * @param to
     * @param subject
     * @param content
     */
    Future<EmailSendResult> send(String to, String subject, String content);

    /**
     * Setup preferred email account in current SaaS account
     *
     * @param account
     */
    void setPreferredEmailAccount(EmailAccount account);

    /**
     * Get preferred email account in current SaaS account
     *
     * @return
     */
    EmailAccount getPreferredEmailAccount();

    /**
     * Find email template by name in current SaaS account. If autocreate is true a new blank email template is created
     *
     * @param name
     * @param autocreate
     * @return
     */
    EmailTemplate getTemplateByName(String name, boolean autocreate);

    /**
     * Find email template by name
     *
     * @param name
     * @return
     */
    EmailTemplate getTemplateByName(String name);

    /**
     * Log all email address from message
     *
     * @param message
     */
    void logEmailAddress(EmailMessage message);

    /**
     * Log email address
     *
     * @param address
     * @param tag
     */
    void logEmailAddress(String address, String tag);

    /**
     * Find a logged email address
     *
     * @param address
     * @return
     */
    EmailAddress getEmailAddress(String address);
}
