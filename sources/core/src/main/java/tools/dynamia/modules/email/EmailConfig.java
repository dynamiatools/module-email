package tools.dynamia.modules.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tools.dynamia.domain.DefaultEntityReferenceRepository;
import tools.dynamia.domain.EntityReferenceRepository;
import tools.dynamia.integration.ms.MessageChannel;
import tools.dynamia.integration.ms.MessageService;
import tools.dynamia.modules.email.domain.EmailTemplate;

@Configuration
public class EmailConfig {

    public static final String EMAIL_CHANNEL = "emailChannel";

    @Autowired
    private MessageService messageService;

    @Bean
    public MessageChannel emailChannel() {
        return messageService.createChannel(EMAIL_CHANNEL, null);
    }

    @Bean
    public EntityReferenceRepository<Long> emailTemplateRefRepository() {
        DefaultEntityReferenceRepository<Long> repo = new DefaultEntityReferenceRepository<>(EmailTemplate.class, "name");
        repo.setCacheable(true);
        return repo;
    }

}
