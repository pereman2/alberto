package dsic.upv.es;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
public class ExtractorDlbp implements Extractor{
	JSONObject jsonObj;

	public ExtractorDlbp(JSONObject jsonObj) {
		this.jsonObj = jsonObj;
	}
	
	public int extract() throws JSONException, SQLException {
		JSONObject dblpJson = new JSONObject();
		JSONArray articlesDblp = new JSONArray();
		JSONObject dblpObject = (JSONObject) this.jsonObj.get("dblp");
		JSONArray articles = (JSONArray) dblpObject.get("article");
		for(int i = 0; i < articles.length(); i++) {
			JSONObject article = articles.getJSONObject(i);
			JSONObject dblpArticle = getPublication(article);
			articlesDblp.put(dblpArticle);
		}
		dblpJson.put("publicaciones", articlesDblp);
		try {
			return DataBaseManager.insertIntoDB(dblpJson);
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
		JSONObject dblpArticle = new JSONObject();
		dblpArticle.put("persona", this.getAuthors(article));
		dblpArticle.put("titulo", this.getTitle(article));
		dblpArticle.put("anyo", this.getYear(article));
		dblpArticle.put("url", this.getUrl(article));
		dblpArticle.put("pagina_inicio", this.getFirstPage(article));
		dblpArticle.put("pagina_fin", this.getEndPage(article));
		dblpArticle.put("ejemplar", this.getEjemplar(article));
		dblpArticle.put("publication_type", "article");
		return dblpArticle;
	}


	private JSONObject getEjemplar(JSONObject article) {
		JSONObject ejemplar = new JSONObject();
		if(article.has("volume")) {
			String volumen;
			try {
				volumen = Integer.toString(article.getInt("volume"));
			} catch (Exception e) {
				volumen = article.getString("volume");
			}
			ejemplar.put("volumen", volumen);
		}
		if(article.has("number")) {
			String number;
			try {
				number = Integer.toString(article.getInt("number"));
			} catch (Exception e) {
				number = article.getString("number");
			}
			ejemplar.put("numero", number);
		}
		ejemplar.put("revista", article.get("journal"));
		return ejemplar;
	}
	private int getYear(JSONObject article) {
		int year = article.getInt("year");
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
		if(article.has("url")) {
			url = article.getString("url");
		}
		return url;
	}
	private String getTitle(JSONObject article) {
		String title = fixApostroph(article.get("title").toString());
		return title;
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
	
	private JSONArray getAuthors(JSONObject article) {
		JSONArray authors = new JSONArray();
		if(article.has("author")) {
			Object authorsObj = article.get("author");
			if(authorsObj instanceof JSONArray) {
				JSONArray authorsJSON = (JSONArray) authorsObj;
				authors = this.getAuthorsFromArray(authorsJSON);
			} else if(authorsObj instanceof JSONObject) {
				JSONObject authorsJSON = (JSONObject) authorsObj;
				authors = this.getAuthorsFromObject(authorsJSON);
				
			} else if(authorsObj instanceof String) {
				String authorsJSON = (String) authorsObj;
				authors = this.getAuthorsFromString(authorsJSON);

			}
		}
		return authors;
	}

	private JSONArray getAuthorsFromObject(JSONObject authorsJSON) {
		JSONArray authors = new JSONArray();
		JSONObject authorName = this.getNameFromObject(authorsJSON);
		authors.put(authorName);
		return authors;
	}


	private JSONArray getAuthorsFromArray(JSONArray authorsJSON) {
		JSONArray authors = new JSONArray();
		for(int i = 0; i < authorsJSON.length(); i++) {
			Object authorNameObject = (Object) authorsJSON.get(i);
			JSONObject authorJson = new JSONObject();
			if(authorNameObject instanceof JSONObject) {
				authorJson = this.getNameFromObject((JSONObject) authorNameObject);
			} else {
				String name = authorNameObject.toString();
				authorJson = this.splitAuthorName(name);
				
			}
			authors.put(authorJson);

		}
		return authors;
	}
	private JSONArray getAuthorsFromString(String authorsJSON) {
		JSONArray authors = new JSONArray();
		JSONObject author = this.splitAuthorName(authorsJSON);
		authors.put(author);
		return authors;
	}
	private JSONObject splitAuthorName(String author) {
		JSONObject authorJson = new JSONObject();
		String[] authorSplit = author.split(" ");
		String name = authorSplit[0];
		String surnames = "";
		for(int i = 1; i < authorSplit.length; i++) {
			surnames += authorSplit[i] + " ";
		}
		authorJson.put("name", fixApostroph(name));
		authorJson.put("surname", fixApostroph(surnames));
		return authorJson;
		
	}
	private JSONObject getNameFromObject(JSONObject authorsJSON) {
		return this.splitAuthorName(authorsJSON.getString("content"));
	}

}
