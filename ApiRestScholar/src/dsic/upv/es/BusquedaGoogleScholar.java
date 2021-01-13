package dsic.upv.es;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@Path("/BuscarGS")
public class BusquedaGoogleScholar {
	private static final int MAX_PAGES = 10;
	private static final int NUM_ELEMENTS = 11;
	private static WebDriver driver = null;
	private static String [] types = {"books","incollection","inproceedings","articles"};

	private static void GoogleChrome(int fechaI, int fechaF) {
		String exePath = "C:\\Users\\Administrador\\git\\alberto\\ApiRestScholar\\Resources\\chromedriver.exe";
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
		
		String publications = loadFromSelenium(fechaI, fechaF);	
		if(publications.equals("")) 
		{
			publications = loadFromFile();
		}
		
		BibTexToJson parser = new BibTexToJson(publications);
		JSONObject data = parser.getJson();
		JSONObject res = new JSONObject();
		for(String type:types) {
			res.put(type, filter(type, data, fechaI, fechaF));
		}
		
		return Response.status(200).entity(res.toMap()).build();
	}
	
	private JSONArray filter(String type, JSONObject input, int startYear, int endYear) {
		JSONArray aux = input.getJSONArray(type);
		JSONArray res = new JSONArray();
		for(int i = 0; i < aux.length(); i++) {
			JSONObject publication = aux.getJSONObject(i);
			if(publication.has("year") && publication.getInt("year") <= endYear &&
					publication.getInt("year") >= startYear) {
				res.put(publication);
			}
		}
		return res;
	}

	private String loadFromFile() throws IOException {
		String xmlString = null;
		xmlString = new String(Files.readString(Paths.get("C:\\Users\\Administrador\\git\\alberto\\ApiRestScholar\\Resources\\scholar.bib")));
		return xmlString;
	}

	private String loadFromSelenium(Integer fechaI, Integer fechaF) {
		String publications = "";
		try {
			GoogleChrome(fechaI, fechaF);
			int i = 0;
			while (i < MAX_PAGES) {
				String currentUrl = driver.getCurrentUrl();
				for (int j = 0; j < NUM_ELEMENTS; j++) {
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
			return publications;
		}
		return publications;
	}
}