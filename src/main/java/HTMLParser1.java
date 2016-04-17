import org.jsoup.*;
import org.jsoup.nodes.Document;

import javax.print.Doc;
import javax.swing.text.html.HTML;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by MadVish on 4/12/16.
 */
public class HTMLParser1 {
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36";
    private static final String REPOSITORY_PATH =  "/Users/MadVish/Documents/WSIR/coen272/projectsamples";

    public void downloadPage() {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            //fetch the document over HTTP
            System.out.println("Downloading page");
            Connection connection = Jsoup.connect("http://google.com")
                    .userAgent(USER_AGENT)
                    .maxBodySize(0); //allows having an unlimited body size
            Document document = connection.get();
            //check if the connection was successful - when should this be done? after get() or connect()?
            if(connection.response().statusCode() == 200){
                if(connection.response().contentType().contains("text/html"))
                {
                    String location = REPOSITORY_PATH+"/"+document.title()+".html";
                    System.out.println("Page downloaded successfully");
                    savePage(stringBuilder.append(document.html()),location);
                }else
                    System.out.println("Content Type:" + connection.response().contentType());
            }
            else
                System.out.println("error");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void savePage(StringBuilder content,String location) {
        try{
            System.out.println("Saving content to a file");
            if(createNewFile(location)) {
                FileWriter fileWriter = new FileWriter(location);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(content.toString());
                bufferedWriter.close();
                System.out.println("Content saved successfully");
            }
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    private boolean createNewFile(String location) {
        System.out.println("Location:" + location);
        File file = new File(location);
        // if file does not exists, then create it
        if (!file.exists()) {
            try {
                if (file.createNewFile())
                    return true;
                else
                    return false;
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
        return true;
    }

    public static void main(String[] args) {
        HTMLParser1 parser = new HTMLParser1();
        parser.downloadPage();
    }
}
