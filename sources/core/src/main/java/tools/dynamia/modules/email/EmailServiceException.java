
package tools.dynamia.modules.email;

/**
 *
 * @author Mario Serrano Leones
 */
public class EmailServiceException extends RuntimeException {

	public EmailServiceException() {
	}

	public EmailServiceException(String message) {
		super(message);
	}

	public EmailServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public EmailServiceException(Throwable cause) {
		super(cause);
	}

	public EmailServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
