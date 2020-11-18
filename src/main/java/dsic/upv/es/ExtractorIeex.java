package dsic.upv.es;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;

import org.json.*;
public class ExtractorIeex {
	JSONObject jsonObj;

	public ExtractorIeex(JSONObject jsonObj) {
		this.jsonObj = jsonObj;
	}
	public JSONObject extract() {
		JSONObject iexJson = new JSONObject();
		JSONArray articles = new JSONArray();
		JSONArray iexArticles = (JSONArray) this.jsonObj.get("articles");
		System.out.println(articles.toString());
		for(int i = 0; i < iexArticles.length(); i++) {
			JSONObject article = iexArticles.getJSONObject(i);
			JSONObject publication = getPublication(article);
			articles.put(publication);
		}
		iexJson.put("publicaciones", articles);
		return iexJson;
	}
	private JSONObject getPublication(JSONObject article) {
		JSONObject publication = new JSONObject();
		publication.put("persona", this.getAuthors(article));
		publication.put("titulo", this.getTitle(article));
		publication.put("año", this.getYear(article));
		publication.put("url", this.getUrl(article));
		//dblpArticle.put("pagina_inicio", this.getFirstPage(article));
		//dblpArticle.put("pagina_fin", this.getEndPage(article));
		//dblpArticle.put("ejemplar", this.getEjemplar(article));
		return publication;
	}
	private JSONObject getEjemplar(JSONObject article) {
		JSONObject ejemplar = new JSONObject();
		if(article.has("volume")) {
			ejemplar.put("volumen", article.get("volume"));
		}
		if(article.has("number")) {
			ejemplar.put("numero", article.get("number"));
		}
		ejemplar.put("revista", article.get("journal"));
		return ejemplar;
	}
	private int getYear(JSONObject article) {
		int year = -1;
		if(article.has("publication_year")) {
			year = article.getInt("publication_year");
		}
		return year;
	}
	private String getFirstPage(JSONObject article) {
		String firstPage = "";
		if(article.has("pages")) {
			Object pagesObj = article.get("pages");
			if(pagesObj instanceof String) {
				String pages = article.getString("pages");
				firstPage = pages.split("-")[0];
			} else if(pagesObj instanceof Integer) {
				firstPage = "1";
			}
		}
		return firstPage;
	}
	private String getEndPage(JSONObject article) {
		String endPage = "";
		if(article.has("pages")) {
			Object pagesObj = article.get("pages");
			if(pagesObj instanceof String) {
				String pages = article.getString("pages");
				endPage = pages.split("-")[0];
			} else if(pagesObj instanceof Integer) {
				int pages = article.getInt("pages");
				endPage = Integer.toString(pages);

			}
		}
		return endPage;
	}
	private String getUrl(JSONObject article) {
		String url = "";
		if(article.has("html_url")) {
			url = article.getString("html_url");
		}
		return url;
	}
	private String getTitle(JSONObject article) {
		String title = "";
		if(article.has("title")) {
			title = article.get("title").toString();
		}
		return title;
	}
	private JSONArray getAuthors(JSONObject article) {
		JSONArray authors = new JSONArray();
		if(article.has("authors")) {
			JSONObject authorObj = article.getJSONObject("authors");
			if(authorObj.has("authors")) {
				JSONArray authorsObj = authorObj.getJSONArray("authors");
				for(int i = 0; i < authorsObj.length(); i++) {
					JSONObject authori = authorsObj.getJSONObject(i);
					JSONObject author = new JSONObject();
					String fullname = authori.getString("full_name");
					String[] splitName = fullname.split(" ");
					String name = splitName[0];
					String surnames = "";
					for(int j = 1; j < splitName.length; j++) {
						surnames += splitName[i] + " ";
					}
					author.put("name", name);
					author.put("surname", surnames);
					authors.put(author);
				}
			}
		}
		return authors;
	}

	public static void main(String[] args) {
		String jsonPath = "/Users/peristocles/uni/iei/lab/ieeexplore.json";
		try {
			InputStream is = new FileInputStream(jsonPath);
			JSONTokener tokener = new JSONTokener(is);
			JSONObject object = new JSONObject(tokener);
			ExtractorIeex ex = new ExtractorIeex(object);
			JSONObject transformedJson = ex.extract();
			int PRETTY_PRINT_INDENT_FACTOR = 4;
			String jsonFile = "/Users/peristocles/uni/iei/lab/iex-converted.json";
			try (FileWriter fileWriter = new FileWriter(jsonFile)){
				fileWriter.write(transformedJson.toString(PRETTY_PRINT_INDENT_FACTOR));
				
			} catch(Exception  e) {System.out.println(e); }
		} catch(Exception  e) {System.out.println(e); }

	}
}
