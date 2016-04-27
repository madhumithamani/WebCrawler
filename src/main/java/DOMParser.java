import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

import java.io.*;

public class DOMParser {

    private static StringBuilder stringBuilder = new StringBuilder();
    private static StringBuilder htmlBuilder = new StringBuilder();
    private static final String OUTPUT_FILE_PATH = "/Users/MadVish/Documents/WSIR/coen272/projectsamples/finalOutput.txt";

    public static void readAllFiles() {
        File folder = new File(PageCrawlerRunnable.REPOSITORY_PATH);
        File[] filesList = folder.listFiles();
        for (File file : filesList) {
            if (file.isFile() && !file.getName().equalsIgnoreCase(".DS_Store")) {
                System.out.println(file.getName());
                System.out.println(file.getAbsolutePath());
                stringBuilder.setLength(0);
                htmlBuilder.setLength(0);
                readHTMLFile(file.getAbsolutePath());
            }
        }
       // readHTMLFile("/Users/MadVish/Documents/ttr.html");
    }

    private static void readHTMLFile(String filePath){
        FileReader fileReader = null;
        try {
            String line;
            fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while((line = bufferedReader.readLine())!=null){
                line = line.trim();
                if(line.length()>0){
                    stringBuilder.append(line);
                }
            }
            bufferedReader.close();
            preprocess();
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

    private static void preprocess(){
        Document document = Jsoup.parse(stringBuilder.toString());
        System.out.println(document.charset());
        document.select("br").remove();
        document.select("script, style, header, footer, nav, meta, comment, CDATA, #comment").remove();
        traverse(document);
    }

    private static void traverse(Document document){
        computeDensity(document.body().getAllElements().first());
        int Cb = Integer.parseInt(document.body().attr("numOfChar"));
        int LCb = Integer.parseInt(document.body().attr("numOfLinkChar"));
        calculateCompositeTextDensity(document.body().getAllElements().first(), Cb, LCb);
        double threshold = Double.parseDouble(document.body().attr("ctd"));
        System.out.println(Integer.parseInt(document.body().attr("numoflinks")));
        System.out.println(Integer.parseInt(document.body().attr("numoflinkchar")));
        System.out.println(Integer.parseInt(document.body().attr("numoftags")));
        System.out.println(Integer.parseInt(document.body().attr("numofchar")));

        System.out.println(document.body().getAllElements().first());
//        System.out.println("Threshold:" + threshold);

        Element maxDensitySumElem = findMaxCTDDensitySumNode(document.body());
        Double maxDensitySum = Double.parseDouble(maxDensitySumElem.attr("ctd_densitysum"));
        System.out.println("Max Density Sum:" + maxDensitySum);
        //System.out.println("Max Density Sum Elem" + maxDensitySumElem.text());
        threshold = printCTDToBody(maxDensitySumElem, document.body());
        System.out.println("New T : " + threshold);


        String text = extractText(document.body().getAllElements().first(), threshold,maxDensitySum);
        StringBuilder finalOutput = new StringBuilder();
        finalOutput.append(document.select("title").text());
        finalOutput.append(text);
        writeOutputToFile(finalOutput.toString());
        System.out.println(finalOutput);

        htmlBuilder.append("</body></html>");
        Document doc = Jsoup.parse(htmlBuilder.toString());
        System.out.println(doc);
    }

    private static void writeOutputToFile(String finalOutput){
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE_PATH, true));
            writer.append(finalOutput);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void computeDensity(Element node){
        if(node == null) {
            return;
        }
        for(Element element : node.children()){
            computeDensity(element);
        }

        Elements elements = node.getElementsByAttribute("href");
        if(elements!=null){
            int linkCount =0 ;
            int linkCharCount=0;
            for(Element element : elements){
                linkCount++;
                linkCharCount+=element.text().length();
            }
            node.attr("numOfLinks", String.valueOf(linkCount));
            node.attr("numOfLinkChar", String.valueOf(linkCharCount));
        }else{
            node.attr("numOfLinks", "0");
            node.attr("numOfLinkChar", "0");
        }

        elements = node.children();
        if(elements!=null && !elements.isEmpty()){
            int tagCount =0 ;
            int densitySum = 0;
            for(Element element : elements){
                densitySum += Integer.parseInt(element.attr("textDensity"));
                tagCount+=Integer.parseInt(element.attr("numOfTags"));
                tagCount += 1;
            }
            node.attr("numOfTags", String.valueOf(tagCount));
            node.attr("textDensity", String.valueOf(node.text().length()/tagCount));
            node.attr("densitySum", String.valueOf(densitySum));
        }else{
            node.attr("textDensity", String.valueOf(node.text().length()));
            node.attr("numOfTags", "0");
            node.attr("densitySum", node.attr("textDensity"));
        }
        node.attr("numOfChar", String.valueOf(node.text().length()));
    }

    private static void calculateCompositeTextDensity(Element node,int Cb,int LCb){
        String CTDDensitySumStr = "CTD_DensitySum";
        if(node==null)
            return;
        double CTD_densitySum = 0D;
        for(Element element : node.children()){
            calculateCompositeTextDensity(element,Cb,LCb);
            CTD_densitySum += Double.parseDouble(element.attr("CTD"));
        }
        node.attr(CTDDensitySumStr, String.valueOf(CTD_densitySum));
        int C = Integer.parseInt(node.attr("numOfChar"));
        int T = Integer.parseInt(node.attr("numOfTags"));
        int LC = Integer.parseInt(node.attr("numOfLinkChar"));
        int LT = Integer.parseInt(node.attr("numOfLinks"));
        int nonLC = C-LC;

        C = (C==0)?1:C;
        T = (T==0)?1:T;
        LC = (LC==0)?1:LC;
        LT = (LT==0)?1:LT;

        double result= Math.round(C/T * Math.log(C/LC * T/LT));

        if(result>0)
            node.attr("CTD",String.valueOf(result));
        else
            node.attr("CTD","0");
    }

    private static Element findMaxDensitySumNode(final Element node){
        Element returnElement = node;
        for (Element element : node.children()) {
            Element maxNode = findMaxDensitySumNode(element);
            int maxNodeDensitySum = Integer.parseInt(maxNode.attr("densitySum"));
            int returnElementDensitySum = Integer.parseInt(returnElement.attr("densitySum"));
            if(maxNodeDensitySum > returnElementDensitySum){
                returnElement = maxNode;
            }
        }
        return returnElement;
    }
    private static double printCTDToBody(Element node, final Element body){
        Element returnNode = node;
        while(node != body){
            System.out.println(node.tagName() + " ::: CTD:" + node.attr("CTD") + ", CTD_densitySum:" + node.attr("CTD_DensitySum") + ", densitySum:" + node.attr("densitySum"));
            if(Double.parseDouble(node.attr("CTD")) < Double.parseDouble(returnNode.attr("CTD"))){
                returnNode = node;
            }
            node = node.parent();

        }
        System.out.println("New threshold:" + returnNode.attr("CTD"));
        return Double.parseDouble(returnNode.attr("CTD"));
    }

    private static Element findMaxCTDDensitySumNode(final Element node){
        Element returnElement = node;
        for (Element element : node.children()) {
            Element maxNode = findMaxCTDDensitySumNode(element);
            double maxNodeDensitySum = Double.parseDouble(maxNode.attr("CTD_DensitySum"));
            double returnElementDensitySum = Double.parseDouble(returnElement.attr("CTD_DensitySum"));
            if(maxNodeDensitySum > returnElementDensitySum){
                returnElement = maxNode;
            }
        }
        return returnElement;
    }

    private static String extractText(final Element node, final double threshold){
        StringBuilder stringBuilder = new StringBuilder();
        htmlBuilder.append("<html><body>");
        for(Element element : node.children()){
            if(Double.parseDouble(element.attr("ctd")) >= threshold){
                String elemText = element.ownText().trim();
                if(!elemText.isEmpty()){
                    stringBuilder.append(elemText).append("\n");
                    htmlBuilder.append(element);
                }
            }
            if(element.children() != null && !element.children().isEmpty()){
                String returnTxt = extractText(element, threshold);
                if(!returnTxt.trim().isEmpty()){
                    stringBuilder.append(returnTxt);
                    //htmlBuilder.append(element);
                }
            }
        }

        return stringBuilder.toString();
    }

    private static String extractText(final Element node, final double threshold,double maxDensitySum){
        StringBuilder stringBuilder = new StringBuilder();
        htmlBuilder.append("<html><body>");
        for(Element element : node.children()) {
            System.out.println("DensitySum:" + element.attr("ctd_densitysum"));
            if (Double.parseDouble(element.attr("ctd_densitysum")) == maxDensitySum) {
                stringBuilder.append(element.text());
                htmlBuilder.append(element);
                element.children().remove();
            }
            else if(Double.parseDouble(element.attr("ctd")) >= threshold && element.parent().attr("href").length()==0) {
                String elemText = element.ownText().trim();
                if (!elemText.isEmpty()) {
                    //TODO:Remove the \n if needed.
                    stringBuilder.append(elemText).append("\n");
                    htmlBuilder.append(element);
                }
            }

            if (element.children() != null && !element.children().isEmpty()) {
                String returnTxt = extractText(element, threshold,maxDensitySum);
                if (!returnTxt.trim().isEmpty()) {
                    stringBuilder.append(returnTxt);
                }

            }

        }

        return stringBuilder.toString();
    }
    public static void main(String[] args) {
        DOMParser.readAllFiles();
    }
}
