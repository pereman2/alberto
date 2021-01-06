package dsic.upv.es;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.XML;

@Path("/BuscarDBLP")
public class BusquedaDBLP {
	private static final String path = "C://Users//polim//Desktop//Universidad//alberto//ApiRest//Resources//DBLP-SOLO_ARTICLE-1 (1).XML";
	private static final int maxPublications= 1000;
	@GET
	@Path("/{fechaI}/{fechaF}")
	@Produces("application/json")
	public Response BuscarDBLP(@PathParam("fechaI") Integer fechaI, @PathParam("fechaF") Integer fechaF)
			throws Exception {
		
		String xmlString = null;
		xmlString = new String(Files.readAllBytes(Paths.get(path)), Charset.forName("UTF-8")).replaceAll("[^\\x20-\\x7e]", "");
		JSONObject xmlJSONObject = null;
		xmlJSONObject = XML.toJSONObject(xmlString);
		JSONArray res = new JSONArray();
		xmlJSONObject = xmlJSONObject.getJSONObject("dblp");
		JSONArray publicaciones = xmlJSONObject.getJSONArray("article");
		for(int i = 0; i < publicaciones.length() && i < maxPublications; i++) {
			JSONObject publicacion = publicaciones.getJSONObject(i);
			if(publicacion.has("year")) {
				int year = publicacion.getInt("year");
				if(year <= fechaF && year >= fechaI) {
					res.put(publicacion);
				}
			}
		}
		/*
		try (FileWriter file = new FileWriter(new File("C:\\Users\\polim\\Desktop\\Juegos\\dblp_converted.json"))) {
			 
            file.write(res.toString(4));
            file.flush();
 
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
		Map<String, Object> map = new JSONObject().put("dblp", new JSONObject().put("article", res)).toMap();
		return Response.status(200).entity(map).encoding("UTF-8").build();
	}
}