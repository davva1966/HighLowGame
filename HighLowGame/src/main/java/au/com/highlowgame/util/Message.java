package au.com.highlowgame.util;

public enum Message {

	OK(0, "message_ok"),
	INVALID_TENANT(1, "message_invalid_tenant"),
	INVALID_USER(4, "message_invalid_player"),
	USER_DISABLED(5, "message_player_is_disabled"),
	INVALID_PASSWORD(7, "message_invalid_password"),
	INVALID_CREDENTIALS(8, "message_invalid_credentials"),
	INVALID_DATE(9, "message_invalid_date"),
	NOT_AUTHORIZED(10, "message_you_dont_have_access_to_the_selected_function"),
	APPLICATION_NOT_FOUND(11, "message_application_not_found"),
	NO_ACTIVE_TRIAL_PRODUCT_FOUND(12, "message_no_active_trial_product_found"),
	INVALID_REQUEST(13, "message_invalid_request"),
	PRODUCT_NOT_FOUND(14, "message_product_not_found"),
	TENANT_CODE_IN_USE(15, "message_tenant_code_in_use"),
	TENANT_EMAIL_IN_USE(16, "message_tenant_email_in_use"),
	TENANT_NOT_CREATED(17, "message_tenant_not_created"),
	NO_COUNTRY_INSTANCE_FOUND(18, "message_no_instance_found_for_country"),
	INVALID_TENANT_EMAIL(19, "message_invalid_tenant_email"),
	INVALID_TENANT_CODE(20, "message_invalid_tenant_code"),
	DATA_CONCURRENCY_FAILURE(901, "message_entity_was_updated_by_someone_else"),
	DATA_INTEGRITY_VIOLATION(902, "message_entity_is_still_referenced_from_another_entity_and_can_not_be_deleted"),
	INTERNAL_ERROR(999, "message_internal_server_error");

	public final int code;
	public String message;
	public String[] arguments;

	Message(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return this.code;
	}

	public String getMessage() {
		if (message != null) {
			if (getArguments() != null && getArguments().length > 0)
				return AppUtil.translate(message, (Object[]) getArguments());
			else
				return AppUtil.translate(message);
		}
		return this.message;
	}

	public String[] getArguments() {
		return this.arguments;
	}

	public String toJson() {
		return toJson(new String[0]);
	}

	public String toJson(String... args) {
		if (args != null && args.length > 0)
			this.arguments = args;
		return new JSONMessage(getMessage(), getArguments(), getCode()).toJson();
	}

	public byte[] toJsonBytes() {
		return new JSONMessage(getMessage(), getArguments(), getCode()).toJsonBytes();
	}
}
