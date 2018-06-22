/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.dynamia.modules.email.actions;

import static tools.dynamia.viewers.ViewDescriptorBuilder.field;
import static tools.dynamia.viewers.ViewDescriptorBuilder.viewDescriptor;

import org.zkoss.zul.Messagebox;

import tools.dynamia.actions.AbstractAction;
import tools.dynamia.actions.ActionEvent;
import tools.dynamia.actions.ActionRenderer;
import tools.dynamia.actions.InstallAction;
import tools.dynamia.commons.ApplicableClass;
import tools.dynamia.crud.AbstractCrudAction;
import tools.dynamia.crud.CrudActionEvent;
import tools.dynamia.crud.CrudState;
import tools.dynamia.integration.Containers;
import tools.dynamia.modules.email.EmailMessage;
import tools.dynamia.modules.email.domain.EmailAccount;
import tools.dynamia.modules.email.services.EmailService;
import tools.dynamia.ui.MessageType;
import tools.dynamia.ui.UIMessages;
import tools.dynamia.viewers.ViewDescriptor;
import tools.dynamia.zk.actions.ToolbarbuttonActionRenderer;
import tools.dynamia.zk.util.ZKUtil;
import tools.dynamia.zk.viewers.ui.Viewer;

/**
 *
 * @author Mario Serrano Leones
 */
@InstallAction
public class TestEmailAccountAction extends AbstractCrudAction {

	public TestEmailAccountAction() {
		setName("Test Account");
		setDescription("Send a test message using this email account");
		setImage("mail");
		setMenuSupported(true);

	}

	@Override
	public void actionPerformed(CrudActionEvent evt) {
		EmailAccount cuenta = (EmailAccount) evt.getData();
		if (cuenta != null) {
			Viewer viewer = createView(cuenta);
			ZKUtil.showDialog("Test Message: " + cuenta.getName(), viewer, "60%", "");
		} else {
			UIMessages.showMessage("Select account to test", MessageType.WARNING);
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

	private Viewer createView(EmailAccount account) {
		ViewDescriptor descriptor = viewDescriptor("form", EmailMessage.class, false)
				.fields(field("to"),
						field("subject"),
						field("content")
								.params("multiline", true, "height", "200px"))
				.layout("columns", 1)
				.build();

		final Viewer viewer = new Viewer(descriptor);
		EmailMessage msg = new EmailMessage();
		msg.setMailAccount(account);
		viewer.setValue(msg);

		viewer.addAction(new SendTestEmailAction());

		return viewer;

	}

	private static class SendTestEmailAction extends AbstractAction {

		public SendTestEmailAction() {
			setName("Send Test");
			setImage("mail");
		}

		@Override
		public void actionPerformed(ActionEvent evt) {
			EmailService service = Containers.get().findObject(EmailService.class);
			EmailMessage msg = (EmailMessage) evt.getData();
			if (msg != null) {
				if (msg.getTo() != null && !msg.getTo().isEmpty()) {
					try {
						service.send(msg);
						UIMessages.showMessage("The email test has been completed, check email inbox");
					} catch (Exception e) {
						Messagebox.show("Error sending test: " + e.getMessage());
						e.printStackTrace();
					}
				} else {
					UIMessages.showMessage("Enter [to] address", MessageType.ERROR);
				}
			}
		}

		@Override
		public ActionRenderer getRenderer() {
			return new ToolbarbuttonActionRenderer(true);
		}

	}

}
