import picocli.CommandLine;

import java.util.concurrent.Callable;

import static picocli.CommandLine.*;

@Command(subcommands = {
		DB2Policies.class,
		OraclePolicies.class,
		XacmlPolicies.class,
})
public class Main implements Callable<Integer> {
	@Option(names = {"-i", "--input"}, required = true, scope = ScopeType.INHERIT)
	String input;

	public static void main(String[] args) {
		int exitCode = new CommandLine(new Main()).execute(args);
		System.exit(exitCode);
	}

	@Override
	public Integer call() {
		return 0;
	}
}
