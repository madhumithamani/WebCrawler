import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import org.jsoup.select.NodeVisitor;
import org.jsoup.select.Selector;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DOMParser {

    private static StringBuilder stringBuilder = new StringBuilder();
    private static StringBuilder htmlBuilder = new StringBuilder();

    private static void readHTMLFile(){
        FileReader fileReader = null;
        try {

            String line;
            fileReader = new FileReader("/Users/MadVish/Documents/ttr.html");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while((line = bufferedReader.readLine())!=null){
                line = line.trim();
                if(line.length()>0){
                    stringBuilder.append(line);
                }
            }
            bufferedReader.close();
            //System.out.println(stringBuilder.toString());
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
        document.select("script").remove();
        document.select("style").remove();
        document.select("header").remove();
        document.select("meta").remove();
        traverse(document);
    }

    private static void traverse(Document document){
        computeDensity(document.body().getAllElements().first());
        int Cb = Integer.parseInt(document.body().attr("numOfChar"));
        int LCb = Integer.parseInt(document.body().attr("numOfLinkChar"));
        calculateCompositeTextDensity(document.body().getAllElements().first(), Cb, LCb);
        double threshold = Double.parseDouble(document.body().attr("ctd"));
        //System.out.println(document.body().getAllElements().first());
        System.out.println("Threshold:" + threshold);
        String text = extractText(document.body().getAllElements().first(), threshold);
        //System.out.println(text);
        htmlBuilder.append("</body></html>");
        Document doc = Jsoup.parse(htmlBuilder.toString());
        System.out.println(doc);
        postProcess(doc);
    }

    private static void postProcess(Document doc){
        doc.select("a[href]").remove();
        doc.select("img").remove();
        String text = extractText(doc.body().getAllElements().first(),0.0);
        System.out.println(text);
    }

//    private static void computeDensity(Element node){
//        if(node==null)
//            return;
//        int children = node.children().size();
//        for(Element element : node.children()){
//            computeDensity(element);
//        }
//        NodeInfo nodeInfo = new NodeInfo();
//        if(children == 0)
//            nodeInfo.setNumberOfTags(node.text().length());
//        else
//            nodeInfo.setNumberOfTags(children);
//
//        nodeInfo.setNumberOfChars(node.text().length());
//        nodeInfo.setTextDensity(nodeInfo.getNumberOfChars()/nodeInfo.getNumberOfTags());
//        map.put(node, nodeInfo);
//    }

    private static void computeDensity(Element node){
        if(node == null) {
            return;
        }
        int children = node.children().size();
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
        int tagCount =0 ;
        if(elements!=null && !elements.isEmpty()){
            for(Element element : elements){
                tagCount+=Integer.parseInt(element.attr("numOfTags"));
                tagCount += 1;
            }
            node.attr("numOfTags", String.valueOf(tagCount));
            node.attr("textDensity", String.valueOf(node.text().length()/tagCount));
        }else{
            node.attr("textDensity", String.valueOf(node.text().length()));
            node.attr("numOfTags", "0");
        }
        node.attr("numOfChar", String.valueOf(node.text().length()));
    }

    private static void calculateCompositeTextDensity(Element node,int Cb,int LCb){
        if(node==null)
            return;
        for(Element element : node.children()){
            calculateCompositeTextDensity(element,Cb,LCb);
        }

        int C = Integer.parseInt(node.attr("numOfChar"));
        int T = Integer.parseInt(node.attr("numOfTags"));
        int LC = Integer.parseInt(node.attr("numOfLinkChar"));
        int LT = Integer.parseInt(node.attr("numOfLinks"));
        int nonLC = C-LC;
//        double first=0;
        C = (C==0)?1:C;
        T = (T==0)?1:T;
        LC = (LC==0)?1:LC;
        LT = (LT==0)?1:LT;
//        nonLC = (nonLC==0)?1:nonLC;

       double result= Math.round(C/T * Math.log(C/LC * T/LT));
//        double numerator = first * (Math.log(C/LC * T/LT));
//        first = Math.log(((C/nonLC) * LC) + (LCb/Cb)*C);
//        double denominator = Math.round(Math.log(first));
//        denominator = (denominator==0)?1:denominator;
//        double result = numerator/denominator;
//        System.out.println(result);
        if(result>0)
            node.attr("CTD",String.valueOf(result));
        else
            node.attr("CTD","0");
    }

    private static String extractText(final Element node, final double threshold){
        StringBuilder stringBuilder = new StringBuilder();

        htmlBuilder.append("<html><body>");
        for(Element element : node.children()){
            if(Double.parseDouble(element.attr("ctd")) >= threshold){
                String elemText = element.ownText().trim();
                if(!elemText.isEmpty()){
                    //TODO:Remove the \n if needed.
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

    public static void main(String[] args) {
        DOMParser.readHTMLFile();
        DOMParser.preprocess();
    }
}
