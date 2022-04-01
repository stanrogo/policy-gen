package xacmlgen;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.*;
import xacmlgen.enums.Algorithms;
import xacmlgen.enums.DataTypes;
import xacmlgen.enums.Functions;
import xacmlgen.enums.Identifiers;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class XacmlPolicyBuilder {
	private static int policySetID = 0;
	private static int policyID = 0;
	private static int ruleID = 0;
	private final Marshaller marshaller;
	private final ObjectFactory objectFactory;

	List<AnyOfType> anyOfType = new ArrayList<>();
	List<AllOfType> allOfType = new ArrayList<>();
	List<MatchType> matchType = new ArrayList<>();
	AttributeValueType leftPred;
	Functions comparator;
	AttributeDesignatorType rightPred;

	public XacmlPolicyBuilder() throws JAXBException {
		String xacmlSchema = "oasis.names.tc.xacml._3_0.core.schema.wd_17";
		JAXBContext jaxbContext = JAXBContext.newInstance(xacmlSchema);
		marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
				"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17 http://docs.oasis-open.org/xacml/3.0/xacml-core-v3-schema-wd-17.xsd");
		objectFactory = new ObjectFactory();
	}

	public String build(String permission, String defaultEffect) throws JAXBException {
		Algorithms permitDenyOverrides = permission.equals("deny") ? Algorithms.POLICY_DENY_OVERRIDES : Algorithms.POLICY_PERMIT_OVERRIDES;

		PolicySetType ps = new PolicySetType();
		ps.setPolicySetId("PS" + policySetID++);
		ps.setPolicyCombiningAlgId(permitDenyOverrides.value());
		ps.setTarget(new TargetType());
		ps.setVersion("1.0");

		PolicyType defaultPolicy = createDefaultRule(defaultEffect.equals("deny") ? EffectType.DENY : EffectType.PERMIT);
		ps.getPolicySetOrPolicyOrPolicySetIdReference().add(objectFactory.createPolicy(defaultPolicy));

		PolicyType pt = new PolicyType();
		pt.setPolicyId("P" + policyID++);
		pt.setRuleCombiningAlgId(Algorithms.RULE_FIRST_APPLICABLE.value());
		pt.setTarget(new TargetType());
		pt.setVersion("1.0");

		if (!anyOfType.isEmpty()) {
			TargetType tt = objectFactory.createTargetType();
			tt.getAnyOf().addAll(anyOfType);
			RuleType rule = createRule(permission.equals("deny") ? EffectType.DENY : EffectType.PERMIT);
			rule.setTarget(tt);
			pt.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition().add(rule);
			ps.getPolicySetOrPolicyOrPolicySetIdReference().add(objectFactory.createPolicy(pt));
		}

		JAXBElement<PolicySetType> root = objectFactory.createPolicySet(ps);
		StringWriter sw = new StringWriter();
		marshaller.marshal(root, sw);
		return sw.toString();
	}

	private RuleType createRule(EffectType effectType) {
		RuleType rt = new RuleType();
		rt.setRuleId("R" + ruleID++);
		rt.setEffect(effectType);
		return rt;
	}

	private PolicyType createDefaultRule(EffectType permitDenyEffect) {
		PolicyType pt = new PolicyType();
		pt.setPolicyId("P" + policyID++);
		pt.setRuleCombiningAlgId(Algorithms.RULE_FIRST_APPLICABLE.value());
		pt.setTarget(new TargetType());
		pt.setVersion("1.0");

		RuleType rt = new RuleType();
		rt.setRuleId("R" + ruleID++);
		rt.setEffect(permitDenyEffect);

		pt.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition().add(rt);
		return pt;
	}

	public void createAnyOf() {
		AnyOfType aot = objectFactory.createAnyOfType();
		aot.getAllOf().addAll(allOfType);
		anyOfType.add(aot);
		allOfType = new ArrayList<>();
	}

	public void createAllOf() {
		AllOfType aot = objectFactory.createAllOfType();
		aot.getMatch().addAll(matchType);
		allOfType.add(aot);
		matchType = new ArrayList<>();
	}

	public void createComparator(String comparator) {
		this.comparator = switch (comparator) {
			case ">" -> Functions.GREATER_THAN;
			case "<" -> Functions.LESS_THAN;
			case ">=" -> Functions.GREATER_EQUAL_THAN;
			case "<=" -> Functions.LESS_EQUAL_THAN;
			default -> Functions.EQUAL;
		};
	}

	public void createMatch() {
		MatchType mt = new MatchType();
		mt.setMatchId(comparator.value());
		mt.setAttributeValue(leftPred);
		mt.setAttributeDesignator(rightPred);
		matchType.add(mt);
	}

	public void createVariable(String value) {
		leftPred = new AttributeValueType();
		leftPred.setDataType(DataTypes.STRING.value());
		leftPred.getContent().add(value);
	}

	public void createAttribute(String value) {
		rightPred = new AttributeDesignatorType();
		rightPred.setMustBePresent(false);
		rightPred.setDataType(DataTypes.STRING.value());
		rightPred.setCategory(Identifiers.RESOURCE_CATEGORY.value());
		rightPred.setAttributeId(value);
	}
}
