import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


public class createReport {

	public void createHTML() {

		File htmlTemplateFile = new File("E:\\HTML5\\testrun\\template.html");
		String htmlString = null;
		String myBaseURI = "E:\\HTML5\\testrun\\template.html";
		
		try {
			htmlString = FileUtils.readFileToString(htmlTemplateFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Add new table rows to template.html file 
		try {
			Document document = Jsoup.parse(htmlTemplateFile, "UTF-8");
			Element tableElement = document.select("tbody").first();
			
			String pt = "http://www.google.com";
			String repo = "E:\\HTML5\\testrun\\doc1.html";
			String targetVal = "_blank";
			
			
			tableElement.append("<tr> </tr>");
			tableElement.select("tr").last().append("<td> <a href = " + pt + " target = targetVal > page title </td>");
			tableElement.select("tr").last().append("<td> <a href = " + repo + " target = targetVal > repo link </td>");
			tableElement.select("tr").last().append("<td> status </td>");
			tableElement.select("tr").last().append("<td> outlinks </td>");
			tableElement.select("tr").last().append("<td> images </td>");
			
			System.out.println(document.html());
			
			//tableEle.append("<tr> <td> row2 </td> <td> repo 2 </td> <td> 403 </td> <td> 5 </td> <td> 5 </td> </tr>");

			File newHtmlFile = new File("E:\\HTML5\\testrun\\report.html");
			try {
				FileUtils.writeStringToFile(newHtmlFile, document.html());
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		createReport myHtml = new createReport();
		myHtml.createHTML();
		System.out.println("report.html file has been updated");
	}

}
