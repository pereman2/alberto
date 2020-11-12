package dsic.upv.es;

import java.io. IOException;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONObject;
import org.json.XML;

public class XMLtoJSON{​​
	public static void main(String[] args) throws IOException 
	{
		int PRETTY_PRINT_INDENT_FACTOR = 4;
		String xmlString = null;
		
		String xmlFile = "c:\\datos\\dblp-solo_article.xml";
		xmlString = new String(Files.readAllBytes(Paths.get(xmlFile)));
		JSONObject xmlJSONObject = null;
		xmlJSONObject = XML.toJSONObject(xmlString);
		String jsonFile = "c:\\datos\\dblp.json";
		String jsonPrettyPrintString = null;
		try (FileWriter fileWriter = new FileWriter(jsonFile)){
			fileWriter.write(xmlJSONObject.toString(PRETTY_PRINT_INDENT_FACTOR));
			jsonPrettyPrintString = xmlJSONObject.toString(PRETTY_PRINT_INDENT_FACTOR);
			System.out.println(jsonPrettyPrintString);
		} catch(Exception  e) {}
	}
}
	