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

import org.zkoss.util.media.AMedia;
import org.zkoss.zul.Iframe;
import tools.dynamia.actions.InstallAction;
import tools.dynamia.crud.AbstractCrudAction;
import tools.dynamia.crud.CrudActionEvent;
import tools.dynamia.crud.CrudState;
import tools.dynamia.modules.email.domain.EmailTemplate;
import tools.dynamia.ui.MessageType;
import tools.dynamia.ui.UIMessages;
import tools.dynamia.zk.util.ZKUtil;

@InstallAction
public class PreviewEmailTemplateAction extends AbstractCrudAction {

    public PreviewEmailTemplateAction() {
        setName("Preview");
        setImage("eye");
        setApplicableClass(EmailTemplate.class);
        setMenuSupported(true);
        setApplicableStates(CrudState.get(CrudState.READ, CrudState.UPDATE, CrudState.CREATE));
    }

    @Override
    public void actionPerformed(CrudActionEvent evt) {
        EmailTemplate template = (EmailTemplate) evt.getData();
        if (template != null) {
            Iframe iframe = new Iframe();
            iframe.setContent(new AMedia(template.getName() + ".html", "html", "text/html", template.getContent()));
            iframe.setVflex("1");
            iframe.setHflex("1");
            ZKUtil.showDialog(template.getSubject(), iframe, "100%", "100%");
        } else {
            UIMessages.showMessage("Select template", MessageType.ERROR);
        }
    }
}
