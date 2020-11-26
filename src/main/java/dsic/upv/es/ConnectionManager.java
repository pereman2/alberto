package dsic.upv.es;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;



public class ConnectionManager {
	private static final String [] convertedPaths = {System.getProperty("user.dir") + "/mapped-data/iex-converted.json"};
	
	private static Connection connection;
	
	private static int idArticulo = 0;
	private static int idComunicacionCongreso = 0;
	private static int idEjemplar = 0;
	private static int idLibro = 0;
	private static int idPersona = 0;
	private static int idPublicacion = 0;
	private static int idRevista = 0;
	
	
	private static void extractAndMapBibtex() {
		
	}
	
	private static void extractAndMapIex() throws FileNotFoundException{
		ExtractorIeex.main(null);
	}
	
	private static void extractAndMapDlbp() {
		
	}
	
	private static void insertIntoDBAll() throws FileNotFoundException, JSONException, SQLException{
		for(String path: convertedPaths) {
			InputStream inputStream = new FileInputStream(path);
			JSONTokener tokener = new JSONTokener(inputStream);
			JSONObject object = new JSONObject(tokener);
			insertIntoDB(object);
			
			
		}
	}
	
	private static void insertIntoDB(JSONObject input) throws JSONException, SQLException {
		JSONArray publicaciones = new JSONArray();
		publicaciones = (JSONArray) input.get("publicaciones");
		for(int i = 0; i < publicaciones.length(); i++) {
			JSONObject publicacion = publicaciones.getJSONObject(i);
			if(publicacion.has("publication_type")) {
				switch(publicacion.getString("publication_type")) {
					case "article":
						processArticle(publicacion);
						break;
					case "conference":
						processConference(publicacion);
						break;
					case "book":
						processBook(publicacion);
						break;
				}
			}else {
				
			}
		}
	}
	
	private static void processArticle(JSONObject article) throws SQLException{
		String idRevis = Integer.toString(idRevista);
		JSONObject ejemplar = article.getJSONObject("ejemplar");
		if(ejemplar.has("revista")) {
			String nombre = ejemplar.getString("revista");
			insertIntoRevistaTable(idRevista++, nombre);
		}else {
			idRevis = null;
		}
		String volumen = "";
		String numero = "";
		String mes = "";
		if(ejemplar.has("volumen")) {
			volumen = ejemplar.getString("volumen");
		}
		if(ejemplar.has("numero")) {
			numero = ejemplar.getString("numero");
		}
		if(ejemplar.has("mes")) {
			mes = ejemplar.getString("mes");
		}
		insertIntoEjemplarTable(idEjemplar, volumen, numero, mes, idRevis);
		String titulo = article.getString("titulo");
		int a�o = article.getInt("a�o");
		String URL = article.getString("URL");
		String paginaInicio = article.getString("pagina_inicio");
		String paginaFin = article.getString("pagina_fin");
		insertIntoPublicacionTable(idPublicacion, titulo, a�o, URL);
		processPersonas(article.getJSONArray("persona"),idPublicacion);
		insertIntoArticuloTable(idArticulo++, paginaInicio, paginaFin, idPublicacion++, idEjemplar++);
	}
	
	private static void processConference(JSONObject conference) throws JSONException, SQLException {
		processPersonas(conference.getJSONArray("persona"),idPublicacion);
	}

	private static void processBook(JSONObject book) throws JSONException, SQLException {
		processPersonas(book.getJSONArray("persona"),idPublicacion);
	}
	
	private static void processPersonas(JSONArray personas, int idPublicacion) throws SQLException {
		for(int i = 0; i < personas.length(); i++) {
			JSONObject persona = personas.getJSONObject(i);
			String nombre = persona.getString("name");
			String apellidos = persona.getString("surname");
			int idPers = checkPersona(nombre, apellidos);
			if(idPers == -1) {
				idPers = idPersona;
				insertIntoPersonaTable(idPersona++, nombre, apellidos);
			}
			insertIntoPublicacionPersonaTable(idPers, idPublicacion);
		}
	}
	
	private static int checkPersona(String nombre, String apellidos) throws SQLException {
		Statement statement = connection.createStatement();
		ResultSet rs = statement.executeQuery("SELECT * FROM persona WHERE (nombre == " + nombre + " AND apellidos == " + apellidos + ")");
		if(rs == null) {
			return -1;
		}else {
			rs.last();
			return rs.getInt("idpersona");
		}
	}
	
	private static void insertIntoArticuloTable(int idArticulo, String paginaInicio, String paginaFin, int idPublicacion, int idEjemplar) throws SQLException {
		Statement query = connection.createStatement();
		query.executeUpdate("INSERT INTO articulo VALUES ("+ idArticulo + "," + "'"+ paginaInicio + "'" + "," + "'" + paginaFin + "'" + "," + idPublicacion + "," + idEjemplar +  ")");
		connection.commit();
	}
	
	private static void insertIntoComunicacionCongresoTable(int idComunicacionCongreso, String congreso, String edicion, String lugar, String paginaInicio, String paginaFin, int idPublicacion) throws SQLException {
		Statement query = connection.createStatement();
		query.executeUpdate("INSERT INTO comunicacioncongreso VALUES (" + idComunicacionCongreso + "," + "'" + congreso + "'" + "," + "'" + edicion + "'" + "," + "'" + lugar + "'" + "," + "'" + paginaInicio + "'" + "," + "'" + paginaFin + "'" + "," + idPublicacion  + ")");
		connection.commit();
	}
	
	private static void insertIntoEjemplarTable(int idEjemplar, String volumen, String numero, String mes, String pertenece) throws SQLException {
		int perteneceInt;
		Statement query = connection.createStatement();
		try {
			perteneceInt = Integer.parseInt(pertenece);
			query.executeUpdate("INSERT INTO ejemplar VALUES (" + idEjemplar + "," + "'" + volumen + "'" +  "," + "'" + numero + "'" + "," + "'" + mes + "'" + "," + perteneceInt + ")");
			connection.commit();
		}catch(Exception e){
			query.executeUpdate("INSERT INTO ejemplar VALUES (" + idEjemplar + "," + "'" + volumen + "'" +  "," + "'" + numero + "'" + "," + "'" + mes + "'" + "," + pertenece + ")");
			connection.commit();
		}
		
	}
	
	private static void insertIntoLibroTable(int idLibro, String editorial, int idPublicacion) throws SQLException {
		Statement query = connection.createStatement();
		query.executeUpdate("INSERT INTO libro VALUES (" + idLibro + "," + "'" + editorial + "'" + "," + idPublicacion + ")");
		connection.commit();
	}
	
	private static void insertIntoPersonaTable(int idPersona, String nombre, String apellidos) throws SQLException {
		Statement query = connection.createStatement();
		query.executeUpdate("INSERT INTO persona VALUES (" + idPersona + "," + "'" + nombre + "'" + ","  + "'" + apellidos + "'" + ")");
		connection.commit();
	}
	
	private static void insertIntoPublicacionTable(int idPublicacion, String titulo, int anyo, String URL) throws SQLException {
		Statement query = connection.createStatement();
		query.executeUpdate("INSERT INTO publicacion VALUES (" + idPublicacion + "," + "'" +  titulo + "'" + ","  + anyo + "," + "'" + URL + "'" + ")");
		connection.commit();
	}
	
	private static void insertIntoPublicacionPersonaTable(int idPublicacion, int idPersona) throws SQLException {
		Statement query = connection.createStatement();
		query.executeUpdate("INSERT INTO publicacionpersona VALUES (" + idPublicacion + "," + idPersona + ")");
		connection.commit();
	}
	
	private static void insertIntoRevistaTable(int idRevista, String nombre) throws SQLException {
		Statement query = connection.createStatement();
		query.executeUpdate("INSERT INTO revista VALUES (" + idRevista + "," + "'" + nombre + "'" + ")");
		connection.commit();
	}

	
	public static void main(String [] args) throws FileNotFoundException{
		//Execute all extractors
		extractAndMapIex();
		
		try
		{
			DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
		   connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/publications?user=root?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "root");
		   connection.setAutoCommit(false);
		   //Statement query = connection.createStatement();		   
		   //query.executeUpdate("INSERT INTO persona VALUES (13,'Manolo','el navajas')");
		   //connection.commit();
		   insertIntoDBAll();
		   connection.close();
		   
		   
		}catch(Exception e) {
			System.out.println(e.toString());
		}
	}
	
}
