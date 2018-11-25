
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
