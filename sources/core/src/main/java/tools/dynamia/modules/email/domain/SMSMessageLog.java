/*
 * Copyright (C)  2020. Dynamia Soluciones IT S.A.S - NIT 900302344-1 All Rights Reserved.
 * Colombia - South America
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

import tools.dynamia.modules.saas.jpa.BaseEntitySaaS;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "email_sms_log")
public class SMSMessageLog extends BaseEntitySaaS {

    private String phoneNumber;
    private String text;
    private String result;


    public SMSMessageLog(String phoneNumber, String text, String result) {
        this.phoneNumber = phoneNumber;
        this.text = text;
        this.result = result;
    }

    public SMSMessageLog(String phoneNumber, String text, String result, Long accountId) {
        this.phoneNumber = phoneNumber;
        this.text = text;
        this.result = result;
        setAccountId(accountId);
    }
}
