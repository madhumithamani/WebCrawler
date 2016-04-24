import java.util.Set;
import java.util.concurrent.*;

public class PageCrawlerMain {

    public static void main(String[] args) throws InterruptedException {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 10, 10, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        CrawlManager crawlManager = new CrawlManager(100, 1000, "http://www.scu.edu");
        while(!crawlManager.hasMinimumCapacityMet()){
            Set<String> nextUrlsToProcess = crawlManager.getNextUrlsToProcess();
            for (String url : nextUrlsToProcess) {
                PageCrawlerRunnable runnable = new PageCrawlerRunnable(url, crawlManager);
                executor.execute(runnable);
            }
            Thread.sleep(100);
        }
        executor.shutdownNow();
        System.exit(0);
    }
}
