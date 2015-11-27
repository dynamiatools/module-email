package tools.dynamia.modules.email;

import tools.dynamia.crud.CrudPage;
import tools.dynamia.integration.sterotypes.Provider;
import tools.dynamia.modules.email.domain.EmailAccount;
import tools.dynamia.modules.email.domain.EmailTemplate;
import tools.dynamia.navigation.Module;
import tools.dynamia.navigation.ModuleProvider;
import tools.dynamia.navigation.PageGroup;

@Provider
public class EmailInstaller implements ModuleProvider {

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
