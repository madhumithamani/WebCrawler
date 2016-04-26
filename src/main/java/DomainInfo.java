import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;


public class DomainInfo {
    private Long lastCrawledTime = 0L;
    private boolean isServing = false;
    private Queue<String> yetToCrawlUrls = new LinkedBlockingQueue<String>();
    private HashSet<String> crawledURLs = new HashSet<String>();

    public Long getLastCrawledTime() {
        return lastCrawledTime;
    }

    public void setLastCrawledTime(Long lastCrawledTime) {
        this.lastCrawledTime = lastCrawledTime;
    }

    public boolean isServing() {
        return isServing;
    }

    public void setServingStatus(boolean status) {
        isServing = status;
    }

    public String getNextURLToCrawl(){
        while(yetToCrawlUrls.peek() != null){
            String url = yetToCrawlUrls.poll();
            if(!hasCrawledURL(url)){
                return url;
            }
        }
        return null;
    }

    public void addNewURL(String url) {
        this.yetToCrawlUrls.add(url);
    }

    public void addToCrawledURL(String url){
        crawledURLs.add(url);
    }

    private boolean hasCrawledURL(String url){
        boolean containsURL= false;
        for(String crawledUrl : crawledURLs){
            URL url1;
            URL url2;
            try {
                url1 = new URL(crawledUrl);
                url2 = new URL(url);

                if(url1.getHost().equalsIgnoreCase(url2.getHost())) {
                    if (url1.getPath().equalsIgnoreCase(url2.getPath())) {
                        if (url1.getProtocol().equalsIgnoreCase(url2.getProtocol()) || !url1.getProtocol().equalsIgnoreCase(url2.getProtocol())) {
                            containsURL = true;
                        }
                    }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

            return containsURL;
       // return crawledURLs.contains(url);
    }

}
