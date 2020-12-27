package dsic.upv.es;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.google.common.base.Charsets;

public class DataBaseManager {
	// FIX paths
	private static final String[] convertedPaths = { System.getProperty("user.dir") + "/mapped-data/iex-converted.json",
			System.getProperty("user.dir") + "/mapped-data/dblp-converted.json",
			System.getProperty("user.dir") + "/mapped-data/bibtext-converted.json" };
	private static final String[] tables = { "publicacionpersona", "articulo", "ejemplar", "revista",
			"comunicacioncongreso", "libro", "persona", "publicacion" };

	private static ConnectionManager connectionManager;
	private static Connection connection;

	private static final String URL = "jdbc:mysql://localhost:3306/publications?useUnicode=yes";
	private static final String USER = "root";
	private static final String PASSWORD = "root";

	private static int idArticulo = 1;
	private static int idComunicacionCongreso = 1;
	private static int idEjemplar = 1;
	private static int idLibro = 1;
	private static int idPersona = 1;
	private static int idPublicacion = 1;
	private static int idRevista = 1;

	public static void main(String[] args) throws Exception {
		// Execute all extractors
		searchDataBase("","",2012,2020,true,true,true);
		connect();
		eraseDataBase();
	}

	public static JSONArray searchDataBase(String titulo, String autor, int startYear, int endYear, boolean article,
			boolean congress, boolean book) throws Exception {
		JSONArray res = new JSONArray();
		ResultSet aux;
		connect();

		if (article) {
			String query = "SELECT DISTINCT pu.idpublicacion, pu.titulo, r.nombre  as \"revista\", e.volumen, e.numero, e.mes, pu.anyo, a.inicio, a.fin, pu.URL \r\n"
					+ "FROM ((((publicacion pu LEFT JOIN publicacionpersona pupe ON pu.idpublicacion = pupe.idpublicacion) \r\n"
					+ "LEFT JOIN persona pe ON pe.idpersona = pupe.idpersona) \r\n"
					+ "LEFT JOIN articulo a ON pu.idpublicacion = a.idpublicacion) \r\n"
					+ "LEFT JOIN ejemplar e ON a.esta_en = e.idejemplar) \r\n"
					+ "LEFT JOIN revista r ON r.idrevista = e.pertenece_a\r\n"
					+ " WHERE a.idarticulo IS NOT NULL AND pu.titulo LIKE '%?%' AND (pe.nombre LIKE \"%?%\" OR pe.apellidos LIKE \"%?%\")";
			query = addParemetersToSearch(query, startYear, endYear);
			PreparedStatement preStatement = connection.prepareStatement(query);
			preStatement.setString(1, titulo);
			preStatement.setString(2, autor);
			preStatement.setString(3, autor);

			aux = preStatement.executeQuery();
			
			JSONArray articles = new JSONArray();
			while(aux.next()) {
				JSONObject art = new JSONObject();
				art.put("authors", getAuthors(aux.getInt("idpublicacion")));
				art.put("titulo", aux.getString("titulo"));
				art.put("revista", aux.getString("revista"));
				art.put("volumen", aux.getString("volumen"));
				art.put("numero", aux.getString("numero"));
				art.put("mes", aux.getString("mes"));
				art.put("anyo", aux.getInt("anyo"));
				art.put("inicio", aux.getString("inicio"));
				art.put("fin",aux.getString("fin"));
				art.put("URL", aux.getString("URL"));
				articles.put(art);
			}
			
			res.put(articles);
		}
		
		if (congress) {
			String query = "SELECT DISTINCT pu.idpublicacion, pu.titulo, cc.edicion, cc.congreso, cc.lugar, pu.anyo, cc.inicio, cc.fin, pu.URL\r\n"
					+ "FROM ((publicacion pu LEFT JOIN publicacionpersona pupe ON pu.idpublicacion = pupe.idpublicacion) \r\n"
					+ "LEFT JOIN persona pe ON pe.idpersona = pupe.idpersona) \r\n"
					+ "LEFT JOIN comunicacioncongreso cc ON pu.idpublicacion = cc.idpublicacion \r\n"
					+ " WHERE cc.idcongreso IS NOT NULL AND pu.titulo LIKE '%?%' AND (pe.nombre LIKE \"%?%\" OR pe.apellidos LIKE \"%?%\")";
			query = addParemetersToSearch(query, startYear, endYear);
			PreparedStatement preStatement = connection.prepareStatement(query);
			preStatement.setString(1, titulo);
			preStatement.setString(2, autor);
			preStatement.setString(3, autor);

			aux = preStatement.executeQuery();
			
			JSONArray conferences = new JSONArray();
			while(aux.next()) {
				JSONObject conference = new JSONObject();
				conference.put("authors", getAuthors(aux.getInt("idpublicacion")));
				conference.put("titulo", aux.getString("titulo"));
				conference.put("edicion", aux.getString("edicion"));
				conference.put("congreso", aux.getString("congreso"));
				conference.put("lugar", aux.getString("lugar"));
				conference.put("anyo", aux.getInt("anyo"));
				conference.put("inicio", aux.getString("inicio"));
				conference.put("fin",aux.getString("fin"));
				conference.put("URL", aux.getString("URL"));
				conferences.put(conference);
			}
			
			res.put(conferences);
			
		}
		
		if (book) {
			String query = "SELECT DISTINCT pu.idpublicacion, pu.titulo, li.editorial, pu.anyo, pu.URL \r\n"
					+ "FROM ((publicacion pu LEFT JOIN publicacionpersona pupe ON pu.idpublicacion = pupe.idpublicacion) \r\n"
					+ "LEFT JOIN persona pe ON pe.idpersona = pupe.idpersona) \r\n"
					+ "LEFT JOIN libro li ON pu.idpublicacion = li.idpublicacion \r\n"
					+ " WHERE li.idlibro IS NOT NULL AND pu.titulo LIKE '%?%' AND (pe.nombre LIKE \"%?%\" OR pe.apellidos LIKE \"%?%\")";
			query = addParemetersToSearch(query, startYear, endYear);
			PreparedStatement preStatement = connection.prepareStatement(query);
			preStatement.setString(1, titulo);
			preStatement.setString(2, autor);
			preStatement.setString(3, autor);
			
			aux = preStatement.executeQuery();
			
			JSONArray books = new JSONArray();
			while(aux.next()) {
				JSONObject bok = new JSONObject();
				bok.put("authors", getAuthors(aux.getInt("idpublicacion")));
				bok.put("titulo", aux.getString("titulo"));
				bok.put("editorial", aux.getString("editorial"));
				bok.put("anyo", aux.getInt("anyo"));
				bok.put("URL", aux.getString("URL"));
				books.put(bok);
			}
			
			res.put(books);
			
		}

		try (FileWriter file = new FileWriter(new File("C:\\Users\\polim\\Desktop\\Universidad\\search response.json"))) {
			 
            file.write(res.toString(4));
            file.flush();
 
        } catch (IOException e) {
            e.printStackTrace();
        }
		return res;
	}

	private static String addParemetersToSearch(String query, int startYear, int endYear) {
		if (!(startYear == 0 || endYear == 0)) {
			query += "AND pu.anyo <= " + endYear + " AND pu.anyo >= " + startYear;
		}
		return query;
	}

	
	private static JSONArray getAuthors(int id) throws SQLException {
		if(connection == null) {
			connect();
		}
		
		String query = "SELECT DISTINCT pe.nombre, pe.apellidos\r\n"
				+ "FROM publicacion pu LEFT JOIN publicacionpersona pp ON pp.idpublicacion = pu.idpublicacion LEFT JOIN persona pe ON pp.idpersona = pe.idpersona\r\n"
				+ "WHERE pu.idpublicacion =?";
		PreparedStatement preStatement = connection.prepareStatement(query);
		preStatement.setInt(1,id);
		ResultSet aux = preStatement.executeQuery();
		JSONArray authors = new JSONArray();
		while(aux.next()) {
			JSONObject author = new JSONObject();
			author.put("nombre", aux.getString("nombre"));
			author.put("apellidos", aux.getString("apellidos"));
			authors.put(author);
		}
		return authors;
	}

	private static void connect() {
		System.out.println("Starting...");
		try {
			connectionManager = new ConnectionManager();
			connection = connectionManager.getConnection(URL, USER, PASSWORD);
			connection.setAutoCommit(false);
			System.out.println("Done! inserted " + (--idPublicacion) + " new publications");

		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

	private static void eraseDataBase() throws SQLException {
		Statement statement = connection.createStatement();
		for (String table : tables) {
			statement.executeUpdate("DELETE FROM " + table);
			connection.commit();
		}
	}

	private static void insertIntoDBAll()
			throws FileNotFoundException, JSONException, SQLException, UnsupportedEncodingException {
		for (String path : convertedPaths) {
			InputStream inputStream = new FileInputStream(path);
			JSONTokener tokener = new JSONTokener(inputStream);
			JSONObject object = new JSONObject(tokener);
			insertIntoDB(object);
		}
	}

	public static void insertIntoDB(JSONObject input) throws JSONException, SQLException, UnsupportedEncodingException {
		connect();
		JSONArray publicaciones = new JSONArray();
		publicaciones = (JSONArray) input.get("publicaciones");
		float total = publicaciones.length();
		for (int i = 0; i < publicaciones.length(); i++) {
			JSONObject publicacion = publicaciones.getJSONObject(i);
			if (publicacion.has("publication_type")) {
				switch (publicacion.getString("publication_type")) {
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
			} else {
				processArticle(publicacion);
			}
			float per = (i / total) * 100;
			if ((i * 1.0) % (total / 400.0) <= 1) {
				System.out.print(String.format("Progreso: %.2f %s \r", per, "%"));
			}
		}
	}

	private static void processArticle(JSONObject article)
			throws SQLException, UnsupportedEncodingException, JSONException {
		String idRevis = Integer.toString(idRevista);
		String titulo = getTitle(article);
		JSONObject ejemplar = article.getJSONObject("ejemplar");
		if (!checkDuplicatedPublication(titulo)) {
			if (hasAJournalName(ejemplar)) {
				String nombre = getJournalName(ejemplar);
				try {
					insertIntoRevistaTable(idRevista++, nombre);
				} catch (Exception e) {
					idRevis = Integer.toString(findRevista(nombre));
					idRevista--;
				}
			} else {
				idRevis = null;
			}

			String volumen = getVolume(ejemplar);
			String numero = getIssue(ejemplar);
			String mes = getMonth(ejemplar);

			insertIntoEjemplarTable(idEjemplar, volumen, numero, mes, idRevis);

			int anyo = getYear(article);
			String URL = getURL(article);
			String paginaInicio = getInitPage(article);
			String paginaFin = getEndPage(article);

			insertIntoPublicacionTable(idPublicacion, titulo, anyo, URL);
			processPersonas(article.getJSONArray("persona"), idPublicacion);
			insertIntoArticuloTable(idArticulo++, paginaInicio, paginaFin, idPublicacion++, idEjemplar++);
		}
	}

	private static void processConference(JSONObject conference)
			throws JSONException, SQLException, UnsupportedEncodingException {
		String titulo = getTitle(conference);
		int anyo = getYear(conference);
		String URL = getURL(conference);
		if (!checkDuplicatedPublication(titulo)) {
			insertIntoPublicacionTable(idPublicacion, titulo, anyo, URL);

			String congreso = getConvention(conference);
			String edicion = getEdition(conference);
			String lugar = getLocation(conference);
			String paginaInicio = getInitPage(conference);
			String paginaFin = getEndPage(conference);

			insertIntoComunicacionCongresoTable(idComunicacionCongreso++, congreso, edicion, lugar, paginaInicio,
					paginaFin, idPublicacion);
			processPersonas(conference.getJSONArray("persona"), idPublicacion++);
		}
	}

	private static void processBook(JSONObject book) throws JSONException, SQLException, UnsupportedEncodingException {
		String titulo = getTitle(book);
		int anyo = getYear(book);
		String URL = getURL(book);
		String editorial = getEditorial(book);

		if (!checkDuplicatedPublication(titulo)) {
			insertIntoPublicacionTable(idPublicacion, titulo, anyo, URL);
			insertIntoLibroTable(idLibro++, editorial, idPublicacion);
			processPersonas(book.getJSONArray("persona"), idPublicacion++);
		}
	}

	private static void processPersonas(JSONArray personas, int idPublicacion)
			throws SQLException, UnsupportedEncodingException {
		for (int i = 0; i < personas.length(); i++) {
			JSONObject persona = personas.getJSONObject(i);

			String nombre = getName(persona);
			String apellidos = getSurname(persona);

			int idPers = checkPersona(nombre, apellidos);
			if (idPers == -1) {
				idPers = idPersona;
				insertIntoPersonaTable(idPersona++, nombre, apellidos);
			}
			if (!checkDuplicatedRowPPTable(idPublicacion, idPers)) {
				insertIntoPublicacionPersonaTable(idPublicacion, idPers);
			}
		}
	}

	private static boolean checkDuplicatedPublication(String titulo) throws SQLException {
		Statement statement = connection.createStatement();
		ResultSet rs = statement
				.executeQuery("SELECT COUNT(*) AS rowcount FROM publicacion WHERE titulo = '" + titulo + "'");
		rs.next();
		int count = rs.getInt("rowcount");
		return count > 0;
	}

	private static boolean checkDuplicatedRowPPTable(int idPublicacion, int idPers) throws SQLException {
		Statement statement = connection.createStatement();
		ResultSet rs = statement
				.executeQuery("SELECT COUNT(*) AS rowcount FROM publicacionpersona WHERE (idpublicacion = "
						+ idPublicacion + " AND idpersona = " + idPers + ")");
		rs.next();
		int count = rs.getInt("rowcount");
		return count > 0;
	}

	private static String getSurname(JSONObject json) {
		String apellidos = "";
		if (hasASurname(json)) {
			apellidos = json.getString("surname");
		}
		return apellidos;
	}

	private static String getName(JSONObject json) throws UnsupportedEncodingException {
		String nombre = "";
		if (hasAName(json)) {
			nombre = json.getString("name");
		}
		return nombre;

	}

	private static int getYear(JSONObject json) {
		int anyo = -1;
		if (hasAYear(json)) {
			anyo = json.getInt("anyo");
		}
		return anyo;
	}

	private static String getURL(JSONObject json) {
		String URL = "";
		if (hasAURL(json)) {
			URL = json.getString("url");
		}
		return URL;
	}

	private static String getEditorial(JSONObject json) {
		String editorial = "";
		if (hasAEditorial(json)) {
			editorial = json.getString("editorial");
		}
		return editorial;
	}

	private static String getTitle(JSONObject json) {
		String titulo = "";
		if (hasATitle(json)) {
			titulo = json.getString("titulo");
		}
		return titulo;
	}

	private static String getConvention(JSONObject json) {
		String congreso = "";
		if (hasAConvention(json)) {
			congreso = json.getString("congreso");
		}
		return congreso;
	}

	private static String getEdition(JSONObject json) {
		String edicion = "";
		if (hasAEdition(json)) {
			edicion = json.getString("edicion");
		}
		return edicion;
	}

	private static String getLocation(JSONObject json) {
		String location = "";
		if (hasALocation(json)) {
			location = json.getString("lugar");
		}
		return location;
	}

	private static String getInitPage(JSONObject json) {
		String initPage = "";
		if (hasAnInitPage(json)) {
			initPage = json.getString("pagina_inicio");
		}
		return initPage;
	}

	private static String getEndPage(JSONObject json) {
		String endPage = "";
		if (hasAnEndPage(json)) {
			endPage = json.getString("pagina_fin");
		}
		return endPage;
	}

	private static String getMonth(JSONObject json) {
		String mes = "";
		if (hasAMonth(json)) {
			try {
				mes = json.getString("mes");
			} catch (Exception e) {
				mes = Integer.toString(json.getInt("mes"));
			}
		}
		return mes;
	}

	private static String getIssue(JSONObject json) {
		String numero = "";
		if (hasAnIssue(json)) {
			numero = json.getString("numero");
		}
		return numero;
	}

	private static String getVolume(JSONObject json) {
		if (hasAVolume(json)) {
			try {
				return json.getString("volumen");
			} catch (Exception e) {
				return Integer.toString(json.getInt("volumen"));
			}
		}
		return "";
	}

	private static String getJournalName(JSONObject json) {
		String nombre = "";
		if (hasAJournalName(json)) {
			nombre = json.getString("revista");
		}
		return nombre;
	}

	private static boolean hasAJournalName(JSONObject json) {
		return json.has("revista");
	}

	private static boolean hasAVolume(JSONObject json) {
		return json.has("volumen");
	}

	private static boolean hasAnIssue(JSONObject json) {
		return json.has("numero");
	}

	private static boolean hasAMonth(JSONObject json) {
		return json.has("mes");
	}

	private static boolean hasAnEndPage(JSONObject json) {
		return json.has("pagina_fin");
	}

	private static boolean hasAnInitPage(JSONObject json) {
		return json.has("pagina-inicio");
	}

	private static boolean hasALocation(JSONObject json) {
		return json.has("lugar");
	}

	private static boolean hasAEdition(JSONObject json) {
		return json.has("edicion");
	}

	private static boolean hasAConvention(JSONObject json) {
		return json.has("congreso");
	}

	private static boolean hasAName(JSONObject json) {
		return json.has("name");
	}

	private static boolean hasASurname(JSONObject json) {
		return json.has("surname");
	}

	private static boolean hasATitle(JSONObject json) {
		return json.has("titulo");
	}

	private static boolean hasAEditorial(JSONObject json) {
		return json.has("editorial");
	}

	private static boolean hasAURL(JSONObject json) {
		return json.has("url");
	}

	private static boolean hasAYear(JSONObject json) {
		return json.has("anyo");
	}

	private static int findRevista(String nombre) throws SQLException {
		Statement statement = connection.createStatement();
		ResultSet rs = statement.executeQuery("SELECT * FROM revista WHERE nombre = '" + nombre + "'");
		rs.next();
		return rs.getInt("idrevista");
	}

	private static int checkPersona(String nombre, String apellidos) throws SQLException {
		Statement statement = connection.createStatement();
		ResultSet rs = statement.executeQuery("SELECT COUNT(*) AS rowcount FROM persona WHERE (nombre = '" + nombre
				+ "' AND apellidos = '" + apellidos + "')");
		rs.next();
		int count = rs.getInt("rowcount");
		if (count == 0) {
			return -1;
		} else {
			rs = statement.executeQuery(
					"SELECT * FROM persona WHERE (nombre = '" + nombre + "' AND apellidos = '" + apellidos + "')");
			rs.next();
			return rs.getInt("idpersona");
		}
	}

	private static void insertIntoArticuloTable(int idArticulo, String paginaInicio, String paginaFin,
			int idPublicacion, int idEjemplar) throws SQLException {
		Statement query = connection.createStatement();
		query.executeUpdate("INSERT INTO articulo VALUES (" + idArticulo + "," + "'" + paginaInicio + "'" + "," + "'"
				+ paginaFin + "'" + "," + idPublicacion + "," + idEjemplar + ")");
		connection.commit();
	}

	private static void insertIntoComunicacionCongresoTable(int idComunicacionCongreso, String congreso, String edicion,
			String lugar, String paginaInicio, String paginaFin, int idPublicacion) throws SQLException {
		Statement query = connection.createStatement();
		query.executeUpdate("INSERT INTO comunicacioncongreso VALUES (" + idComunicacionCongreso + "," + "'" + congreso
				+ "'" + "," + "'" + edicion + "'" + "," + "'" + lugar + "'" + "," + "'" + paginaInicio + "'" + "," + "'"
				+ paginaFin + "'" + "," + idPublicacion + ")");
		connection.commit();
	}

	private static void insertIntoEjemplarTable(int idEjemplar, String volumen, String numero, String mes,
			String pertenece) throws SQLException {
		int perteneceInt;
		Statement query = connection.createStatement();
		try {
			perteneceInt = Integer.parseInt(pertenece);
			query.executeUpdate("INSERT INTO ejemplar VALUES (" + idEjemplar + "," + "'" + volumen + "'" + "," + "'"
					+ numero + "'" + "," + "'" + mes + "'" + "," + perteneceInt + ")");
			connection.commit();
		} catch (Exception e) {
			query.executeUpdate("INSERT INTO ejemplar VALUES (" + idEjemplar + "," + "'" + volumen + "'" + "," + "'"
					+ numero + "'" + "," + "'" + mes + "'" + "," + pertenece + ")");
			connection.commit();
		}

	}

	private static void insertIntoLibroTable(int idLibro, String editorial, int idPublicacion) throws SQLException {
		Statement query = connection.createStatement();
		query.executeUpdate(
				"INSERT INTO libro VALUES (" + idLibro + "," + "'" + editorial + "'" + "," + idPublicacion + ")");
		connection.commit();
	}

	private static void insertIntoPersonaTable(int idPersona, String nombre, String apellidos) throws SQLException {
		Statement query = connection.createStatement();
		query.executeUpdate("INSERT INTO persona VALUES (" + idPersona + "," + "'" + nombre + "'" + "," + "'"
				+ apellidos + "'" + ")");
		connection.commit();
	}

	private static void insertIntoPublicacionTable(int idPublicacion, String titulo, int anyo, String URL)
			throws SQLException {
		Statement query = connection.createStatement();
		query.executeUpdate("INSERT INTO publicacion VALUES (" + idPublicacion + "," + "'" + titulo + "'" + "," + anyo
				+ "," + "'" + URL + "'" + ")");
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
}
