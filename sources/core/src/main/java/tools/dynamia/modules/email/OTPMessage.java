package tools.dynamia.modules.email;

import tools.dynamia.commons.StringUtils;

import java.io.Serializable;
import java.util.Random;
import java.util.function.Function;

/**
 * Simple OTP (One-Time Password) message
 */
public class OTPMessage implements Serializable {

    public static OTPMessage sms(String targetPhoneNumber, OTPType type, Function<String, String> contentGenerator) {
        var msg = new OTPMessage();
        msg.sendSMS = true;
        msg.targetPhoneNumber = targetPhoneNumber;
        if (type == OTPType.NUMERIC) {
            msg.generateNumericCode();
        } else {
            msg.generateTextCode();
        }
        msg.content = contentGenerator.apply(msg.otp);
        return msg;
    }

    public static OTPMessage email(String targetEmail, OTPType type, String emailSubject, Function<String, String> contentGenerator) {
        var msg = new OTPMessage();
        msg.sendEmail = true;
        msg.targetEmail = targetEmail;
        if (type == OTPType.NUMERIC) {
            msg.generateNumericCode();
        } else {
            msg.generateTextCode();
        }
        msg.emailSubject = emailSubject;
        msg.content = contentGenerator.apply(msg.otp);
        return msg;
    }

    private String otp;
    private String targetEmail;
    private String targetPhoneNumber;
    private boolean sendEmail;
    private boolean sendSMS;
    private String content;
    private String emailSubject;
    private Long accountId;


    public OTPMessage() {
    }


    /**
     * Generate 6 length numeric code using random integer
     */
    public void generateNumericCode() {
        int number = 10000 + new Random().nextInt(90000);
        otp = String.valueOf(number);
    }

    /**
     * Generate a 6 length text code using a random string
     */
    public void generateTextCode() {
        otp = StringUtils.randomString().substring(0, 6).toUpperCase();
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getTargetEmail() {
        return targetEmail;
    }

    public void setTargetEmail(String targetEmail) {
        this.targetEmail = targetEmail;
    }

    public String getTargetPhoneNumber() {
        return targetPhoneNumber;
    }

    public void setTargetPhoneNumber(String targetPhoneNumber) {
        this.targetPhoneNumber = targetPhoneNumber;
    }

    public boolean isSendEmail() {
        return sendEmail;
    }

    public void setSendEmail(boolean sendEmail) {
        this.sendEmail = sendEmail;
    }

    public boolean isSendSMS() {
        return sendSMS;
    }

    public void setSendSMS(boolean sendSMS) {
        this.sendSMS = sendSMS;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getEmailSubject() {
        return emailSubject;
    }

    public void setEmailSubject(String emailSubject) {
        this.emailSubject = emailSubject;
    }


    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public OTPMessage withAccountId(Long accountId) {
        this.accountId = accountId;
        return this;
    }

    public enum OTPType {
        NUMERIC, TEXT
    }


}
