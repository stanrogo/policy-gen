package xacmlgen.enums;

/**
 * We require only strings.
 *
 * @see <a href="http://docs.oasis-open.org/xacml/3.0/xacml-3.0-core-spec-cd-1-en.html#_Toc229296597"></a>
 */
public enum DataTypes {
	STRING("http://www.w3.org/2001/XMLSchema#string");

	private final String value;

	DataTypes(String value) {
		this.value = value;
	}

	public String value() {
		return value;
	}
}
