import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by MadVish on 4/14/16.
 */
public class CrawlManager {
    private final int minimumLinksToCrawl;
    private final int politenessInMs;
    private int tasksProcessed = 0;

    public static volatile HashMap<String,DomainInfo> domainCollection = new HashMap<String, DomainInfo>();

    public CrawlManager(int minumumLinksToCrawl, int politenessInMs, String startUrl) {
        this.minimumLinksToCrawl = minumumLinksToCrawl;
        this.politenessInMs = politenessInMs;
        addURL(startUrl);
    }

    public void addURL(String url){
        String domainName = getDomainName(url);
        synchronized (this){
            if(!domainCollection.containsKey(domainName)){
                domainCollection.put(domainName, new DomainInfo());
            }
            DomainInfo domainInfo = domainCollection.get(domainName);
            domainInfo.addNewURL(url);
        }
    }

    public boolean hasMinimumCapacityMet() {
        return tasksProcessed >= minimumLinksToCrawl;
    }

    public void completed(String url){
        String domainName = getDomainName(url);
        synchronized (this){
            if(domainCollection.containsKey(domainName)){
                tasksProcessed ++;
                DomainInfo domainInfo = domainCollection.get(domainName);
                domainInfo.setLastCrawledTime(System.currentTimeMillis());
                domainInfo.setServingStatus(false);
                domainInfo.addToCrawledURL(url);
            } else {
                throw new RuntimeException("Crawled URL not found. Immpossible");
            }
        }
    }

    public Set<String> getNextUrlsToProcess() {
        synchronized (this){
//            System.out.println("Getting next set of URLs to run.");
            long startTime = System.currentTimeMillis();
            HashSet<String> returnUrls = new HashSet<String>();
            for (Map.Entry<String, DomainInfo> entry : domainCollection.entrySet()) {
                String key = entry.getKey();
                DomainInfo domainInfo = entry.getValue();
                if(!domainInfo.isServing() && startTime - domainInfo.getLastCrawledTime() > politenessInMs){
                    String nextURLToCrawl = domainInfo.getNextURLToCrawl();
                    if(nextURLToCrawl!= null){
                        returnUrls.add(nextURLToCrawl);
                        domainInfo.setServingStatus(true);
                    }
                }
            }

            return returnUrls;
        }
    }


    private static String getDomainName(String inputURL){
        try{
//            System.out.println("inside : " + inputURL);
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

}
