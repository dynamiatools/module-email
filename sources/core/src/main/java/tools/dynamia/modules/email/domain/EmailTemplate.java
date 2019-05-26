package tools.dynamia.modules.email.domain;

/*-
 * #%L
 * DynamiaModules - Email
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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import tools.dynamia.domain.contraints.NotEmpty;
import tools.dynamia.modules.saas.jpa.SimpleEntitySaaS;

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
	@Column(length = 2000, name = "templateTo")
	private String to;
	@Column(length = 2000, name = "templateCc")
	private String cc;
	@Column(length = 2000, name = "templateBcc")
	private String bcc;
	@OneToOne
	private EmailTemplate parent;
	private boolean sendSMS;
	private String smsNumber;
	private String smsText;

	public EmailTemplate getParent() {
		return parent;
	}

	public void setParent(EmailTemplate parent) {
		this.parent = parent;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getCc() {
		return cc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

	public String getBcc() {
		return bcc;
	}

	public void setBcc(String bcc) {
		this.bcc = bcc;
	}

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

	public String getSmsText() {
		return smsText;
	}

	public void setSmsText(String smsText) {
		this.smsText = smsText;
	}

	public String getSmsNumber() {
		return smsNumber;
	}

	public void setSmsNumber(String smsNumber) {
		this.smsNumber = smsNumber;
	}

	public boolean isSendSMS() {
		return sendSMS;
	}

	public void setSendSMS(boolean sendSMS) {
		this.sendSMS = sendSMS;
	}
}
