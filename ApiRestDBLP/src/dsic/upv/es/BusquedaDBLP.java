package dsic.upv.es;

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

@Path("/BuscarDBLP")
public class BusquedaDBLP {
	private static final String PATH = "C:\\Users\\Administrador\\git\\alberto\\ApiRestDBLP\\Resources\\DBLP-ENTREGA-FINAL.xml";
	private static final int maxPublications= 1000;
	@GET
	@Path("/{fechaI}/{fechaF}")
	@Produces("application/json")
	public Response BuscarDBLP(@PathParam("fechaI") Integer fechaI, @PathParam("fechaF") Integer fechaF)
			throws Exception {
		
		String xmlString = null;
		xmlString = new String(Files.readAllBytes(Paths.get(PATH)), Charset.forName("UTF-8")).replaceAll("[^\\x20-\\x7e]", "");
		Map<String, Object> map = transformToJSON(fechaI, fechaF, xmlString);
		return Response.status(200).entity(map).encoding("UTF-8").build();
	}
	private Map<String, Object> transformToJSON(Integer fechaI, Integer fechaF, String xmlString) {
		JSONObject xmlJSONObject = XML.toJSONObject(xmlString);
		JSONArray res = new JSONArray();
		xmlJSONObject = xmlJSONObject.getJSONObject("dblp");
		JSONArray publicaciones = xmlJSONObject.getJSONArray("article");
		for(int i = 0; i < publicaciones.length() && i < maxPublications; i++) {
			JSONObject publicacion = publicaciones.getJSONObject(i);
			if(publicacion.has("year")) {
				int year = publicacion.getInt("year");
				if(validDate(fechaI, fechaF, year)) {
					res.put(publicacion);
				}
			}
		}
		Map<String, Object> map = new JSONObject().put("dblp", new JSONObject().put("article", res)).toMap();
		return map;
	}
	private boolean validDate(Integer fechaI, Integer fechaF, int year) {
		return year <= fechaF && year >= fechaI;
	}
}