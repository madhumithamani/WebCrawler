/**
 * Created by MadVish on 4/14/16.
 */
public class PageInfo {
    private String liveURL = null;
    private String title = null;
    private int imgCount = 0;
    private int linkCount = 0; //valid HTML link count
    private int httpStatusCode = 0;
    private String localURL = null;

    public PageInfo(){
    }

    public PageInfo(String liveURL,String title,int imgCount,int linkCount,int httpStatusCode,String localURL){
        this.liveURL = liveURL;
        this.title = title;
        this.imgCount = imgCount;
        this.linkCount = linkCount;
        this.httpStatusCode = httpStatusCode;
        this.localURL = localURL;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImgCount() {
        return imgCount;
    }

    public void setImgCount(int imgCount) {
        this.imgCount = imgCount;
    }

    public int getLinkCount() {
        return linkCount;
    }

    public void setLinkCount(int linkCount) {
        this.linkCount = linkCount;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public String getlocalURL() {
        return localURL;
    }

    public void setlocalURL(String localURL) {
        this.localURL = localURL;
    }

    public String getliveURL() {
        return liveURL;
    }

    public void setliveURL(String liveURL) {
        this.liveURL = liveURL;
    }
}
