import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * @author Nisha
 *
 */
public class RobotFetcher {

	/**
	 * @param url - URL object to verify against robots.tct
	 * 
	 * @return true = allowed URL
	 * 		   false = disallowed URL 			
	 */
	public static RobotInfo getRobotInfo(URL url){
		String strHost = url.getHost();
		String strProtocol = url.getProtocol();
		String strRobot = strProtocol + "://" + strHost + "/robots.txt";
//		System.out.println("Getting Robot file: " + strRobot);
		StringBuilder builder = new StringBuilder();
		InputStream urlRobotStream = null;
		try {
			URL urlRobot = new URL(strRobot);
			URLConnection connection = urlRobot.openConnection();
			connection.setConnectTimeout(1000);
			urlRobotStream = connection.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(urlRobotStream));
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line).append("\n");
			}
		} catch (IOException e) {
			return new RobotInfo(new ArrayList<RobotRule>(), new ArrayList<RobotRule>());
		} finally {
			if (urlRobotStream != null) {
				try {
					urlRobotStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		String strCommands = builder.toString();
		if (strCommands.contains("Disallow")) {
			String[] split = strCommands.split("\n");
			ArrayList<RobotRule> robotRulesDisallow = new ArrayList<RobotRule>();
			ArrayList<RobotRule> robotRulesAllow = new ArrayList<RobotRule>();

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

			return new RobotInfo(robotRulesAllow, robotRulesDisallow);
		}
		else {
			return new RobotInfo(new ArrayList<RobotRule>(), new ArrayList<RobotRule>());
		}
	}
}
