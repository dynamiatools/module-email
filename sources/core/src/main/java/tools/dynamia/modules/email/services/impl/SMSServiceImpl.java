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

package tools.dynamia.modules.email.services.impl;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNSAsync;
import com.amazonaws.services.sns.AmazonSNSAsyncClientBuilder;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import org.springframework.stereotype.Service;
import tools.dynamia.domain.services.AbstractService;
import tools.dynamia.integration.Containers;
import tools.dynamia.modules.email.SMSMessage;
import tools.dynamia.modules.email.SMSServiceListener;
import tools.dynamia.modules.email.domain.SMSMessageLog;
import tools.dynamia.modules.email.services.SMSService;

import java.util.HashMap;
import java.util.Map;

@Service
public class SMSServiceImpl extends AbstractService implements SMSService {

    @Override
    public String send(SMSMessage message) {
        validate(message);

        AWSCredentialsProvider credentials = new AWSStaticCredentialsProvider(new BasicAWSCredentials(message.getUsername(), message.getPassword()));

        AmazonSNSAsync snsClient = AmazonSNSAsyncClientBuilder.standard()
                .withCredentials(credentials)
                .withRegion(message.getRegion())
                .build();


        Map<String, MessageAttributeValue> smsAttributes = new HashMap<>();
        if (message.isTransactional()) {
            smsAttributes.put("AWS.SNS.SMS.SMSType", new MessageAttributeValue()
                    .withStringValue("Transactional")
                    .withDataType("String"));
        }

        if (message.getSenderID() != null && !message.getSenderID().isEmpty()) {
            smsAttributes.put("AWS.SNS.SMS.SenderID", new MessageAttributeValue()
                    .withStringValue(message.getSenderID()) //The sender ID shown on the device.
                    .withDataType("String"));
        }


        log("Sending SMS to " + message.getPhoneNumber());
        fireSendingListener(message);
        PublishResult result = snsClient.publish(new PublishRequest()
                .withMessage(message.getText())
                .withPhoneNumber(message.getPhoneNumber())
                .withMessageAttributes(smsAttributes));

        new SMSMessageLog(message.getPhoneNumber(),
                message.getText(), result.getMessageId(),
                message.getAccountId())
                .save();

        message.setResult(result.getMessageId());
        message.setSended(true);
        log("SMS Sended - " + message.getPhoneNumber() + "  message id: " + message.getResult());
        fireSendedListener(message);
        return message.getResult();
    }

    private void fireSendingListener(SMSMessage smsMessage) {
        Containers.get().findObjects(SMSServiceListener.class).forEach(l -> l.onMessageSending(smsMessage));
    }


    private void fireSendedListener(SMSMessage smsMessage) {
        Containers.get().findObjects(SMSServiceListener.class).forEach(l -> l.onMessageSended(smsMessage));
    }
}
