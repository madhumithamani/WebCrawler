import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;


public class CrawlManager {
    private final int minimumLinksToCrawl;
    private final int politenessInMs;
    private int tasksAssigned = 0;
    private int tasksProcessed = 0;
    private String allowedURL;

    public static volatile HashMap<String,DomainInfo> domainCollection = new HashMap<String, DomainInfo>();

    public CrawlManager(int minumumLinksToCrawl, int politenessInMs, String startUrl, String allowedURL) throws IOException {

        this.minimumLinksToCrawl = minumumLinksToCrawl;
        this.politenessInMs = politenessInMs;
        this.allowedURL = allowedURL;
        addURL(startUrl);
    }

    private boolean checkDomainRestriction(String url){

        boolean isAllowed = false;
        try {

            URL url1 = new URL(url);
            System.out.println(url1);
            String hostName = url1.getHost();
            String path= url1.getPath();
            String fullURL = hostName.concat(path);
            if(!fullURL.endsWith("/")){
                fullURL = fullURL.concat("/");
                System.out.println(fullURL);
            }

            if(!allowedURL.endsWith("/"))
            {
                allowedURL = allowedURL.concat("/");
//                System.out.println(allowedURL);
            }

            URI uri = new URI(allowedURL);
//            System.out.println("URI:" + uri);

            if(uri.isAbsolute()){
                hostName = uri.getHost();
                path= uri.getPath();
                String fullrestrictedURL = hostName.concat(path);
                if(fullURL.startsWith(fullrestrictedURL)){
                    isAllowed = true;
                }
                else{
                    isAllowed = false;
                }
            }else{
                if(fullURL.startsWith(allowedURL)){
                    isAllowed = true;
                }
                else{
                    isAllowed = false;
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return isAllowed;
    }

    public void addURL(String url) throws IOException {
        String domainName = getDomainName(url).trim();
        if(domainName.isEmpty()){
            return;
        }


//        if(!allowedURL.equalsIgnoreCase("-")){
//            String allowedDomainName = getDomainName("http://"+allowedURL);
//            System.out.println("domain:" + domainName);
//            System.out.println("allowedDomain:" + allowedDomainName);
//            if(!domainName.equalsIgnoreCase(allowedDomainName)){
//                System.out.println("Rejected URL:" + url);
//                return;
//            }
//        }
        if (allowedURL.equalsIgnoreCase("-") || checkDomainRestriction(url)) {
            if(domainCollection.containsKey(domainName)){
                addUrlToDomainInfo(domainCollection.get(domainName),url);
            } else {
                RobotInfo robotInfo = RobotFetcher.getRobotInfo(new URL(url));
                synchronized (domainCollection){
                    if(domainCollection.containsKey(domainName)){
                        addUrlToDomainInfo(domainCollection.get(domainName),url);
                    } else {
                        domainCollection.put(domainName,new DomainInfo(robotInfo));
                    }
                }
                addUrlToDomainInfo(domainCollection.get(domainName), url);
            }
        }
    }

    private void addUrlToDomainInfo(DomainInfo domainInfo, String url){
        synchronized (domainInfo){
//            System.out.println("Adding url:" + url);
            domainInfo.addNewURL(url);
        }
    }

    public boolean hasCrawlCompleted() {
        if(tasksProcessed >= minimumLinksToCrawl){
            return true;
        } else {
            //if currently processing links found return false
            //if any links in the queue return false
            // else return true
            synchronized (domainCollection){
                for (Map.Entry<String, DomainInfo> entry : domainCollection.entrySet()) {
                    DomainInfo domainInfo = entry.getValue();
                    if(domainInfo.isServing()){
                        return false;
                    }else if(domainInfo.hasMoreURLsToCrawl()){
                        return false;
                    }
                }
                return true;
            }
        }
    }

    public void completed(String url){
        String domainName = getDomainName(url);
        synchronized (domainCollection){
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
//            System.out.println("Getting next set of URLs to run.");
        long startTime = System.currentTimeMillis();
        HashSet<String> returnUrls = new HashSet<String>();
        synchronized (domainCollection) {
            for (Map.Entry<String, DomainInfo> entry : domainCollection.entrySet()) {
                String key = entry.getKey();
                DomainInfo domainInfo = entry.getValue();
                if (!domainInfo.isServing() && startTime - domainInfo.getLastCrawledTime() > politenessInMs) {
                    String nextURLToCrawl = domainInfo.getNextURLToCrawl();
                    if (nextURLToCrawl != null) {
                        returnUrls.add(nextURLToCrawl);
                        domainInfo.setServingStatus(true);
                    }
                }
            }
        }
        HashSet<String> finalUrls = new HashSet<String>();
        Iterator<String> iterator = returnUrls.iterator();
        while(tasksAssigned < minimumLinksToCrawl && iterator.hasNext()){
            finalUrls.add(iterator.next());
            tasksAssigned++;
        }
        return finalUrls;
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
