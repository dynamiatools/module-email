
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

package tools.dynamia.modules.email.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import tools.dynamia.domain.contraints.NotEmpty;
import tools.dynamia.modules.saas.jpa.SimpleEntitySaaS;

/**
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

    private boolean smsEnabled;
    private String smsUsername;
    private String smsPassword;
    private String smsRegion;
    private String smsDefaultPrefix;
    private String smsSenderID;

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

    public String getSmsUsername() {
        return smsUsername;
    }

    public void setSmsUsername(String smsUsername) {
        this.smsUsername = smsUsername;
    }

    public String getSmsPassword() {
        return smsPassword;
    }

    public void setSmsPassword(String smsPassword) {
        this.smsPassword = smsPassword;
    }

    public boolean isSmsEnabled() {
        return smsEnabled;
    }

    public void setSmsEnabled(boolean smsEnabled) {
        this.smsEnabled = smsEnabled;
    }

    public String getSmsDefaultPrefix() {
        return smsDefaultPrefix;
    }

    public void setSmsDefaultPrefix(String smsDefaultPrefix) {
        this.smsDefaultPrefix = smsDefaultPrefix;
    }

    public String getSmsRegion() {
        return smsRegion;
    }

    public void setSmsRegion(String smsRegion) {
        this.smsRegion = smsRegion;
    }

    public String getSmsSenderID() {
        return smsSenderID;
    }

    public void setSmsSenderID(String smsSenderID) {
        this.smsSenderID = smsSenderID;
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", name, fromAddress);
    }


}
