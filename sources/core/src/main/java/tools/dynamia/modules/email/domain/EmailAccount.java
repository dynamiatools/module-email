/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.dynamia.modules.email.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import tools.dynamia.domain.contraints.NotEmpty;
import tools.dynamia.modules.saas.api.SimpleEntitySaaS;

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
