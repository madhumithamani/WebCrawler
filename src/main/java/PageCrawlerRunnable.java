import org.jsoup.Connection;
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
    private static final String REPOSITORY_PATH =  "/Users/MadVish/Documents/WSIR/coen272/projectsamples/repository";
    public static PageInfo[] pageInfoCollection;
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
        startCrawlProcess();
    }

    private void startCrawlProcess(){
        //1. download the page content
        //2. get the title of the page
        //2. get the valid URLs (html links) to crawl
        //3. check against robots.txt file by passing the domain name & url
        //4. get the link count (only valid URLs or all URLs?)
        //5. get the image count
        //6. generate a name for the page
        //7. create an html file and store it
        System.out.println(crawlURL);
        Connection connection = Jsoup.connect(crawlURL)
                .userAgent(USER_AGENT)
                .maxBodySize(0); //allows having an unlimited body size
        try {
            Document document = connection.get();
            PageInfo pageInfo = new PageInfo();
            pageInfo.setliveURL(crawlURL);
            pageInfo.setHttpStatusCode(connection.response().statusCode());
            crawlManager.completed(crawlURL);
            if(pageInfo.getHttpStatusCode() == 200){
                processPage(document, pageInfo);
            }

        }catch (IOException ex){
            ex.printStackTrace();
        }
    }
    private void processPage(Document document, PageInfo pageInfo){
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

    private static void savePage(String content, PageInfo pageInfo) {
        String localDocName = generateLocalDocName();
        String localURL = REPOSITORY_PATH+"/"+localDocName+".html";
//        System.out.println("DocName:" + localDocName);
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
//                System.out.println("Content saved successfully");
            }catch(IOException ex){
                ex.printStackTrace();
            } finally {
                if(fileWriter  != null)
                    try {
                        fileWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }

        }
    }

    private static String generateLocalDocName(){
        //http://gregszabo.blogspot.com/2010/01/generating-unique-file-names-with.html
        String uuid = UUID.randomUUID().toString();
//        String DATE_FORMAT="yyyyMMdd_HHmmss-SSS";
//        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(DATE_FORMAT);
        Random random = new Random();
        return uuid +"-"+random.nextInt();
    }

    private static boolean createNewDocument(String localURL){
//        System.out.println("Location:" + localURL);
        File file = new File(localURL);
        // if file does not exists, then create it
        if (!file.exists()) {
            try {
                return file.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
        return true;
    }

}
