package dsic.upv.es;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

@Path("/BuscarIEEEX")
public class BusquedaIEEEX {
	@GET
	@Path("/{fechaI}/{fechaF}")
	@Produces("application/json")
	public Response BuscarIEEX(@PathParam("fechaI") Integer fechaI, @PathParam("fechaF") Integer fechaF)
			throws Exception {
		/*
		HttpURLConnection con = null;
		String apiKey = "efv84mzqq6ydx4dbd59jhdcn";
		
		String inputLine;
		StringBuffer content = new StringBuffer();
		for (int year = fechaI; year <= fechaF; year++) {
			//https://ieeexploreapi.ieee.org/api/v1/search/articles?parameter&apikey=efv84mzqq6ydx4dbd59jhdcn&publication_year={year}&max_records=100
			URL url = new URL("http://ieeexploreapi.ieee.org/api/v1/search/articles?parameter&apikey=" + apiKey + 
					"&publication_year=" + year + "&max_records=20&format=xml");
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setDoOutput(true);
			con.setConnectTimeout(5000);
			con.setReadTimeout(5000);
			
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			while ((inputLine = in.readLine()) != null) {
				content.append(inputLine);
			}
			in.close();
		}
		JSONObject data = XML.toJSONObject(new String(content.toString().getBytes(),Charset.forName("UTF-8")).replaceAll("[^\\x20-\\x7e]", ""));
		
		return Response.status(200).entity(data.toMap()).build();
		
		*/
		String xmlString = null;
		xmlString = new String(Files.readAllBytes(Paths.get("C://Users//polim//Desktop//Universidad//bib.json")), Charset.forName("UTF-8")).replaceAll("[^\\x20-\\x7e]", "");
		return Response.status(200).entity(xmlString).build();
	}
}
