import SimpleBoolean.SimpleBooleanLexer;
import SimpleBoolean.SimpleBooleanParser;
import jakarta.xml.bind.JAXBException;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import picocli.CommandLine;
import xacmlgen.SimpleBooleanVisitor;
import xacmlgen.XacmlPolicyBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

@CommandLine.Command(name = "xacml")
public class XacmlPolicies extends PolicyParser {

	@CommandLine.Option(names = {"-d", "--outputDir"}, required = true)
	private String outputDir;

	@Override
	protected void writeSQL(Map<String, String> policies) throws IOException {
		Properties props = new Properties();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		props.load(loader.getResourceAsStream("project.properties"));

		String dirName = props.get("my.basedir") + "/" + outputDir;
		File xacmlDir = new File(dirName);

		if (!xacmlDir.exists() && !xacmlDir.mkdir()) {
			throw new IOException();
		}

		for (Map.Entry<String, String> entry : policies.entrySet()) {
			String fileName = dirName + "/" + entry.getKey() + ".xacml";
			File xacmlFile = new File(fileName);

			if (xacmlFile.exists() && !xacmlFile.delete()) {
				throw new IOException();
			}

			if (!xacmlFile.createNewFile()) {
				throw new IOException();
			}

			FileWriter myWriter = new FileWriter(xacmlFile);
			myWriter.write(entry.getValue());
			myWriter.close();
		}
	}

	@Override
	protected String buildRowPolicy(String baseTable, String policy, String permission) throws JAXBException {
		return buildPolicy(baseTable, policy, permission);
	}

	@Override
	protected String buildCellPolicy(String baseTable, String attribute, String policy, String permission) throws JAXBException {
		return buildPolicy(baseTable, policy, permission);
	}

	private String buildPolicy(String baseTable, String policy, String permission) throws JAXBException {
		if (policy.isEmpty()) {
			return new XacmlPolicyBuilder().build(permission, permission);
		}

		SimpleBooleanLexer lexer = new SimpleBooleanLexer(CharStreams.fromString(policy));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		SimpleBooleanParser parser = new SimpleBooleanParser(tokens);
		ParseTree tree = parser.target();
		XacmlPolicyBuilder builder = tree.accept(new SimpleBooleanVisitor(baseTable));
		return builder.build(permission, permission.equals("deny") ? "permit" : "deny");
	}
}
