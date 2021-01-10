package dsic.upv.es;

import java.sql.SQLException;

import org.json.JSONException;

public interface Extractor {
	public int extract() throws JSONException, SQLException;
}
