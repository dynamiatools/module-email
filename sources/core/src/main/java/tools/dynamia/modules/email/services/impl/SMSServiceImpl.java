/*
 * Copyright (C) 2021 Dynamia Soluciones IT S.A.S - NIT 900302344-1
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

        AmazonSNSAsync snsClient = buildSNSClient(message);


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
        message.setMessageId(result.getMessageId());
        message.setSended(true);
        log("SMS Sended - " + message.getPhoneNumber() + "  message id: " + message.getResult());
        fireSendedListener(message);
        return message.getResult();
    }

    private AmazonSNSAsync buildSNSClient(SMSMessage message) {
        AWSCredentialsProvider credentials = new AWSStaticCredentialsProvider(new BasicAWSCredentials(message.getUsername(), message.getPassword()));

        return AmazonSNSAsyncClientBuilder.standard()
                .withCredentials(credentials)
                .withRegion(message.getRegion())
                .build();
    }

    private void fireSendingListener(SMSMessage smsMessage) {
        Containers.get().findObjects(SMSServiceListener.class).forEach(l -> l.onMessageSending(smsMessage));
    }


    private void fireSendedListener(SMSMessage smsMessage) {
        Containers.get().findObjects(SMSServiceListener.class).forEach(l -> l.onMessageSended(smsMessage));
    }
}
