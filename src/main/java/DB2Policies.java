import net.sf.jsqlparser.JSQLParserException;
import picocli.CommandLine;

import java.util.List;

@CommandLine.Command(name = "db2")
public class DB2Policies extends SinglePolicyFile {

	@Override
	protected String buildRowPolicy(String baseTable, String policy, String permission)
			throws JSQLParserException {
		return "CREATE OR REPLACE PERMISSION " + baseTable + "_ROW_ACCESS ON " + baseTable +
				" FOR ROWS WHERE\n" +
				genExists(baseTable, policy, permission) + "\n" +
				"ENFORCED FOR ALL ACCESS ENABLE;\n" +
				"ALTER TABLE " + baseTable + " ACTIVATE ROW ACCESS CONTROL;";
	}

	@Override
	protected String buildCellPolicy(String baseTable, String attribute, String policy,
									 String permission)
			throws JSQLParserException {
		return "CREATE OR REPLACE MASK " + attribute + "_COL_MASK ON " + baseTable + " FOR\n" +
				"COLUMN " + attribute + " RETURN CASE WHEN (\n" +
				genExists(baseTable, policy, permission) + "\n" +
				") THEN " + attribute + "\n" +
				"ELSE NULL END ENABLE;\n" +
				"ALTER TABLE " + baseTable + " ACTIVATE COLUMN ACCESS CONTROL;";
	}

	@Override
	protected String buildPreamble() {
		return "";
	}

	private String genExists(String baseTable, String policy, String permission)
			throws JSQLParserException {
		if (policy.isEmpty()) {
			String boolPermission = permission.equals("deny") ? "false" : "true";
			return "SYSTEM_USER = 'DB2INST1' OR " + boolPermission;
		}

		List<String> tableList = getTablesUsed(baseTable, policy);
		String negative = permission.equals("deny") ? "NOT " : "";
		return "SYSTEM_USER = 'DB2INST1' OR " +
				negative + "EXISTS (SELECT 1 FROM " + String.join(", ", tableList) + " WHERE (" +
				policy + ") AND employee.e_name = SYSTEM_USER)";
	}
}
