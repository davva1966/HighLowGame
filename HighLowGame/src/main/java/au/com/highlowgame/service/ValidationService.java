package au.com.highlowgame.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import au.com.highlowgame.model.DomainEntity;

@Service("validationService")
public interface ValidationService {

	public static final String ALREADYEXIST = "alreadyexist";

	public class ValidationResult {

		protected List<String> messages;

		public ValidationResult() {
			super();
		}

		public boolean hasError() {
			return messages != null && messages.size() > 0;
		}

		public List<String> getMessages() {
			return messages;
		}

		public void addMessage(String message) {
			if (messages == null)
				messages = new ArrayList<String>();

			messages.add(message);
		}

		public String getMessageString() {
			if (!hasError())
				return null;

			StringBuilder sb = new StringBuilder();
			for (String message : messages) {
				sb.append(message + "; ");
			}

			return sb.toString();
		}

	}
	
	void rejectIfNotUnique(DomainEntity entity, String property, Errors e);

	void rejectIfNotUnique(DomainEntity entity, String property, String messageField, Errors e);

	void rejectIfNotUnique(DomainEntity entity, String identifyingProperty, String[] uniqueProperties, Errors e);

	void rejectIfNotUnique(DomainEntity entity, String identifyingProperty, String messageField, String[] uniqueProperties, Errors e);

	boolean verifyUnique(DomainEntity entity, String[] uniqueProperties);

	void rejectIfEmptyOrWhitespace(String property, Errors e);

	ValidationResult validate(Object object);

}
