package dsic.upv.es;

import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

public interface Extractor {
	public int extract() throws JSONException, SQLException;
}
