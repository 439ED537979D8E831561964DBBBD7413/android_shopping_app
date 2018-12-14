package course.android.shopping_example_app.Services;

import org.json.JSONObject;

import java.util.List;

public interface IResultReceiver {

	void onRecieve(List<JSONObject> results);
}
