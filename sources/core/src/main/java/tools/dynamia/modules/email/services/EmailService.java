
/*
 * Copyright (C) 2023 Dynamia Soluciones IT S.A.S - NIT 900302344-1
 * Colombia / South America
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tools.dynamia.modules.email.services;

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
     * Send email message asynchronously. Default implementation use Spring Scheduling and Async API. Make sure your
     * application has {@link org.springframework.scheduling.annotation.EnableAsync} and
     * {@link org.springframework.scheduling.annotation.EnableScheduling} configured.
     *
     * @param message
     */
    Future<EmailSendResult> send(EmailMessage message);

    /**
     * Build and send email message asynchronously. See {@link EmailService#send(EmailMessage)}
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

    EmailSendResult sendAndWait(EmailMessage mailMessage);

    /**
     * Get preferred email account in current SaaS account
     *
     * @return
     */
    EmailAccount getPreferredEmailAccount();

    /**
     * Get preferred email account in SaaS account ID
     *
     * @return
     */
    EmailAccount getPreferredEmailAccount(Long accountId);

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
    void logEmailAddress(EmailAccount account, EmailMessage message);

    /**
     * Log email address
     *
     * @param address
     * @param tag
     */
    void logEmailAddress(EmailAccount account, String address, String tag);

    /**
     * Find a logged email address
     *
     * @param address
     * @return
     */
    EmailAddress getEmailAddress(String address);

    void clearCache(EmailAccount account);
}
