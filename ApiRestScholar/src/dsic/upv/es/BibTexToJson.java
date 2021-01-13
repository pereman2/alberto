package dsic.upv.es;


import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

public class BibTexToJson {
	private String bibtex;
	private String block;
	private JSONObject jo;
	private JSONObject obj;

	public BibTexToJson(String bibtex) {
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
		for (String block : blocks) {
			this.block = block;
			if (!this.block.trim().equals("")) {
				processBlock();
			}
		}
	}

	private void processBlock() {
		this.obj = new JSONObject();
		String type = getType();
		String[] attributes = getAttributes();
		processObj(attributes);
		switch (type) {
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
		for (String attr : attrs) {
			String[] split = attr.split("=");
			String key = split[0].trim();
			String value;
			if(split[1].charAt(split[1].length() - 1) == ',') {
				value = split[1].trim().substring(1, split[1].length() - 3);
			} else {
				value = split[1].trim().substring(1, split[1].length() - 2);
			}

			value = resolveAccents(value);
			this.obj.put(key, value);
		}
	}
	
	private String resolveAccents(String value) {
		value = value.replaceAll("(\\{[^\\}]*(\\w)\\})", "$2");
		return value;
	}

	private String[] getAttributes() {
		ArrayList<String> res = new ArrayList<String>();
		Pattern pattern = Pattern.compile("(.*=.*)");
		Matcher matcher = pattern.matcher(block);
		while (matcher.find()) {
			if(matcher.group().trim() != "") {
				res.add(matcher.group());
			}
		}
		String[] a = res.toArray(new String[res.size()]);

		return a;
	}

	private String getType() {
		Pattern pattern = Pattern.compile("(^[a-z]*)");
		Matcher matcher = pattern.matcher(block);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return "";
	}

}

