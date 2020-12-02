package dsic.upv.es;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Main {

	static String dblpPath = "/DemoJsons/DBLP-SOLO_ARTICLE_SHORT.json";
	static String iexPath = "ieeeXplore_2018-2020-short.json";
	static String bibtexPath = "bibtex_sample_array.json";

	public static void main(String[] args) throws FileNotFoundException, JSONException, SQLException {
		JSONObject dblpJson = Main.loadJson(dblpPath);
		ExtractorDlbp dblpExtractor = new ExtractorDlbp(dblpJson);
		dblpExtractor.extract();

		JSONObject iexJson = Main.loadJson(iexPath);
		ExtractorIeex iexExtractor = new ExtractorIeex(iexJson);
		iexExtractor.extract();

		JSONObject bibJson = Main.loadJson(bibtexPath);
		ExtractorBibtex bibExtractor = new ExtractorBibtex(bibJson);
		bibExtractor.extract();
	}
	
	private static JSONObject loadJson(String path) throws FileNotFoundException {
		InputStream is = new FileInputStream(System.getProperty("user.dir") + path);
		JSONTokener tokener = new JSONTokener(is);
		JSONObject object = new JSONObject(tokener);
		return object;
		
	}

}