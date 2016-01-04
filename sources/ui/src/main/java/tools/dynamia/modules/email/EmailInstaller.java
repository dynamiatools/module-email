package tools.dynamia.modules.email;

import org.springframework.stereotype.Component;

import tools.dynamia.crud.CrudPage;
import tools.dynamia.modules.email.domain.EmailAccount;
import tools.dynamia.modules.email.domain.EmailTemplate;
import tools.dynamia.navigation.Module;
import tools.dynamia.navigation.ModuleProvider;
import tools.dynamia.navigation.PageGroup;

@Component("EmailModule")
public class EmailInstaller implements ModuleProvider {

	public EmailInstaller() {
		System.err.println("STARTING EMAIL MODULE INSTALLER");
	}

	@Override
	public Module getModule() {
		Module email = Module.getRef("system");

		PageGroup group = new PageGroup("email", "Email");
		group.addPage(new CrudPage("accounts", "Cuentas", EmailAccount.class));
		group.addPage(new CrudPage("templates", "Plantillas", EmailTemplate.class));

		email.addPageGroup(group);
		return email;
	}

}
