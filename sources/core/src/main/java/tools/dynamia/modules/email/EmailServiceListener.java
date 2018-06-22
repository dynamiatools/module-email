/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.dynamia.modules.email;

/**
 *
 * @author Mario Serrano Leones
 */
public interface EmailServiceListener {

	public void onMailSending(EmailMessage message);

	public void onMailSended(EmailMessage message);

	public void onMailSendFail(EmailMessage message, Throwable cause);

}
