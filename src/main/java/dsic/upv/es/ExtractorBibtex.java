package dsic.upv.es;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;

import org.json.*;
public class ExtractorBibtex implements Extractor{
	JSONObject jsonObj;

	public ExtractorBibtex(JSONObject jsonObj) {
		this.jsonObj = jsonObj;
	}
	public JSONObject extract() {
		JSONObject iexJson = new JSONObject();
		JSONArray articles = new JSONArray();
		JSONArray iexArticles = new JSONArray();
		if(this.jsonObj.has("articles")) {
			iexArticles = (JSONArray) this.jsonObj.get("articles");
		}
		for(int i = 0; i < iexArticles.length(); i++) {
			JSONObject article = iexArticles.getJSONObject(i);
			JSONObject publication = getPublication(article);
			articles.put(publication);
		}
		if(this.jsonObj.has("books")) {
			iexArticles = (JSONArray) this.jsonObj.get("books");
		}
		for(int i = 0; i < iexArticles.length(); i++) {
			JSONObject article = iexArticles.getJSONObject(i);
			JSONObject publication = getBook(article);
			articles.put(publication);
		}
		if(this.jsonObj.has("inproceedings")) {
			iexArticles = (JSONArray) this.jsonObj.get("inproceedings");
		}
		for(int i = 0; i < iexArticles.length(); i++) {
			JSONObject article = iexArticles.getJSONObject(i);
			JSONObject publication = getCongress(article);
			articles.put(publication);
		}
		if(this.jsonObj.has("incollections")) {
			iexArticles = (JSONArray) this.jsonObj.get("books");
		}
		for(int i = 0; i < iexArticles.length(); i++) {
			JSONObject article = iexArticles.getJSONObject(i);
			JSONObject publication = getBook(article);
			articles.put(publication);
		}
		iexJson.put("publicaciones", articles);
		return iexJson;
	}
	private JSONObject getCongress(JSONObject article) {
		JSONObject publication = new JSONObject();
		publication.put("persona", this.getAuthors(article));
		publication.put("titulo", this.getTitle(article));
		publication.put("anyo", this.getYear(article));
		publication.put("congreso", this.getCongressName(article));
		publication.put("pagina_inicio", this.getFirstPage(article));
		publication.put("pagina_fin", this.getEndPage(article));
		publication.put("publication_type", "conference");
		return publication;
	}
	private String getCongressName(JSONObject article) {
		if(article.has("booktitle")) {
			return article.getString("booktitle");
		}
		return "";
	}
	private JSONObject getBook(JSONObject article) {
		JSONObject publication = new JSONObject();
		publication.put("persona", this.getAuthors(article));
		publication.put("titulo", this.getTitle(article));
		publication.put("anyo", this.getYear(article));
		publication.put("editorial", this.getEditorial(article));
		publication.put("publication_type", "book");
		return publication;
	}
	private JSONObject getPublication(JSONObject article) {
		JSONObject publication = new JSONObject();
		publication.put("publication_type", "article");
		publication.put("persona", this.getAuthors(article));
		publication.put("titulo", this.getTitle(article));
		publication.put("anyo", this.getYear(article));
		publication.put("pagina_inicio", this.getFirstPage(article));
		publication.put("pagina_fin", this.getEndPage(article));
		publication.put("ejemplar", this.getEjemplar(article));
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
		if(article.has("year")) {
			year = article.getInt("year");
		}
		return year;
	}
	private String getFirstPage(JSONObject article) {
		if(article.has("pages")) {
			return article.getString("pages").split("--")[0];
		}
		return "";
	}
	private String getEndPage(JSONObject article) {
		String endPage = "";
		if(article.has("pages")) {
			endPage = article.getString("pages").split("--")[1];
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
			title = fixApostroph(article.get("title").toString());
		}
		return title;
	}
	private JSONArray getAuthors(JSONObject article) {
		JSONArray authors = new JSONArray();
		if(article.has("author")) {
			String[] authorsNames = article.getString("author").split(" and ");
			for(int i = 0; i < authorsNames.length; i++) {
				JSONObject author = new JSONObject();
				String fullname = authorsNames[i];
				String[] splitName = fullname.split(", ");
				String name = splitName[1];
				String surnames = splitName[0];
				author.put("name", fixApostroph(name));
				author.put("surname", fixApostroph(surnames));
				authors.put(author);
			}
		}
		return authors;
	}
	
	private static String fixApostroph(String input) {
		String aux = input;
		for(int i = 0; i < aux.length(); i++) {
			if(aux.charAt(i) == '\'') {
				aux = aux.substring(0, i + 1) + "'" + aux.substring(i + 2, aux.length());
				i++;
			}
		}
		return aux;
	}

	public static void main(String[] args) throws FileNotFoundException {
		String jsonPath = System.getProperty("user.dir") + "/DemoJsons/bibtex_sample_array.json";
		InputStream is = new FileInputStream(jsonPath);
		JSONTokener tokener = new JSONTokener(is);
		JSONObject object = new JSONObject(tokener);
		ExtractorBibtex ex = new ExtractorBibtex(object);
		JSONObject transformedJson = ex.extract();
		int PRETTY_PRINT_INDENT_FACTOR = 4;
		String jsonFile = System.getProperty("user.dir") + "/mapped-data/bibtext-converted.json";
		try (FileWriter fileWriter = new FileWriter(jsonFile)){
			fileWriter.write(transformedJson.toString(PRETTY_PRINT_INDENT_FACTOR));
			fileWriter.close();
			is.close();

		} catch(Exception  e) {
			System.out.println(e); 
		}
	}
}
