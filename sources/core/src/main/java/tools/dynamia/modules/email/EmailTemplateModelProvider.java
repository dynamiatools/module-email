package tools.dynamia.modules.email;

import java.util.Map;

public interface EmailTemplateModelProvider {

	public String getSource();

	public Map<String, Object> getModel(EmailMessage message);

}
