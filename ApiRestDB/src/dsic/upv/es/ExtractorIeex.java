package dsic.upv.es;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

import org.json.*;

public class ExtractorIeex implements Extractor {
	JSONObject jsonObj;

	public ExtractorIeex(JSONObject jsonObj) {
		this.jsonObj = jsonObj;
	}

	public int extract() throws JSONException, SQLException {
		JSONObject iexJson = new JSONObject();
		JSONArray articles = new JSONArray();
		JSONArray aux = this.jsonObj.getJSONArray("articles");
		for (int i = 0; i < aux.length(); i++) {
			JSONArray iexArticles = ((JSONObject) aux.get(i)).getJSONArray("article");
			for (int j = 0; j < iexArticles.length(); j++) {
				JSONObject article = iexArticles.getJSONObject(j);
				JSONObject publication = getPublication(article);
				articles.put(publication);
			}
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

	private JSONObject getPublication(JSONObject article) {
		JSONObject publication = new JSONObject();
		publication.put("persona", this.getAuthors(article));
		publication.put("titulo", this.getTitle(article));
		publication.put("anyo", this.getYear(article));
		publication.put("url", this.getUrl(article));
		String publicationType = article.getString("content_type").toLowerCase();
		if (publicationType.contains("article") || publicationType.contains("journal")) {
			publication.put("publication_type", "article");
			publication.put("pagina_inicio", this.getFirstPage(article));
			publication.put("pagina_fin", this.getEndPage(article));
			publication.put("ejemplar", this.getEjemplar(article));

		} else if (publicationType.contains("conference")) {
			publication.put("publication_type", "conference");
			publication.put("lugar", this.getLugar(article));
			publication.put("pagina_inicio", this.getFirstPage(article));
			publication.put("pagina_fin", this.getEndPage(article));
		} else {
			publication.put("publication_type", "book");
			publication.put("editorial", this.getEditorial(article));
		}
		// dblpArticle.put("pagina_inicio", this.getFirstPage(article));
		// dblpArticle.put("pagina_fin", this.getEndPage(article));
		// dblpArticle.put("ejemplar", this.getEjemplar(article));
		return publication;
	}

	private JSONObject getEjemplar(JSONObject article) {
		JSONObject ejemplar = new JSONObject();
		if (article.has("volume")) {
			ejemplar.put("volumen", article.get("volume"));
		}
		if (article.has("number")) {
			ejemplar.put("numero", article.get("number"));
		}
		if (article.has("publication_date")) {
			ejemplar.put("mes", article.get("publication_date"));
		}
		if (article.has("publication_title")) {
			ejemplar.put("revista", article.get("publication_title"));
		}
		return ejemplar;
	}

	private String getEditorial(JSONObject article) {
		if (article.has("publisher")) {
			return article.getString("publisher");
		}
		return "";
	}

	private String getLugar(JSONObject article) {
		if (article.has("conference_location")) {
			return article.getString("conference_location");
		}
		return "";
	}

	private int getYear(JSONObject article) {
		int year = -1;
		if (article.has("publication_year")) {
			year = article.getInt("publication_year");
		}
		return year;
	}

	private String getFirstPage(JSONObject article) {
		if (article.has("start_page")) {
			try {
				return article.getString("start_page");
			} catch (Exception e) {
				return Integer.toString(article.getInt("start_page"));
			}
		}
		return "";
	}

	private String getEndPage(JSONObject article) {
		if (article.has("end_page")) {
			try {
				return article.getString("end_page");
			} catch (Exception e) {
				return Integer.toString(article.getInt("end_page"));
			}
		}
		return "";
	}

	private String getUrl(JSONObject article) {
		String url = "";
		if (article.has("pdf_url")) {
			url = article.getString("pdf_url");
		}
		return url;
	}

	private String getTitle(JSONObject article) {
		String title = "";
		if (article.has("title")) {
			title = fixApostroph(article.get("title").toString());
		}
		return title;
	}

	private JSONArray getAuthors(JSONObject article) {
		JSONArray authors = new JSONArray();
		if (article.has("authors")) {
			if (!article.get("authors").equals("")) {
				JSONObject authorObj = article.getJSONObject("authors");
				if (authorObj.has("author")) {
					Object authorsObj = authorObj.get("author");
					if (authorsObj instanceof JSONArray) {
						JSONArray authorsJSON = (JSONArray) authorsObj;
						authors = this.getAuthorsFromArray(authorsJSON);
					} else if (authorsObj instanceof JSONObject) {
						JSONObject authorsJSON = (JSONObject) authorsObj;
						authors = this.getAuthorsFromObject(authorsJSON);
					}else {
						authors = this.getAuthorsFromString();

					}
				}
			}else {
				authors = this.getAuthorsFromString();
			}
		}
		return authors;
	}

	private JSONArray getAuthorsFromArray(JSONArray authorsObj) {
		JSONArray authors = new JSONArray();
		for (int i = 0; i < authorsObj.length(); i++) {
			JSONObject authori = authorsObj.getJSONObject(i);
			JSONObject author = new JSONObject();
			String fullname = authori.getString("full_name");
			String[] splitName = fullname.split(" ");
			String name = splitName[0];
			String surnames = "";
			for (int j = 1; j < splitName.length; j++) {
				surnames += splitName[j] + " ";
			}
			author.put("name", fixApostroph(name));
			author.put("surname", fixApostroph(surnames));
			authors.put(author);
		}
		return authors;
	}

	private JSONArray getAuthorsFromObject(JSONObject authorsObj) {
		JSONArray authors = new JSONArray();
		JSONObject author = new JSONObject();
		String fullname = authorsObj.getString("full_name");
		String[] splitName = fullname.split(" ");
		String name = splitName[0];
		String surnames = "";
		for (int j = 1; j < splitName.length; j++) {
			surnames += splitName[j] + " ";
		}
		author.put("name", fixApostroph(name));
		author.put("surname", fixApostroph(surnames));
		authors.put(author);
		return authors;
	}

	private JSONArray getAuthorsFromString() {
		return new JSONArray();
	}

	private static String fixApostroph(String input) {
		String aux = input;
		for (int i = 0; i < aux.length(); i++) {
			if (aux.charAt(i) == '\'') {
				aux = aux.substring(0, i + 1) + "'" + aux.substring(i + 2, aux.length());
				i++;
			}
		}
		return aux;
	}

}
