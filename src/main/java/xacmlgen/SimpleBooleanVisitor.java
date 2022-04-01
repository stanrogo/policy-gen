package xacmlgen;

import SimpleBoolean.SimpleBooleanBaseVisitor;
import SimpleBoolean.SimpleBooleanParser;
import jakarta.xml.bind.JAXBException;

public class SimpleBooleanVisitor extends SimpleBooleanBaseVisitor<XacmlPolicyBuilder> {
	private final XacmlPolicyBuilder xacmlPolicyBuilder;
	private final String baseTable;

	public SimpleBooleanVisitor(String baseTable) throws JAXBException {
		this.baseTable = baseTable;
		xacmlPolicyBuilder = new XacmlPolicyBuilder();
	}

	@Override
	public XacmlPolicyBuilder visitAnyOf(SimpleBooleanParser.AnyOfContext ctx) {
		visitChildren(ctx);
		xacmlPolicyBuilder.createAnyOf();
		return xacmlPolicyBuilder;
	}

	@Override
	public XacmlPolicyBuilder visitAllOf(SimpleBooleanParser.AllOfContext ctx) {
		visitChildren(ctx);
		xacmlPolicyBuilder.createAllOf();
		return xacmlPolicyBuilder;
	}

	@Override
	public XacmlPolicyBuilder visitAttribute(SimpleBooleanParser.AttributeContext ctx) {
		visitChildren(ctx);
		if (ctx.IDENTIFIER() != null) {
			xacmlPolicyBuilder.createAttribute(baseTable + "." + ctx.IDENTIFIER().getText());
		} else if (ctx.qualifiedIdentifier() != null) {
			xacmlPolicyBuilder.createAttribute(ctx.qualifiedIdentifier().getText());
		}
		return xacmlPolicyBuilder;
	}

	@Override
	public XacmlPolicyBuilder visitVariable(SimpleBooleanParser.VariableContext ctx) {
		visitChildren(ctx);
		if (ctx.STRING() != null) {
			xacmlPolicyBuilder.createVariable(ctx.STRING().getText());
		} else if (ctx.DECIMAL() != null) {
			xacmlPolicyBuilder.createVariable(ctx.DECIMAL().getText());
		} else if (ctx.IDENTIFIER() != null) {
			xacmlPolicyBuilder.createVariable(baseTable + "." + ctx.IDENTIFIER().getText());
		} else if (ctx.qualifiedIdentifier() != null) {
			xacmlPolicyBuilder.createVariable(ctx.qualifiedIdentifier().getText());
		}
		return xacmlPolicyBuilder;
	}

	@Override
	public XacmlPolicyBuilder visitComparator(SimpleBooleanParser.ComparatorContext ctx) {
		visitChildren(ctx);
		xacmlPolicyBuilder.createComparator(ctx.getText());
		return xacmlPolicyBuilder;
	}

	@Override
	public XacmlPolicyBuilder visitPredicate(SimpleBooleanParser.PredicateContext ctx) {
		visitChildren(ctx);
		xacmlPolicyBuilder.createMatch();
		return xacmlPolicyBuilder;
	}
}