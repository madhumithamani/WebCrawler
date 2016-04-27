import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

/**
 * Created by MadVish on 4/14/16.
 */
public class PageCrawlerRunnable implements Runnable {
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36";
    public static final String REPOSITORY_PATH =  "/Users/MadVish/Documents/WSIR/coen272/projectsamples/repository";
    public static ArrayList<PageInfo> pageInfoCollection = new ArrayList<PageInfo>();
    private final String crawlURL;
    private final CrawlManager crawlManager;

    public PageCrawlerRunnable(String crawlURL, CrawlManager crawlManager){
        this.crawlURL = crawlURL;
        this.crawlManager = crawlManager;
    }

    public String getCrawlURL(){
        return this.crawlURL;
    }


    //once the link is fetched, see if it is in the list of already visited URL's
    public void run() {
        try{
            startCrawlProcess();
        } catch (Throwable e) {
            System.out.println("Error while crawling " + crawlURL);
            e.printStackTrace();
        }finally {
            crawlManager.completed(crawlURL);
        }
    }

    private void startCrawlProcess() throws IOException {
        //1. download the page content
        //2. get the title of the page
        //2. get the valid URLs (html links) to crawl
        //3. check against robots.txt file by passing the domain name & url
        //4. get the link count (only valid URLs or all URLs?)
        //5. get the image count
        //6. generate a name for the page
        //7. create an html file and store it
        System.out.println("crawling URL:" + crawlURL);
        Connection connection = Jsoup.connect(crawlURL)
                .userAgent(USER_AGENT)
                .maxBodySize(0) //allows having an unlimited body size
        .timeout(10000);

        PageInfo pageInfo = new PageInfo();
        pageInfo.setliveURL(crawlURL);
        synchronized (pageInfoCollection) {
            pageInfoCollection.add(pageInfo);
        }
        try {
            Document document = connection.get();
            pageInfo.setHttpStatusCode(connection.response().statusCode());
            if(pageInfo.getHttpStatusCode() == 200){
                processPage(document, pageInfo);
            }
        }catch (HttpStatusException ex){
            pageInfo.setHttpStatusCode(ex.getStatusCode());
            throw ex;
        }
    }
    private void processPage(Document document, PageInfo pageInfo) throws IOException {
        pageInfo.setTitle(document.title());
        Elements links = document.select("a[href]");
        //Elements imports = document.select("link[href]");
        ArrayList<String> yetToCrawlURLList = new ArrayList<String>();
        for (Element link : links)
        {
            String strLink = link.absUrl("href");
//            System.out.println("link : " + strLink);
            //check robot validity before adding to the arraylist
            if(!strLink.equals("") && !strLink.isEmpty()) {
                yetToCrawlURLList.add(link.absUrl("href"));
            }
        }
        for (String url : yetToCrawlURLList) {
            crawlManager.addURL(url);
        }
        Elements images = document.select("img[src~=(?i)\\.(jpe?g|png|gif|bmp)]");
//        for (Element image : images) {
//            System.out.println("image : " + image.attr("abs:src"));
//        }
        pageInfo.setLinkCount(yetToCrawlURLList.size());
        pageInfo.setImgCount(images.size());
        savePage(document.html(), pageInfo);

    }

    private static void savePage(String content, PageInfo pageInfo) throws IOException {
        String localDocName = generateLocalDocName();
        String localURL = REPOSITORY_PATH+"/"+localDocName+".html";
        if(createNewDocument(localURL)) //file creation successful
        {
            pageInfo.setlocalURL(localURL);
            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter(localURL);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(content);
                bufferedWriter.flush();
                bufferedWriter.close();
//                System.out.println("PageInfo:" + pageInfo.getHttpStatusCode());
            } finally {
                if(fileWriter  != null)
                    fileWriter.close();
            }
        }
    }

    private static String generateLocalDocName(){
        //http://gregszabo.blogspot.com/2010/01/generating-unique-file-names-with.html
        String uuid = UUID.randomUUID().toString();
        Random random = new Random();
        return uuid +"-"+random.nextInt();
    }

    private static boolean createNewDocument(String localURL) throws IOException {
        File file = new File(localURL);
        // if file does not exists, then create it
        if (!file.exists()) {
            return file.createNewFile();
        }
        return true;
    }

}
