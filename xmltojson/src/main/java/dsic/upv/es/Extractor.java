package dsic.upv.es;

import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

public interface Extractor {
	public void extract() throws JSONException, SQLException;
}
