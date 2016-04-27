import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by MadVish on 4/26/16.
 */
public class RobotInfo {
    private  ArrayList<RobotRule> robotRulesDisallow = new ArrayList();
    private  ArrayList<RobotRule> robotRulesAllow = new ArrayList();

    @Override
    public String toString() {
        return "RobotInfo{" +
                "robotRulesDisallow=" + robotRulesDisallow.size() +
                ", robotRulesAllow=" + robotRulesAllow.size() +
                '}';
    }

    public RobotInfo(ArrayList<RobotRule> robotRulesAllow, ArrayList<RobotRule> robotRulesDisallow) {
        this.robotRulesDisallow.addAll(robotRulesDisallow);
        this.robotRulesAllow.addAll(robotRulesAllow);
    }

    public boolean isUrlAllowed(String urlStr){
        URL url = null;
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
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

            if(!robotRule.userAgent.equals("*")){
                continue;
            }

            if (robotRule.ruleDisallow.length() <= path.length()) {
                String pathCompare = path.substring(0,
                        robotRule.ruleDisallow.length());
                if (pathCompare.equals(robotRule.ruleDisallow))
                    return false;
            }
        }

        return true;
    }
}
