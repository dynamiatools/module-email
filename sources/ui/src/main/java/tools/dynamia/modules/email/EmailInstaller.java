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

import org.springframework.stereotype.Component;

import tools.dynamia.crud.CrudPage;
import tools.dynamia.modules.email.domain.EmailAccount;
import tools.dynamia.modules.email.domain.EmailAddress;
import tools.dynamia.modules.email.domain.EmailTemplate;
import tools.dynamia.navigation.Module;
import tools.dynamia.navigation.ModuleProvider;
import tools.dynamia.navigation.PageGroup;

@Component("EmailModule")
public class EmailInstaller implements ModuleProvider {


    @Override
    public Module getModule() {
        Module email = Module.getRef("system");

        PageGroup group = new PageGroup("email", "Email");
        group.addPage(new CrudPage("accounts", "Accounts", EmailAccount.class));
        group.addPage(new CrudPage("templates", "Templates", EmailTemplate.class));
        group.addPage(new CrudPage("addresses", "Email Addresses", EmailAddress.class));

        email.addPageGroup(group);
        return email;
    }

}
