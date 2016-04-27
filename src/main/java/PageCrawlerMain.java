import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.*;

public class PageCrawlerMain {

    private static String seed = "";
    private static int totalPages = 0;
    private static String allowedDomain = null;
    private static int politnessPolicy  = 100;
    private final static String SEED_FILE_PATH = "/Users/MadVish/Documents/seed.csv";

    private static void readSeedFile(){
        FileReader fileReader = null;
        try {
            String line;
            fileReader = new FileReader(SEED_FILE_PATH);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            line = bufferedReader.readLine().trim();
            if(line.length()>0){
                String[] splits = line.split(",");
                if(splits.length>0){
                    seed = splits[0];
                    totalPages = Integer.parseInt(splits[1]);
                    if(splits.length==3) {
                        allowedDomain = splits[2];
                    }else if(splits.length ==2){
                        allowedDomain = "-";
                    }
                }else{
                    bufferedReader.close();
                    System.out.println("Invalid seed file");
                    System.exit(0);
                }
            }else{
                bufferedReader.close();
                System.out.println("Invalid seed file");
                System.exit(0);
            }
            bufferedReader.close();
            //System.out.println(seed + ":" + totalPages + ":" + allowedDomain);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            if(fileReader!=null){
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void generateReport(){
        createReport myHtml = new createReport();
        myHtml.createHTML(PageCrawlerRunnable.pageInfoCollection);
    }

    public static void main(String[] args) throws Exception {
        readSeedFile();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(20, 20, 10, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        CrawlManager crawlManager = new CrawlManager(totalPages, politnessPolicy, seed,allowedDomain);
        while(!crawlManager.hasCrawlCompleted() ){
            Set<String> nextUrlsToProcess = crawlManager.getNextUrlsToProcess();
            for (String url : nextUrlsToProcess) {
                PageCrawlerRunnable runnable = new PageCrawlerRunnable(url, crawlManager);
                executor.execute(runnable);
            }
            Thread.sleep(100);
        }
        executor.shutdownNow();
        DOMParser.readAllFiles();
        generateReport();
        System.exit(0);
    }
}
