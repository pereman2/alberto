package dsic.upv.es;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

@Path("/LoadDB")
public class LoadDB {
	@GET
	@Path("/{fechaI}/{fechaF}/{dblp}/{ieeex}/{scholar}")
	@Produces("application/json")
	public Response BuscarDBLP(@PathParam("fechaI") Integer fechaI, @PathParam("fechaF") Integer fechaF, @PathParam("dblp") String dblp,
			@PathParam("ieeex") String ieeex, @PathParam("scholar") String scholar) throws Exception {
		String [] sources = new String [3];
		if(dblp.equals("true")) {
			sources[0] = "dblp";
		}else {
			sources[0] = "";
		}
		if(ieeex.equals("true")) {
			sources[1] = "ieeex";
		}else {
			sources[1] = "";
		}
		if(scholar.equals("true")) {
			sources[2] = "scholar";
		}else {
			sources[2] = "";
		}
		
		JSONObject res = Main.loadJsons(sources, fechaI, fechaF);
		
		return Response.status(200).entity(res.toMap()).build();
	}
}