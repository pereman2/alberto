package dsic.upv.es;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Main {

	static String searchApi = "http://localhost:8080/RestJerseyEjemplo/servicios";
	public static void main(String[] args) throws JSONException, SQLException, IOException {
		String[] sources = {"ieeex"};
		loadJsons(sources, 2012, 2020);
	}

	private static void extractGS(int yearIni, int yearFi) throws SQLException, IOException {
		String searchLocation = "BuscarGS";
		String urlString = String.format("%s/%s/%s/%s", searchApi, searchLocation, yearIni, yearFi);
		String dataString = getData(urlString);
		BibtexToJson bib2json = new BibtexToJson(dataString);
		JSONObject data = bib2json.getJson();
		try (FileWriter file = new FileWriter(new File("C:\\Users\\polim\\Desktop\\Juegos\\bib.json"))) {
			 
            file.write(data.toString(4));
            file.flush();
 
        } catch (IOException e) {
            e.printStackTrace();
        }
		ExtractorBibtex bibExtractor = new ExtractorBibtex(data);
		bibExtractor.extract();
	}


	private static void extractIeeex(int yearIni, int yearFi) throws SQLException, MalformedURLException, ProtocolException, IOException {
		String searchLocation = "BuscarIEEEX";
		String urlString = String.format("%s/%s/%s/%s", searchApi, searchLocation, yearIni, yearFi);
		String dataString = getData(urlString);
		JSONObject data = new JSONObject(dataString);
		try (FileWriter file = new FileWriter(new File("C:\\Users\\polim\\Desktop\\Juegos\\bib.json"))) {
			 
            file.write(data.toString(4));
            file.flush();
 
        } catch (IOException e) {
            e.printStackTrace();
        }
		ExtractorIeex iexExtractor = new ExtractorIeex(data);
		iexExtractor.extract();
	}

	private static void extractDblp(int yearIni, int yearFi) throws SQLException, MalformedURLException, ProtocolException, IOException {
		String searchLocation = "BuscarDBLP";
		String urlString = String.format("%s/%s/%s/%s", searchApi, searchLocation, yearIni, yearFi);
		String dataString = getData(urlString);
		JSONObject data = new JSONObject(dataString);
		ExtractorDlbp dblpExtractor = new ExtractorDlbp(data);
		dblpExtractor.extract();
	}
	
	public static void loadJsons(String[] sources, int yearIni, int yearFi) throws SQLException, IOException {
		for(String source : sources) {
			if(source.equals("dblp")) {
				extractDblp(yearIni, yearFi);
			} else if(source.equals("ieeex")) {
				extractIeeex(yearIni, yearFi);
			} else if(source.equals("scholar")) {
				extractGS(yearIni, yearFi);
			}
		}
	}
	
	private static JSONObject loadJson(String path) throws FileNotFoundException {
		InputStream is = new FileInputStream(System.getProperty("user.dir") + path);
		JSONTokener tokener = new JSONTokener(is);
		JSONObject object = new JSONObject(tokener);
		return object;
	}

	private static String getData(String urlString)
			throws MalformedURLException, IOException, ProtocolException {
		HttpURLConnection con = null;

		String inputLine;
		StringBuffer content = new StringBuffer();
		URL url = new URL(urlString);
		con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		con.setDoOutput(true);
		con.setConnectTimeout(5000);
		con.setReadTimeout(100000000);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		while ((inputLine = in.readLine()) != null) {
			content.append(inputLine);
		}
		in.close();
		String data = content.toString();
		return data;
	}

}