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
import java.sql.ResultSet;
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

@Path("/BuscarDB")
public class BusquedaDB {
	@GET
	@Path("/{titulo}/{autor}/{fechaI}/{fechaF}/{article}/{congress}/{book}")
	@Produces("application/json")
	public Response BuscarDBLP(@PathParam("titulo") String titulo, @PathParam("autor") String autor,
			@PathParam("fechaI") Integer fechaI, @PathParam("fechaF") Integer fechaF, @PathParam("article") String article,
			@PathParam("congress") String congress, @PathParam("book") String book) throws Exception {
		if(autor.equals(" ")) autor = "";
		if(titulo.equals(" ")) titulo = "";
		boolean articleB = false, congressB = false, bookB = false;
		if(article.equals("true")) {
			articleB = true;
		}
		if(congress.equals("true")) {
			congressB = true;
		}
		if(book.equals("true")) {
			bookB = true;
		}
		JSONArray setList = DataBaseManager.searchDataBase(titulo,autor,fechaI,fechaF,articleB,congressB,bookB);
		return Response.status(200).entity(new JSONObject().put("publicaciones", setList).toMap()).encoding("UTF-8").build();
	}
}