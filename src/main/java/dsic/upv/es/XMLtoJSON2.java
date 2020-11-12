package dsic.upv.es;

import java.io. IOException;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONObject;
import org.json.XML;

public class XMLtoJSON2 {

	public static void main(String[] args) throws IOException {
		String xmlFile = "/Users/peristocles/uni/iei/lab/dblp-solo_article.xml";
		//String xmlFile = "c:\\datos\\dblp-solo_article.xml";
		JSONObject jsonObj = XMLtoJSON2.transformXMLtoJson(xmlFile);
		ExtractorDlbp extractorDlbp = new ExtractorDlbp(jsonObj);
		JSONObject dblpJson = extractorDlbp.extract();

	}

	private static JSONObject transformXMLtoJson(String path) throws IOException {
		int PRETTY_PRINT_INDENT_FACTOR = 4;
		String xmlString = null;
		xmlString = new String(Files.readAllBytes(Paths.get(path)));
		JSONObject xmlJSONObject = null;
		xmlJSONObject = XML.toJSONObject(xmlString);
		String jsonFile = "/Users/peristocles/uni/iei/lab/dblp.json";
		String jsonPrettyPrintString = null;
		JSONObject obj = new JSONObject();
		try (FileWriter fileWriter = new FileWriter(jsonFile)){
			fileWriter.write(xmlJSONObject.toString(PRETTY_PRINT_INDENT_FACTOR));
			jsonPrettyPrintString = xmlJSONObject.toString(PRETTY_PRINT_INDENT_FACTOR);
			obj = new JSONObject(jsonPrettyPrintString);
			
		} catch(Exception  e) {System.out.println(e); }
		return obj;
	}
}
