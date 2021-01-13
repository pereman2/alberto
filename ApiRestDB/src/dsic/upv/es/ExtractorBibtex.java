package dsic.upv.es;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

import org.json.*;
public class ExtractorBibtex implements Extractor{
	JSONObject jsonObj;

	public ExtractorBibtex(JSONObject jsonObj) {
		this.jsonObj = jsonObj;
	}
	public int extract() throws JSONException, SQLException {
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
		try {
			return DataBaseManager.insertIntoDB(iexJson);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
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
			String pages = article.getString("pages");
			String[] split = pages.split("--");
			if(split.length > 1) {
				endPage = split[1];
			}
		}
		return endPage;
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
				String name;
				String surnames;
				if(splitName.length > 1) {
					name = splitName[1];
					surnames = splitName[0];
				} else {
					name = splitName[0];
					surnames = "";
				}
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
}
