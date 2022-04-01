package xacmlgen.enums;

public enum Functions {
	EQUAL("urn:oasis:names:tc:xacml:1.0:function:function:string-equal"),
	GREATER_THAN("urn:oasis:names:tc:xacml:1.0:function:string-greater-than"),
	GREATER_EQUAL_THAN("urn:oasis:names:tc:xacml:1.0:function:string-greater-equal-than"),
	LESS_THAN("urn:oasis:names:tc:xacml:1.0:function:string-less-than"),
	LESS_EQUAL_THAN("urn:oasis:names:tc:xacml:1.0:function:string-less-equal-than");

	private final String value;

	Functions(String value) {
		this.value = value;
	}

	public String value() {
		return value;
	}
}
