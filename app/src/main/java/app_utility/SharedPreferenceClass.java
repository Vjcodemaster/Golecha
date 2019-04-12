package app_utility;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class SharedPreferenceClass {
    private SharedPreferences sharedPreferences;
    private Context _context;
    SharedPreferences.Editor editor;

    private static final String APP_PREFERENCES = "GOLECHA_PREFERENCES";

    private static final int PRIVATE_MODE = 0;
    private static final String IS_LOGGED_IN = "IS_LOGGED_IN";
    private static final String USER_NAME = "USER_NAME";

    private static final String USER_ORDERS = "USER_ORDERS";


    // Constructor
    public SharedPreferenceClass(Context context) {
        this._context = context;

        sharedPreferences = _context.getSharedPreferences(APP_PREFERENCES, PRIVATE_MODE);
        editor = sharedPreferences.edit();
        editor.apply();
    }

    /**
     * Converts the provided ArrayList<String>
     * into a JSONArray and saves it as a single
     * string in the apps shared preferences
     * //@param key string Preference key for SharedPreferences
     * @param array ArrayList<String> containing the list items
     */
    public void saveArray(ArrayList<String> array) {
        JSONArray jArray = new JSONArray(array);
        editor.remove(USER_ORDERS);
        editor.putString(USER_ORDERS, jArray.toString());
        editor.commit();
    }

    /**
     * Loads a JSONArray from shared preferences
     * and converts it to an ArrayList<String>
     * @param key string Preference key for SharedPreferences
     * @return ArrayList<String> containing the saved values from the JSONArray
     */
    public ArrayList<String> getArray(String key) {
        ArrayList<String> array = new ArrayList<>();
        String jArrayString = sharedPreferences.getString(key, "NOPREFSAVED");
        if (jArrayString.matches("NOPREFSAVED")) return getDefaultArray();
        else {
            try {
                JSONArray jArray = new JSONArray(jArrayString);
                for (int i = 0; i < jArray.length(); i++) {
                    array.add(jArray.getString(i));
                }
                return array;
            } catch (JSONException e) {
                return getDefaultArray();
            }
        }
    }

    // Get a default array in the event that there is no array
    // saved or a JSONException occurred
    private ArrayList<String> getDefaultArray() {
        ArrayList<String> array = new ArrayList<>();
        array.add("Example 1");
        array.add("Example 2");
        array.add("Example 3");

        return array;
    }
}
