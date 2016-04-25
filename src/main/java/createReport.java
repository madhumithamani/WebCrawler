import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * @author Nisha Narayanaswamy
 * 
 *         Generates the report.html page containing summary of all pages
 *         visited.
 *
 */
public class createReport {

	public void createHTML(PageInfo[] allPages) {

		File htmlTemplateFile = new File("src/main/java/template.html");
		String htmlString = null;

		try {
			htmlString = FileUtils.readFileToString(htmlTemplateFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Add new table rows to template.html file
		try {
			Document document = Jsoup.parse(htmlTemplateFile, "UTF-8");
			Element tableElement = document.select("tbody").first();
			
			for (PageInfo pageObj : allPages) {
				tableElement.append("<tr> </tr>");
				tableElement
						.select("tr")
						.last()
						.append("<td> <a href = " + pageObj.getliveURL() 
								+ " target = '_blank' >"
								+ pageObj.getTitle()
								+ "</td>");
				tableElement
						.select("tr")
						.last()
						.append("<td> <a href = " + pageObj.getlocalURL()
								+ " target = '_blank' >" 
								+ pageObj.getTitle()
								+ "</td>");
				
				tableElement.select("tr").last()
						.append("<td>" + pageObj.getHttpStatusCode() + "</td>");
				
				tableElement.select("tr").last()
						.append("<td>" + pageObj.getLinkCount() + "</td>");
				
				tableElement.select("tr").last()
						.append("<td>" + pageObj.getImgCount() + "</td>");
			}

			File newHtmlFile = new File("src/main/java/report.html");
			try {
				FileUtils.writeStringToFile(newHtmlFile, document.html());
				System.out.println(" ** The report.html file has been updated and will launch automatically **");
				Desktop.getDesktop().browse(newHtmlFile.toURI());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		createReport myHtml = new createReport();
		PageInfo page1 = new PageInfo(
				"https://phonebook.scu.edu/",
				"Santa Clara University",
				8,
				118,
				200,
				"C:/Users/Nisha/Documents/WebCrawlerRepository/9af5cf98-bbde-4c36-9b29-770ae4704ec8-479181225.html");
		PageInfo page2 = new PageInfo(
				"https://www.linkedin.com/edu/santa-clara-university-17914",
				"Santa Clara University | LinkedIn",
				40,
				250,
				200,
				"C:/Users/Nisha/Documents/WebCrawlerRepository/6e00754e-a842-4016-b366-3a6f70360cc7--2135172176.html");
		PageInfo page3 = new PageInfo(
				"https://magazine.scu.edu/webonly.cfm?b=439&c=23875",
				"Santa Clara Magazine - SCM Web Only",
				4,
				69,
				200,
				"C:/Users/Nisha/Documents/WebCrawlerRepository/ac8ef28f-7db5-49bf-a4af-e9b2c10bba9a--285624058.html");
		PageInfo[] allPages = { page1, page2, page3 };
		myHtml.createHTML(allPages);
	}

}
