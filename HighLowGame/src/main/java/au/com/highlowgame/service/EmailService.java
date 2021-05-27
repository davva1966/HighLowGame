package au.com.highlowgame.service;

import org.springframework.stereotype.Service;

@Service("emailService")
public interface EmailService {

	void sendMail(String to, String replyTo, String subject, String text) throws Exception;

}
