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



public class DataBaseManager {
	//FIX paths 
	private static final String [] convertedPaths = {System.getProperty("user.dir") + "/mapped-data/iex-converted.json", System.getProperty("user.dir") + "/mapped-data/dblp-converted.json", System.getProperty("user.dir") + "/mapped-data/bibtext-converted.json"};
	private static final String [] tables = {"publicacionpersona", "articulo","ejemplar","revista","comunicacioncongreso", "libro","persona","publicacion"};
	
	private static ConnectionManager connectionManager;
	private static Connection connection;
	
	private static final String URL = "jdbc:mysql://localhost:3306/publications";
	private static final String USER = "root";
	private static final String PASSWORD = "root";
	
	private static int idArticulo = 1;
	private static int idComunicacionCongreso = 1;
	private static int idEjemplar = 1;
	private static int idLibro = 1;
	private static int idPersona = 1;
	private static int idPublicacion = 1;
	private static int idRevista = 1;
	
	
	private static void extractAndMapBibtex() throws FileNotFoundException {
		ExtractorBibtex.main(null);
	}
	
	private static void extractAndMapIex() throws FileNotFoundException{
		ExtractorIeex.main(null);
	}
	
	private static void extractAndMapDlbp() throws FileNotFoundException {
		ExtractorDlbp.main(null);
	}
	
	private static void eraseDataBase() throws SQLException {
		Statement statement = connection.createStatement();
		for(String table:tables) {
			statement.executeUpdate("DELETE FROM " + table);
			connection.commit();
		}
	}
	
	private static void insertIntoDBAll() throws FileNotFoundException, JSONException, SQLException{
		for(String path: convertedPaths) {
			InputStream inputStream = new FileInputStream(path);
			JSONTokener tokener = new JSONTokener(inputStream);
			JSONObject object = new JSONObject(tokener);
			insertIntoDB(object);	
		}
	}
	
	public static void insertIntoDB(JSONObject input) throws JSONException, SQLException {
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
				processArticle(publicacion);
			}
		}
	}
	
	private static void processArticle(JSONObject article) throws SQLException{
		String idRevis = Integer.toString(idRevista);
		String titulo = article.getString("titulo");
		JSONObject ejemplar = article.getJSONObject("ejemplar");
		if(!checkDuplicatedPublication(titulo)) {
		if(ejemplar.has("revista")) {
			String nombre = ejemplar.getString("revista");
			try {
				insertIntoRevistaTable(idRevista++, nombre);
			}catch(Exception e) {
				idRevis = Integer.toString(findRevista(nombre));
				idRevista--;
			}
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
		int anyo = article.getInt("anyo");
		String URL = "";
		if(article.has("url")) {
			URL = article.getString("url");
		}
		String paginaInicio = article.getString("pagina_inicio");
		String paginaFin = article.getString("pagina_fin");
		insertIntoPublicacionTable(idPublicacion, titulo, anyo, URL);
		processPersonas(article.getJSONArray("persona"),idPublicacion);
		insertIntoArticuloTable(idArticulo++, paginaInicio, paginaFin, idPublicacion++, idEjemplar++);
		}
	}
	
	private static void processConference(JSONObject conference) throws JSONException, SQLException {
		String titulo = conference.getString("titulo");
		int anyo = conference.getInt("anyo");
		String URL = "";
		if(conference.has("url")) {
			URL = conference.getString("url");
		}
		if(!checkDuplicatedPublication(titulo)) {
			insertIntoPublicacionTable(idPublicacion, titulo, anyo, URL);
			String congreso = "";
			String edicion = "";
			String lugar = "";
			String paginaInicio = conference.getString("pagina_inicio");;
			String paginaFin = conference.getString("pagina_fin");;
			if(conference.has("congreso")) {
				congreso = conference.getString("congreso");
			}
			if(conference.has("edicion")) {
				edicion = conference.getString("edicion");
			}
			if(conference.has("lugar")) {
				lugar = conference.getString("lugar");
			}
		
			insertIntoComunicacionCongresoTable(idComunicacionCongreso++, congreso, edicion, lugar, paginaInicio, paginaFin, idPublicacion);
			processPersonas(conference.getJSONArray("persona"),idPublicacion++);
		}
	}

	private static void processBook(JSONObject book) throws JSONException, SQLException {
		String titulo = book.getString("titulo");
		int anyo = book.getInt("anyo");
		String URL = "";
		if(book.has("url")) {
			URL = book.getString("url");
		}
		String editorial = "";
		if(book.has("editorial")) {
			editorial = book.getString("editorial");
		}
		if(!checkDuplicatedPublication(titulo)) {
			insertIntoPublicacionTable(idPublicacion, titulo, anyo, URL);
			insertIntoLibroTable(idLibro++, editorial, idPublicacion);
			processPersonas(book.getJSONArray("persona"),idPublicacion++);
		}
	}
	
	private static void processPersonas(JSONArray personas, int idPublicacion) throws SQLException {
		for(int i = 0; i < personas.length(); i++) {
			JSONObject persona = personas.getJSONObject(i);
			String nombre = "";
			String apellidos = "";
			if(persona.has("name")){
					nombre = persona.getString("name");
			}
			if(persona.has("surname")) {	
				apellidos = persona.getString("surname");
			}
			int idPers = checkPersona(nombre, apellidos);
			if(idPers == -1) {
				idPers = idPersona;
				insertIntoPersonaTable(idPersona++, nombre, apellidos);
			}
			if(!checkDuplicatedRowPPTable(idPublicacion, idPers)) {
				insertIntoPublicacionPersonaTable(idPublicacion, idPers);
			}
		}
	}
	
	private static boolean checkDuplicatedPublication(String titulo) throws SQLException {
		Statement statement = connection.createStatement();
		ResultSet rs = statement.executeQuery("SELECT COUNT(*) AS rowcount FROM publicacion WHERE titulo = '" + titulo + "'");
		rs.next();
		int count = rs.getInt("rowcount");
		if(count == 0) {
			return false;
		}else {
			return true;
		}
	}
	
	
	private static boolean checkDuplicatedRowPPTable(int idPublicacion, int idPers) throws SQLException {
		Statement statement = connection.createStatement();
		ResultSet rs = statement.executeQuery("SELECT COUNT(*) AS rowcount FROM publicacionpersona WHERE (idpublicacion = " + idPublicacion + " AND idpersona = " + idPers + ")");
		rs.next();
		int count = rs.getInt("rowcount");
		if(count == 0) {
			return false;
		}else {
			return true;
		}
	}
	
	private static int findRevista(String nombre) throws SQLException {
		Statement statement = connection.createStatement();
		ResultSet rs = statement.executeQuery("SELECT * FROM revista WHERE nombre = '" + nombre + "'" );
		rs.next();
		return rs.getInt("idrevista");
	}
	
	private static int checkPersona(String nombre, String apellidos) throws SQLException {
		Statement statement = connection.createStatement();
		ResultSet rs = statement.executeQuery("SELECT COUNT(*) AS rowcount FROM persona WHERE (nombre = '" + nombre + "' AND apellidos = '" + apellidos + "')");
		rs.next();
		int count = rs.getInt("rowcount");
		if(count == 0) {
			return -1;
		}else {
			rs = statement.executeQuery("SELECT * FROM persona WHERE (nombre = '" + nombre + "' AND apellidos = '" + apellidos + "')");
			rs.next();
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
		System.out.println("Starting...");
		extractAndMapIex();
		extractAndMapBibtex();
		extractAndMapDlbp();
		
		try
		{
			connectionManager = new ConnectionManager();
		   connection = connectionManager.getConnection(URL, USER, PASSWORD);
		   connection.setAutoCommit(false);
		   eraseDataBase();
		   insertIntoDBAll();
		   connection.close();
		   System.out.println("Done! inserted " + (--idPublicacion) + " new publications" );
		   
		}catch(Exception e) {
			System.out.println(e.toString());
		}
	}
	
}
