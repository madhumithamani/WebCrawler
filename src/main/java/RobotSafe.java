import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * @author Nisha
 *
 */
public class RobotSafe {

	/**
	 * @param url - URL object to verify against robots.tct
	 * 
	 * @return true = allowed URL
	 * 		   false = disallowed URL 			
	 */
	public static boolean robotSafe(URL url) {
		String strHost = url.getHost();
		String strProtocol = url.getProtocol();
		String strRobot = strProtocol + "://" + strHost + "/robots.txt";
		URL urlRobot;
		try {
			urlRobot = new URL(strRobot);
		} catch (MalformedURLException e) {
			return false;
		}

		String strCommands;

		try {

			InputStream urlRobotStream = urlRobot.openStream();
			byte b[] = new byte[3000];

			int numRead = urlRobotStream.read(b);

			strCommands = new String(b, 0, numRead);

			while (numRead != -1) {
				numRead = urlRobotStream.read(b);
				if (numRead != -1) {
					String newCommands = new String(b, 0, numRead);
					strCommands += newCommands;
				}
			}
			urlRobotStream.close();
		}

		catch (IOException e) {
			return true; // if there is no robots.txt file, it is OK to search
		}

		
		if (strCommands.contains("Disallow")) {
			String[] split = strCommands.split("\n");
			ArrayList<RobotRule> robotRulesDisallow = new ArrayList();
			ArrayList<RobotRule> robotRulesAllow = new ArrayList();

			String mostRecentUserAgent = null;
			for (int i = 0; i < split.length; i++) {
				String line = split[i].trim();

				if (line.toLowerCase().startsWith("user-agent")) {
					int start = line.indexOf(":") + 1;
					int end = line.length();
					mostRecentUserAgent = line.substring(start, end).trim();
				} else if (line.startsWith("Disallow")) {
					if (mostRecentUserAgent != null) {
						RobotRule r = new RobotRule();
						r.userAgent = mostRecentUserAgent;
						int start = line.indexOf(":") + 1;
						int end = line.length();
						r.ruleDisallow = line.substring(start, end).trim();
						robotRulesDisallow.add(r);
					}
				} else if (line.startsWith("Allow")) {
					if (mostRecentUserAgent != null) {
						RobotRule r = new RobotRule();
						r.userAgent = mostRecentUserAgent;
						int start = line.indexOf(":") + 1;
						int end = line.length();
						r.ruleAllow = line.substring(start, end).trim();
						robotRulesAllow.add(r);
					}
				}
			}

			for (RobotRule robotRule : robotRulesAllow) {
				String path = url.getFile(); // getPath();

				if (robotRule.ruleAllow.length() == 0)
					return true; // allows everything if BLANK

				if (robotRule.ruleAllow == "/")
					return true; // allows nothing if /

				if (robotRule.ruleAllow.length() <= path.length()) {
					String pathCompare = path.substring(0,
							robotRule.ruleAllow.length());
					if (pathCompare.equals(robotRule.ruleAllow))
						return true;
				}
			}

			for (RobotRule robotRule : robotRulesDisallow) {
				String path = url.getFile(); // getPath();
				if (robotRule.ruleDisallow.length() == 0)
					return true; // allows everything if BLANK
				if (robotRule.ruleDisallow == "/")
					return false; // allows nothing if /

				if (robotRule.ruleDisallow.length() <= path.length()) {
					String pathCompare = path.substring(0,
							robotRule.ruleDisallow.length());
					if (pathCompare.equals(robotRule.ruleDisallow))
						return false;
				}
			}
		}
		return true;
	}
}
