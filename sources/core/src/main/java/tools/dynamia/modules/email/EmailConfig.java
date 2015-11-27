package tools.dynamia.modules.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tools.dynamia.integration.ms.MessageChannel;
import tools.dynamia.integration.ms.MessageService;

@Configuration
public class EmailConfig {

	public static final String EMAIL_CHANNEL = "emailChannel";
	
	@Autowired
	private MessageService messageService;

	@Bean
	public MessageChannel emailChannel() {
		return messageService.createChannel(EMAIL_CHANNEL, null);
	}

}
