package xacmlgen.enums;

public enum Identifiers {
	RESOURCE_CATEGORY("urn:oasis:names:tc:xacml:3.0:attribute-category:resource"),
	RESOURCE_ID("urn:oasis:names:tc:xacml:1.0:resource:resource-id");

	private final String value;

	Identifiers(String value) {
		this.value = value;
	}

	public String value() {
		return value;
	}
}
