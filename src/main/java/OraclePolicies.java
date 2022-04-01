import net.sf.jsqlparser.JSQLParserException;
import picocli.CommandLine;

import java.util.List;

@CommandLine.Command(name = "oracle")
public class OraclePolicies extends SinglePolicyFile {
	@CommandLine.Option(names = {"-s", "--schema"}, required = true)
	private String schema;

	private String buildPolicy(String baseTable, String policyName, String funcName,
							   String existsClause, String props) {
		return "CREATE OR REPLACE FUNCTION " + funcName + "(\n" +
				"    schema_var IN VARCHAR2,\n" +
				"    table_var IN VARCHAR2)\n" +
				"  RETURN VARCHAR2\n" +
				"  AS\n" +
				"    return_val VARCHAR2 (800);\n" +
				"  BEGIN\n" +
				"    return_val := '(" + existsClause + ")';\n" +
				"  RETURN return_val;\n" +
				"END;\n" +
				"/\n" +
				"\n" +
				"BEGIN\n" +
				"  DBMS_RLS.DROP_POLICY('" + schema + "', '" + baseTable + "', '" + policyName + "');\n" +
				"  EXCEPTION\n" +
				"  WHEN OTHERS THEN\n" +
				"    IF SQLCODE != -28102 THEN\n" +
				"      RAISE;\n" +
				"    END IF;\n" +
				"END;\n" +
				"/\n" +
				"BEGIN\n" +
				"  DBMS_RLS.ADD_POLICY(\n" +
				props + "\n" +
				"  );\n" +
				"END;\n" +
				"/\n";
	}

	private String buildAddPolicyRow(String baseTable, String funcName, String policyName) {
		return "    object_schema => '" + schema + "',\n" +
				"    object_name => '" + baseTable + "',\n" +
				"    policy_name => '" + policyName + "',\n" +
				"    function_schema => '" + schema + "',\n" +
				"    policy_function => '" + funcName + "',\n" +
				"    statement_types => 'select'";
	}

	private String buildAddPolicyCell(String baseTable, String attribute, String funcName,
									  String policyName) {
		return buildAddPolicyRow(baseTable, funcName, policyName) + ",\n" +
				"    sec_relevant_cols => '" + attribute + "',\n" +
				"    sec_relevant_cols_opt => dbms_rls.all_rows";
	}

	@Override
	protected String buildRowPolicy(String baseTable, String policy, String permission)
			throws JSQLParserException {
		String funcName = baseTable + "_ROW_ACCESS";
		String policyName = funcName + "_policy";
		String existsClause = genExists(baseTable, policy, permission);
		String addPolicy = buildAddPolicyRow(baseTable, funcName, policyName);
		return buildPolicy(baseTable, policyName, funcName, existsClause, addPolicy);
	}

	@Override
	protected String buildCellPolicy(String baseTable, String attribute, String policy,
									 String permission)
			throws JSQLParserException {
		String funcName = baseTable + "_" + attribute + "_CELL_ACCESS";
		String policyName = funcName + "_policy";
		String existsClause = genExists(baseTable, policy, permission);
		String addPolicy = buildAddPolicyCell(baseTable, attribute, funcName, policyName);
		return buildPolicy(baseTable, policyName, funcName, existsClause, addPolicy);
	}

	@Override
	protected String buildPreamble() {
		return "";
	}

	private String genExists(String baseTable, String policy, String permission)
			throws JSQLParserException {
		if (policy.isEmpty()) {
			String boolPermission = permission.equals("deny") ? "1 = 0" : "1 = 1";
			return "(SYS_CONTEXT(''USERENV'', ''SESSION_USER'') = ''ADMIN1'') OR " + boolPermission;
		}

		List<String> tableList = getTablesUsed(baseTable, policy);
		String condition = policy.replaceAll("'", "''");
		if (!tableList.isEmpty()) {
			if (tableList.contains("employee")) {
				condition += " AND (employee.e_name = SYS_CONTEXT(''USERENV'', ''SESSION_USER''))";
			}

			condition = "EXISTS (SELECT 1 FROM " + String.join(", ", tableList) + " WHERE (" + condition + "))";
		}

		String negative = permission.equals("deny") ? "NOT " : "";
		return "(SYS_CONTEXT(''USERENV'', ''SESSION_USER'') = ''ADMIN1'') OR " + negative + "(" + condition + ")";
	}
}

