import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Nisha
 *
 */
public class TestRobot {

	@SuppressWarnings("static-access")
	public static void main(String[] args) {

		RobotSafe rb = new RobotSafe();

		try {
			URL myurl = new URL("https://www.google.com/search/about");
			// URL myurl = new URL("https://www.paypal.com/refer/");
			// URL myurl = new URL("https://www.yahoo.com/bin/test");
			
			boolean status = rb.robotSafe(myurl);

			System.out.println(status);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}
