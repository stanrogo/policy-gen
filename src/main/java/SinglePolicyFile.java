import picocli.CommandLine;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public abstract class SinglePolicyFile extends PolicyParser {
	@CommandLine.Option(names = {"-o", "--outputFile"}, required = true)
	private String outputFile;

	@Override
	protected void writeSQL(Map<String, String> policies) throws IOException {
		List<String> policyList = new ArrayList<>(policies.values());
		policyList.add(buildPreamble());

		Properties props = new Properties();
		props.load(this.getClass().getResourceAsStream("project.properties"));
		String fileName = props.get("my.basedir") + "/" + outputFile;
		File myObj = new File(fileName);
		if ((myObj.delete() && myObj.createNewFile()) || myObj.createNewFile()) {
			FileWriter myWriter = new FileWriter(fileName);
			myWriter.write(String.join("\n\n", policyList));
			myWriter.close();
		}
	}

	abstract String buildPreamble();
}
