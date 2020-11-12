package dsic.upv.es;

import org.json.JSONArray;
import org.json.JSONObject;
public class ExtractorDlbp {
	JSONObject jsonObj;

	public ExtractorDlbp(JSONObject jsonObj) {
		this.jsonObj = jsonObj;
	}
	
	public JSONObject extract() {
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
		System.out.println(dblpJson.toString());
		return dblpJson;
	}

	private JSONObject getPublication(JSONObject article) {
		JSONObject dblpArticle = new JSONObject();
		dblpArticle.put("persona", this.getAuthors(article));
		dblpArticle.put("titulo", this.getTitle(article));
		dblpArticle.put("año", this.getYear(article));
		dblpArticle.put("url", this.getUrl(article));
		return dblpArticle;
	}

	private int getYear(JSONObject article) {
		int year = article.getInt("year");
		return year;
	}
	private String getUrl(JSONObject article) {
		String url = "";
		try {
			url = article.getString("url");
		} catch (Exception e) {}
		return url;
	}
	private String getTitle(JSONObject article) {
		String title = article.get("title").toString();
		return title;
	}
	private JSONArray getAuthors(JSONObject article) {
		JSONArray authors = new JSONArray();
		try {
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
		} catch (Exception e) {
			
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
		surnames = surnames.stripTrailing();
		authorJson.put("nombre", name);
		authorJson.put("apellido", surnames);
		return authorJson;
		
	}
	private JSONObject getNameFromObject(JSONObject authorsJSON) {
		return this.splitAuthorName(authorsJSON.getString("content"));
	}
}
