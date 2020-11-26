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
		String publicationType = article.getString("content_type").toLowerCase();
		if(publicationType.contains("article") || publicationType.contains("journal")) {
			publication.put("publication_type", "article");
			publication.put("pagina_inicio", this.getFirstPage(article));
			publication.put("pagina_fin", this.getEndPage(article));
			publication.put("ejemplar", this.getEjemplar(article));

		} else if(publicationType.contains("conference")) {
			publication.put("publication_type", "conference");
			publication.put("lugar", this.getLugar(article));
			publication.put("pagina_inicio", this.getFirstPage(article));
			publication.put("pagina_fin", this.getEndPage(article));
		} else {
			publication.put("publication_type", "book");
			publication.put("editorial", this.getEditorial(article));
		}
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
		if(article.has("publication_date")) {
			ejemplar.put("mes", article.get("publication_date"));
		}
		if(article.has("journal")) {
			ejemplar.put("revista", article.get("journal"));
		}
		return ejemplar;
	}
	private String getEditorial(JSONObject article) {
		if(article.has("publisher")) {
			return article.getString("publisher");
		}
		return "";
	}
	private String getLugar(JSONObject article) {
		if(article.has("conference_location")) {
			return article.getString("conference_location");
		}
		return "";
	}
	private int getYear(JSONObject article) {
		int year = -1;
		if(article.has("publication_year")) {
			year = article.getInt("publication_year");
		}
		return year;
	}
	private String getFirstPage(JSONObject article) {
		if(article.has("start_page")) {
			return article.getString("start_page");
		}
		return "";
	}
	private String getEndPage(JSONObject article) {
		String endPage = "";
		if(article.has("end_page")) {
			endPage = article.getString("end_page");
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
						surnames += splitName[j] + " ";
					}
					author.put("name", name);
					author.put("surname", surnames);
					authors.put(author);
				}
			}
		}
		return authors;
	}

	public static void main(String[] args) throws FileNotFoundException {
		String jsonPath = System.getProperty("user.dir") + "/DemoJsons/ieeeXplore_2018-2020-short.json";
		InputStream is = new FileInputStream(jsonPath);
		JSONTokener tokener = new JSONTokener(is);
		JSONObject object = new JSONObject(tokener);
		ExtractorIeex ex = new ExtractorIeex(object);
		JSONObject transformedJson = ex.extract();
		int PRETTY_PRINT_INDENT_FACTOR = 4;
		String jsonFile = System.getProperty("user.dir") + "/mapped-data/iex-converted.json";
		try (FileWriter fileWriter = new FileWriter(jsonFile)){
			fileWriter.write(transformedJson.toString(PRETTY_PRINT_INDENT_FACTOR));

		} catch(Exception  e) {System.out.println(e); }

	}
}
