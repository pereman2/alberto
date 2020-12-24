package dsic.upv.es;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.json.JSONException;
import org.json.JSONObject;

@Path("/BuscarDBLP")
public class BusquedaDBLP {
	@GET
	@Path("/{fechaI}/{fechaF}")
	@Produces("application/json")
	public Response BuscarDBLP(@PathParam("fechaI") Integer fechaI, @PathParam("fechaF") Integer fechaF)
			throws JSONException {
		String s = "Buscando en dblp entre las fechas " + fechaI + " " + fechaF;
// aquí debe procesar el archivo xml de dblp para convertirlo en json
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("Mensaje", s);
		String result = "@Produces(\"application/json\") Output: \n\nResultados búsqueda: \n\n" + jsonObject;
		return Response.status(200).entity(result).build();
	}
}