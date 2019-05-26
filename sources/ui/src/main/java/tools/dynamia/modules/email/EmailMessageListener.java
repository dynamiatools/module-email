package tools.dynamia.modules.email;

/*-
 * #%L
 * DynamiaModules - Email UI
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

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import tools.dynamia.integration.ms.MapMessage;
import tools.dynamia.integration.ms.Message;
import tools.dynamia.integration.ms.MessageChannelExchange;
import tools.dynamia.integration.ms.MessageEvent;
import tools.dynamia.integration.ms.MessageListener;
import tools.dynamia.integration.sterotypes.Listener;
import tools.dynamia.modules.email.domain.EmailTemplate;
import tools.dynamia.modules.email.services.EmailService;

@Listener
@MessageChannelExchange(channel = EmailConfig.EMAIL_CHANNEL, broadcastReceive = false)
public class EmailMessageListener implements MessageListener<Message> {

	@Autowired
	private EmailService service;

	@Override
	public void onMessage(MessageEvent<Message> evt) {
		Message message = evt.getMessage();

		EmailMessage emailMessage = new EmailMessage();
		emailMessage.setTo((String) message.getHeader("to"));

		if (emailMessage.getTo() != null && !emailMessage.getTo().isEmpty()) {
			emailMessage.setSubject((String) message.getHeader("subject"));
			emailMessage.setContent((String) message.getHeader("content"));
			emailMessage.setTemplate(loadTemplate(message));

			if (message instanceof MapMessage) {
				emailMessage.setTemplateModel((Map<String, Object>) message.getContent());
			}
			service.send(emailMessage);
		}
	}

	private EmailTemplate loadTemplate(Message message) {
		String name = (String) message.getHeader("template");
		if (name != null && !name.isEmpty()) {
			EmailTemplate template = service.getTemplateByName(name);
			return template;
		}
		return null;
	}

}
