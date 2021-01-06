package dsic.upv.es;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import java.util.regex.*;

@Path("/BuscarGS")
public class BusquedaGoogleScholar {
	private static WebDriver driver = null;

	private static void GoogleChrome(int fechaI, int fechaF) {
		String exePath = "C:\\Users\\polim\\Desktop\\Universidad\\alberto\\ApiRest\\Resources\\chromedriver.exe";
		System.setProperty("webdriver.chrome.driver", exePath);
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--start-maximized");
		driver = new ChromeDriver(options);
		driver.get("https://scholar.google.es/scholar?start=0&hl=es&as_sdt=0,5&as_ylo=" + fechaI + "&as_yhi=" + fechaF);
	}

	private static String getNextUrl(String url, int page) {
		int nextPage = 10 * page;
		return url.replaceAll("(?<=start=)(.*)(?=&hl)", Integer.toString(nextPage));
	}

	@GET
	@Path("/{fechaI}/{fechaF}")
	@Produces("application/json")
	public Response BuscarGS(@PathParam("fechaI") Integer fechaI, @PathParam("fechaF") Integer fechaF)
			throws Exception {
		
		GoogleChrome(fechaI, fechaF);
		int i = 0;
		int maxPages = 10;
		String publications = "";
		
		try {
			while (i < maxPages) {
				String currentUrl = driver.getCurrentUrl();
				for (int j = 0; j < 11; j++) {
					WebDriverWait waiting = new WebDriverWait(driver, 10);
					waiting.until(ExpectedConditions.presenceOfElementLocated(By.className("gs_scl")));
					List<WebElement> lista = driver.findElements(By.className("gs_scl"));
					if (!lista.get(j).getAttribute("id").equals("gs_asd_frm")) {
						waiting.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@title='Citar']")));
						Thread.sleep(1200);
						lista.get(j).findElement(By.xpath(".//*[@title='Citar']")).click();
						waiting.until(
								ExpectedConditions.elementToBeClickable(By.xpath("//*[text()[contains(.,'BibTeX')]]")));
						List<WebElement> bibtexes = driver.findElements(By.xpath("//*[text()[contains(.,'BibTeX')]]"));
						Thread.sleep(1100);
						bibtexes.get(0).click();
						waiting.until(ExpectedConditions.presenceOfElementLocated(By.tagName("pre")));
						publications = publications + driver.findElement(By.tagName("pre")).getText() + "\n";
						Thread.sleep(1200);
						driver.navigate().back();
						Thread.sleep(900);
						waiting.until(ExpectedConditions.elementToBeClickable(By.id("gs_cit-x")));
						driver.findElement(By.id("gs_cit-x")).click();
			
					}
				}
				i++;
				driver.get(getNextUrl(currentUrl, i));
			}
		} catch (Exception e) {
			return Response.status(200).entity(publications).build();
		}			
		
		return Response.status(200).entity(publications).build();
		/*
		String xmlString = null;
		xmlString = new String(Files.readString(Paths.get("C://Users//polim//Desktop//Universidad//scholar.bib")));
		return Response.status(200).entity(xmlString).build();
		*/
	}
}