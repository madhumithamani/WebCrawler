/**
 * @author Nisha
 *
 */
public class RobotRule {
	public String userAgent;
	public String ruleDisallow;
	public String ruleAllow;

	RobotRule() {

		userAgent = "*";

	}

	//format to store the parsed robots.txt file
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		String NEW_LINE = System.getProperty("line.separator");
		result.append(this.getClass().getName() + " Object {" + NEW_LINE);
		result.append("   userAgent: " + this.userAgent + NEW_LINE);

		if (this.ruleDisallow != null)
			result.append("   rule: " + this.ruleDisallow + NEW_LINE);
		if (this.ruleAllow != null)
			result.append("   rule: " + this.ruleAllow + NEW_LINE);

		result.append("}");
		return result.toString();
	}
}