package tools.dynamia.modules.email;

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
