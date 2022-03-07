package tools.dynamia.modules.email.services;

import tools.dynamia.modules.email.OTPMessage;
import tools.dynamia.modules.email.OTPSendResult;

import java.util.concurrent.Future;

public interface OTPService {


    /**
     * Send OTP message using email, sms or both service
     *
     * @param message
     * @return
     */
    Future<OTPSendResult> send(OTPMessage message);

}
