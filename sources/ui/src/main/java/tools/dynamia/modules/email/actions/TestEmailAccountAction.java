
/*
 * Copyright (C)  2020. Dynamia Soluciones IT S.A.S - NIT 900302344-1
 * Colombia - South America
 *  All Rights Reserved.
 *
 * This file is free software: you can redistribute it and/or modify it  under the terms of the
 *  GNU Lesser General Public License (LGPL v3) as published by the Free Software Foundation,
 *   either version 3 of the License, or (at your option) any later version.
 *
 *  This file is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *   without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *   See the GNU Lesser General Public License for more details. You should have received a copy of the
 *   GNU Lesser General Public License along with this file.
 *   If not, see <https://www.gnu.org/licenses/>.
 *
 */

package tools.dynamia.modules.email.actions;

import org.springframework.beans.factory.annotation.Autowired;
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
import tools.dynamia.integration.ProgressMonitor;
import tools.dynamia.modules.email.EmailMessage;
import tools.dynamia.modules.email.EmailSendResult;
import tools.dynamia.modules.email.SMSMessage;
import tools.dynamia.modules.email.domain.EmailAccount;
import tools.dynamia.modules.email.services.EmailService;
import tools.dynamia.modules.email.services.SMSService;
import tools.dynamia.ui.MessageType;
import tools.dynamia.ui.UIMessages;
import tools.dynamia.viewers.ViewDescriptor;
import tools.dynamia.zk.actions.ToolbarbuttonActionRenderer;
import tools.dynamia.zk.ui.LongOperationMonitorWindow;
import tools.dynamia.zk.util.LongOperation;
import tools.dynamia.zk.util.ZKUtil;
import tools.dynamia.zk.viewers.ui.Viewer;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static tools.dynamia.viewers.ViewDescriptorBuilder.field;
import static tools.dynamia.viewers.ViewDescriptorBuilder.viewDescriptor;

/**
 * @author Mario Serrano Leones
 */
@InstallAction
public class TestEmailAccountAction extends AbstractCrudAction {

    @Autowired
    private EmailService service;

    public TestEmailAccountAction() {
        setName("Test Account");
        setDescription("Send a test message using this email account");
        setImage("mail");
        setMenuSupported(true);

    }

    @Override
    public void actionPerformed(CrudActionEvent evt) {
        SMSService smsService = Containers.get().findObject(SMSService.class);

        EmailAccount account = (EmailAccount) evt.getData();
        SMSMessage sms = new SMSMessage("+573187807221", "Prueba de mensaje");
        sms.setCredentials(account.getSmsUsername(), account.getSmsPassword(), sms.getRegion());

        smsService.send(sms);
        if (account != null) {
            Viewer viewer = createView(account);
            ZKUtil.showDialog("Test Message: " + account.getName(), viewer, "60%", null);
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
        viewer.addAction(new SendTestEmailAction(service));
        viewer.setVflex(null);
        viewer.setContentVflex(null);
        service.clearCache(account);
        return viewer;

    }

    private static class SendTestEmailAction extends AbstractAction {

        private EmailService service;

        public SendTestEmailAction(EmailService service) {
            setName("Send Test");
            setImage("mail");
            this.service = service;
        }

        @Override
        public void actionPerformed(ActionEvent evt) {

            EmailMessage msg = (EmailMessage) evt.getData();
            if (msg != null) {
                if (msg.getTo() != null && !msg.getTo().isEmpty()) {
                    try {

                        Future<EmailSendResult> future = service.send(msg);
                        ProgressMonitor monitor = new ProgressMonitor();
                        LongOperation operation = LongOperation.create()
                                .execute(() -> {
                                    while (!future.isDone()) {
                                        try {
                                            //wait 200ms and try again
                                            Thread.sleep(200);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                })
                                .onFinish(() -> {
                                    try {
                                        EmailSendResult result = future.get();
                                        String exception = "";
                                        if (result.getException() != null && result.getException().getCause() != null) {
                                            exception = result.getException().getCause().getClass() + ". " + result.getException().getCause().getMessage();
                                        }
                                        Messagebox.show("Sended? " + result.isSended() + ". " + result.getCause() + ". " + exception);
                                    } catch (InterruptedException | ExecutionException e) {
                                        e.printStackTrace();
                                    }
                                })
                                .onException(ex -> UIMessages.showMessage(ex.getMessage(), MessageType.ERROR))
                                .onCancel(() -> future.cancel(true))
                                .start();
                        LongOperationMonitorWindow window = new LongOperationMonitorWindow(operation, monitor);
                        window.setTitle("Sending Test Email: " + msg.getTo());
                        window.doModal();
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
