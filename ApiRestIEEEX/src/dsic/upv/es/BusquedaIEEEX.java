package dsic.upv.es;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

@Path("/BuscarIEEEX")
public class BusquedaIEEEX {
	private static final String IEEEX_JSON_PATH = "C:\\Users\\Administrador\\git\\alberto\\ApiRestIEEEX\\Resources\\bib.json";

	@GET
	@Path("/{fechaI}/{fechaF}")
	@Produces("application/json")
	public Response BuscarIEEEX(@PathParam("fechaI") Integer fechaI, @PathParam("fechaF") Integer fechaF)
			throws Exception {
		// Si no se puede usar por problemas de carga con IEEEX cambiar a loadFromFile
		Map<String, Object> map = loadFromIeeex(fechaI, fechaF);
		// Map<String, Object> map = loadFromFile(fechaI, fechaF);
		
		return Response.status(200).entity(map).build();
	}

	private Map<String, Object> loadFromIeeex(Integer fechaI, Integer fechaF)
			throws MalformedURLException, IOException, ProtocolException {
		HttpURLConnection con = null;
		String apiKey = "efv84mzqq6ydx4dbd59jhdcn";
		
		String inputLine;
		StringBuffer content = new StringBuffer();
		for (int year = fechaI; year <= fechaF; year++) {
			URL url = new URL("http://ieeexploreapi.ieee.org/api/v1/search/articles?parameter&apikey=" + apiKey + 
					"&publication_year=" + year + "&max_records=5&format=xml");
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
		return data.toMap();
	}

	private Map<String, Object> loadFromFile(Integer fechaI, Integer fechaF) throws IOException {
		String xmlString = null;
		xmlString = new String(Files.readAllBytes(Paths.get(IEEEX_JSON_PATH)), 
				Charset.forName("UTF-8")).replaceAll("[^\\x20-\\x7e]", ""); // Transformar formateo de acentos de xml a utf-8
		
		JSONObject xmlJSONObject = new JSONObject(xmlString);
		JSONArray res = new JSONArray();
		JSONArray articulos = new JSONArray();
		JSONArray publicaciones = xmlJSONObject.getJSONArray("articles");
		loadAllArticles(articulos, publicaciones);
		JSONArray article = filterPublicaciones(fechaI, fechaF, articulos);
		res.put(new JSONObject().put("article", article));
		Map<String, Object> map = new JSONObject().put("articles", res).toMap();
		return map;
	}

	private JSONArray filterPublicaciones(Integer fechaI, Integer fechaF, JSONArray articulos) {
		JSONArray article = new JSONArray();
		for(int i = 0; i < articulos.length() ; i++) {
			JSONObject publicacion = articulos.getJSONObject(i);
			if(publicacion.has("publication_year")) {
				int year = publicacion.getInt("publication_year");
				if(year <= fechaF && year >= fechaI) {
					article.put(publicacion);
				}
			}
		}
		return article;
	}

	private void loadAllArticles(JSONArray articulos, JSONArray publicaciones) {
		for(int i = 0; i < publicaciones.length(); i++) {
			JSONArray iexArticles = ((JSONObject) publicaciones.get(i)).getJSONArray("article");
			for (int j = 0; j < iexArticles.length(); j++) {
				JSONObject article = iexArticles.getJSONObject(j);
				articulos.put(article);
			}
		}
	}
}
