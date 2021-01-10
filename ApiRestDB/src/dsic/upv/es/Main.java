package dsic.upv.es;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

public class Main {

	static String searchApi = "http://localhost:8080/ApiRest";
	public static void main(String[] args) throws JSONException, SQLException, IOException {
		String[] sources = {"ieeex"};
		loadJsons(sources, 2012, 2020);
	}

	private static int extractGS(int yearIni, int yearFi) throws SQLException, IOException {
		String searchLocation = "BuscarGS";
		String urlString = String.format("%sScholar/servicios/%s/%s/%s", searchApi, searchLocation, yearIni, yearFi);
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
		return bibExtractor.extract();
	}


	private static int extractIeeex(int yearIni, int yearFi) throws SQLException, MalformedURLException, ProtocolException, IOException {
		String searchLocation = "BuscarIEEEX";
		String urlString = String.format("%sIEEEX/servicios/%s/%s/%s", searchApi, searchLocation, yearIni, yearFi);
		String dataString = getData(urlString);
		JSONObject data = new JSONObject(dataString);

		ExtractorIeex iexExtractor = new ExtractorIeex(data);
		return iexExtractor.extract();
	}

	private static int extractDblp(int yearIni, int yearFi) throws SQLException, MalformedURLException, ProtocolException, IOException {
		String searchLocation = "BuscarDBLP";
		String urlString = String.format("%sDBLP/servicios/%s/%s/%s", searchApi, searchLocation, yearIni, yearFi);
		String dataString = getData(urlString);
		JSONObject data = new JSONObject(dataString);
		ExtractorDlbp dblpExtractor = new ExtractorDlbp(data);
		return dblpExtractor.extract();
	}
	
	public static JSONObject loadJsons(String[] sources, int yearIni, int yearFi) throws SQLException, IOException {
		JSONObject total = new JSONObject();
		DataBaseManager.eraseDataBase();
		for(String source : sources) {
			if(source.equals("dblp")) {
				total.put("dblp", extractDblp(yearIni, yearFi));
			} else if(source.equals("ieeex")) {
				total.put("ieeex", extractIeeex(yearIni, yearFi));
			} else if(source.equals("scholar")) {
				total.put("scholar", extractGS(yearIni, yearFi));
			}
		}
		return total;
	}

	private static String getData(String urlString)
			throws MalformedURLException, IOException, ProtocolException {
		HttpURLConnection con = null;

		String inputLine;
		String content = "";
		URL url = new URL(urlString);
		con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		con.setDoOutput(true);
		con.setConnectTimeout(5000);
		con.setReadTimeout(100000000);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		while ((inputLine = in.readLine()) != null) {
			content += inputLine.toString() + "\n";
		}
		in.close();
		return content;
	}

}