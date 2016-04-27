import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Nisha
 *
 */
public class TestRobot {


	public static void main(String[] args) {


		try {
			URL myurl = new URL("https://www.google.com/search/about");
			// URL myurl = new URL("https://www.paypal.com/refer/");
			// URL myurl = new URL("https://www.yahoo.com/bin/test");

			RobotInfo status = RobotFetcher.getRobotInfo(myurl);

			System.out.println(status);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}
