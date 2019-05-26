
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

import javax.persistence.Entity;
import javax.persistence.Table;

import tools.dynamia.domain.contraints.NotEmpty;
import tools.dynamia.modules.saas.jpa.SimpleEntitySaaS;

/**
 *
 * @author Mario Serrano Leones
 */
@Entity
@Table(name = "email_accounts")
public class EmailAccount extends SimpleEntitySaaS {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3769420109733883374L;
	@NotEmpty(message = "Enter account's name")
	private String name;
	@NotEmpty(message = "Enter account's username")
	private String username;
	private String password;
	@NotEmpty(message = "Enter server host name or ip address")
	private String serverAddress;
	private String fromAddress;
	private int port = 25;
	private boolean useTTLS;
	private boolean loginRequired;
	private boolean preferred;
	private String enconding;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isUseTTLS() {
		return useTTLS;
	}

	public void setUseTTLS(boolean useTTLS) {
		this.useTTLS = useTTLS;
	}

	public boolean isLoginRequired() {
		return loginRequired;
	}

	public void setLoginRequired(boolean loginRequired) {
		this.loginRequired = loginRequired;
	}

	public boolean isPreferred() {
		return preferred;
	}

	public void setPreferred(boolean preferred) {
		this.preferred = preferred;
	}

	public String getEnconding() {
		return enconding;
	}

	public void setEnconding(String enconding) {
		this.enconding = enconding;
	}

	@Override
	public String toString() {
		return String.format("%s (%s)", name, fromAddress);
	}

}
