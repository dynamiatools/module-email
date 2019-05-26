package tools.dynamia.modules.email;
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

/**
 * Email sending process result.
 */
public class EmailSendResult {

    private EmailMessage message;
    private boolean sended;
    private Exception exception;
    private String cause;

    public EmailSendResult(EmailMessage message, boolean sended, String reason) {
        this.message = message;
        this.sended = sended;
        this.cause = reason;
    }

    public EmailSendResult(EmailMessage message, Exception exception) {
        this.message = message;
        this.sended = false;
        this.exception = exception;
        this.cause = exception.getMessage();
    }

    public Exception getException() {
        return exception;
    }


    public EmailMessage getMessage() {
        return message;
    }

    public boolean isSended() {
        return sended;
    }

    public String getCause() {
        return cause;
    }
}
