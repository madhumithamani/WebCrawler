import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by MadVish on 4/13/16.
 */
public abstract class DomainName {
    public static String getHostName(String inputURL){
        try{
            URL url = new URL(inputURL);
            String hostName = url.getHost();
            if(hostName.startsWith("www")){
                hostName = hostName.substring("www".length()+1);
            }
            return hostName;
        }catch(MalformedURLException ex)
        {
            ex.printStackTrace();
        }
        return "";
    }
    public static void main(String[] args) {
        String hostName = DomainName.getHostName("https://www.google.com/intl/en/about.html?fg=1");
        System.out.println(hostName);
//        hostName = DomainName.getHostName("http://www.stackoverflow.com");
//        System.out.println(hostName);
//        hostName = DomainName.getHostName("http://www.stackoverflow.com");
//        System.out.println(hostName);
    }
}
