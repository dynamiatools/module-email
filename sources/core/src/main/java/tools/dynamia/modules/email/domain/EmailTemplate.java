package tools.dynamia.modules.email.domain;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.Table;

import tools.dynamia.domain.contraints.NotEmpty;
import tools.dynamia.modules.saas.api.SimpleEntitySaaS;

@Entity
@Table(name = "email_templates")
public class EmailTemplate extends SimpleEntitySaaS {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4639771745251094987L;
	@NotEmpty(message = "Enter template name")
	private String name;
	private String description;
	@NotEmpty(message = "Enter subject")
	private String subject;
	@Lob
	@Basic(fetch = FetchType.LAZY)
	@NotEmpty(message = "Enter template content")
	private String content;
	private boolean enabled = true;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return getName();
	}

}
