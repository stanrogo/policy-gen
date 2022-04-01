import jakarta.xml.bind.JAXBException;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import picocli.CommandLine;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;

abstract public class PolicyParser implements Callable<Integer> {
	@CommandLine.ParentCommand
	private Main parent;

	public void parse() throws IOException, JSQLParserException, JAXBException {
		Properties props = new Properties();
		props.load(this.getClass().getResourceAsStream("project.properties"));
		String policyDir = props.get("my.basedir") + "/" + parent.input;
		CSVFormat format = CSVFormat.RFC4180.builder().setHeader().setSkipHeaderRecord(true).setDelimiter('|').build();
		CSVParser parser = new CSVParser(new FileReader(policyDir), format);

		Map<String, String> policies = new HashMap<>();

		for (CSVRecord record : parser) {
			String baseTable = record.get("table");
			String attribute = record.get("attribute");
			String policy = record.get("policy");
			String permission = record.get("permission");

			if (attribute.isEmpty()) {
				String sql = buildRowPolicy(baseTable, policy, permission);
				policies.put(baseTable, sql);
			} else {
				String sql = buildCellPolicy(baseTable, attribute, policy, permission);
				policies.put(attribute, sql);
			}
		}

		parser.close();
		writeSQL(policies);
	}

	protected List<String> getTablesUsed(String baseTable, String policy) throws JSQLParserException {
		Expression stmt = CCJSqlParserUtil.parseCondExpression(policy);
		TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
		List<String> tableList = tablesNamesFinder.getTableList(stmt);
		tableList.removeIf(table -> table.equals(baseTable));
		return tableList;
	}

	protected abstract void writeSQL(Map<String, String> policies) throws IOException;

	protected abstract String buildRowPolicy(String baseTable, String policy, String permission)
			throws JSQLParserException, JAXBException, IOException;

	protected abstract String buildCellPolicy(String baseTable, String attribute, String policy,
											  String permission) throws JSQLParserException, JAXBException;

	@Override
	public Integer call() throws Exception {
		this.parse();
		return 0;
	}
}
