/*
 * Copyright (C)  2020. Dynamia Soluciones IT S.A.S - NIT 900302344-1 All Rights Reserved.
 * Colombia - South America
 *
 * This file is free software: you can redistribute it and/or modify it  under the terms of the
 *  GNU Lesser General Public License (LGPL v3) as published by the Free Software Foundation,
 *   either version 3 of the License, or (at your option) any later version.
 *
 *  This file is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *   without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *   See the GNU Lesser General Public License for more details. You should have received a copy of the
 *   GNU Lesser General Public License along with this file.
 *   If not, see <https://www.gnu.org/licenses/>.
 *
 */

package tools.dynamia.modules.email;

import org.springframework.beans.factory.annotation.Autowired;
import tools.dynamia.commons.logger.LoggingService;
import tools.dynamia.commons.logger.SLF4JLoggingService;
import tools.dynamia.domain.ValidationError;
import tools.dynamia.integration.sterotypes.Listener;
import tools.dynamia.modules.email.domain.EmailAccount;
import tools.dynamia.modules.email.domain.EmailTemplate;
import tools.dynamia.modules.email.services.SMSService;
import tools.dynamia.templates.VelocityTemplateEngine;

@Listener
public class AutoSendSMSFromEmailServiceListener implements EmailServiceListener {

    @Autowired
    private SMSService smsService;

    private LoggingService logger = new SLF4JLoggingService(AutoSendSMSFromEmailServiceListener.class);

    @Override
    public void onMailSending(EmailMessage message) {
        //ignore
    }

    @Override
    public void onMailSended(EmailMessage message) {

        if (message.getTemplate() != null && message.getTemplate().isSendSMS() && message.getMailAccount() != null && message.getMailAccount().isSmsEnabled()) {

            EmailTemplate template = message.getTemplate();
            EmailAccount account = message.getMailAccount();

            SMSMessage sms = new SMSMessage();
            sms.setPhoneNumber(parse(message, template.getSmsNumber()));
            sms.setText(parse(message, template.getSmsText()));
            sms.setCredentials(account.getSmsUsername(), account.getSmsPassword(), account.getSmsRegion());
            sms.setSenderID(account.getSmsSenderID());
            sms.setAccountId(account.getAccountId());

            if (account.getSmsDefaultPrefix() != null && !account.getSmsDefaultPrefix().isEmpty()) {
                String prefix = account.getSmsDefaultPrefix();
                if (!sms.getPhoneNumber().startsWith("+") && !sms.getPhoneNumber().startsWith(prefix)) {
                    sms.setPhoneNumber(prefix + sms.getPhoneNumber());
                }
            }
            try {
                if (sms.getUsername() != null && sms.getPassword() != null) {

                    String result = smsService.send(sms);
                    logger.info("SMS Sended - " + result);
                }
            } catch (Exception e) {
                logger.error("Error sending sms message from email " + message + " --> " + e.getMessage());
            }
        }
    }

    private String parse(EmailMessage message, String text) {
        return new VelocityTemplateEngine().evaluate(text, message.getTemplateModel());
    }

    @Override
    public void onMailSendFail(EmailMessage message, Throwable cause) {
//ignore
    }
}
