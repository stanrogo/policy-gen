package xacmlgen.enums;

public enum Algorithms {
	RULE_DENY_OVERRIDES("urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:deny-overrides"),
	POLICY_DENY_OVERRIDES("urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:deny-overrides"),
	RULE_PERMIT_OVERRIDES("urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:permit-overrides"),
	POLICY_PERMIT_OVERRIDES(
			"urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:permit-overrides"),
	RULE_FIRST_APPLICABLE("urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:first-applicable"),
	POLICY_FIRST_APPLICABLE(
			"urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:first-applicable"),
	RULE_ORDERED_DENY_OVERRIDES(
			"urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:ordered-deny-overrides"),
	POLICY_ORDERED_DENY_OVERRIDES(
			"urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:ordered-deny-overrides"),
	RULE_ORDERED_PERMIT_OVERRIDES(
			"urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:ordered-permit-overrides"),
	POLICY_ORDERED_PERMIT_OVERRIDES(
			"urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:ordered-permit-overrides"),
	RULE_DENY_UNLESS_PERMIT(
			"urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:deny-unless-permit"),
	POLICY_DENY_UNLESS_PERMIT(
			"urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:deny-unless-permit"),
	RULE_PERMIT_UNLESS_DENY(
			"urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:permit-unless-deny"),
	POLICY_PERMIT_UNLESS_DENY(
			"urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:permit-unless-deny");


	private final String value;

	Algorithms(String value) {
		this.value = value;
	}

	public String value() {
		return value;
	}
}
