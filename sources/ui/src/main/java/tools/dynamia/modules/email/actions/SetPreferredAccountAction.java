
package tools.dynamia.modules.email.actions;

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

import org.springframework.beans.factory.annotation.Autowired;

import tools.dynamia.actions.InstallAction;
import tools.dynamia.commons.ApplicableClass;
import tools.dynamia.crud.AbstractCrudAction;
import tools.dynamia.crud.CrudActionEvent;
import tools.dynamia.crud.CrudState;
import tools.dynamia.modules.email.domain.EmailAccount;
import tools.dynamia.modules.email.services.EmailService;
import tools.dynamia.ui.MessageType;
import tools.dynamia.ui.UIMessages;

/**
 *
 * @author Mario Serrano Leones
 */
@InstallAction
public class SetPreferredAccountAction extends AbstractCrudAction {

	@Autowired
	private EmailService service;

	public SetPreferredAccountAction() {
		setMenuSupported(true);
		setName("Set as preferred email account");
		setImage("star");

	}

	@Override
	public void actionPerformed(CrudActionEvent evt) {
		EmailAccount account = (EmailAccount) evt.getData();
		if (account != null) {
			service.setPreferredEmailAccount(account);
			evt.getController().doQuery();
			UIMessages.showMessage("Account " + account + " set as preferred successfully");
		} else {
			UIMessages.showMessage("Select account", MessageType.WARNING);
		}
	}

	@Override
	public CrudState[] getApplicableStates() {
		return CrudState.get(CrudState.READ);
	}

	@Override
	public ApplicableClass[] getApplicableClasses() {
		return ApplicableClass.get(EmailAccount.class);
	}

}
