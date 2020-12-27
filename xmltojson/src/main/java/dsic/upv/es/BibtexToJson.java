package dsic.upv.es;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

public class BibtexToJson {
	private String bibtex;
	private String block;
	private JSONObject jo;
	private JSONObject obj;
	
	public BibtexToJson(String bibtex) {
		this.bibtex = bibtex;
	}

	public JSONObject getJson() {
		jo = new JSONObject();
		jo.put("books", new JSONArray());
		jo.put("inproceedings", new JSONArray());
		jo.put("articles", new JSONArray());
		jo.put("incollection", new JSONArray());
		mainLoop();
		return jo;
	}
	
	private void mainLoop() {
		String[] blocks = this.bibtex.split("\n*@");
		for(String block : blocks) {
			this.block = block;
			processBlock();
		}
	}
	
	private void processBlock() {
		this.obj = new JSONObject();
		String type = getType();
		String[] attributes = getAttributes();
		processObj(attributes);
		switch(type) {
			case "book":
				jo.getJSONArray("books").put(obj);
				break;
			case "article":
				jo.getJSONArray("articles").put(obj);
				break;
			case "incollection":
				jo.getJSONArray("incollection").put(obj);
				break;
			case "inproceedings":
				jo.getJSONArray("inproceedings").put(obj);
				break;

		}
	}
	
	private void processObj(String[] attrs) {
		for(String attr : attrs) {
			String[] split = attr.split("=");
			String key = split[0];
			String value = split[1].substring(1, split[1].length() - 1);
			this.obj.put(key, value);
		}
	}
	
	private String[] getAttributes() {
		ArrayList<String> res = new ArrayList<String>();
		Pattern pattern = Pattern.compile("(\n.*=*)*");
		Matcher matcher = pattern.matcher(block);
		while (matcher.find()) {
			res.add(matcher.group());
		}
		return (String[]) res.toArray();
	}
	
	private String getType() {
		Pattern pattern = Pattern.compile("^[a-z]*");
		Matcher matcher = pattern.matcher(block);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return "";
	}
	
}
