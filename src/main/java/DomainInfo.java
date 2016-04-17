import java.util.HashSet;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by MadVish on 4/14/16.
 */
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
        return crawledURLs.contains(url);
    }

}
