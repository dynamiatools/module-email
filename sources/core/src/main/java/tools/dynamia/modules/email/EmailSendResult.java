package tools.dynamia.modules.email;

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
