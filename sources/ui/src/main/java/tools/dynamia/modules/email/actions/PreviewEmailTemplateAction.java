package tools.dynamia.modules.email.actions;

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
