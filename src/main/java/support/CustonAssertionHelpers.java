package support;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Arrays;

public class CustonAssertionHelpers {

    static JSONArray getJSONArray(JSONObject jsonObject, String... searchTerms){
        try{
            for (String searchTerm : searchTerms) {
                Class objectClass = jsonObject.get(searchTerm).getClass();
                if (objectClass == Long.class || objectClass == String.class || objectClass == Boolean.class) { //expecting value to be a Long, String or Boolean, may have to update this later to include other types
                    return null; //this means the searcher has hit a dead-end
                } else if (jsonObject.get(searchTerm).getClass() == JSONArray.class) {//this is what we're looking for
                    return (JSONArray) jsonObject.get(searchTerm);
                } else { //if not a Long or a String, it's probably a nested JSONObject, in which case we will search with the next search term
                    jsonObject = (JSONObject) jsonObject.get(searchTerm);
                }
            }
        }
        catch (NullPointerException e){
            //will return null in the event of nullpointerexception
        }
        return null;
    }

    /**
     * Retrieve a single value from a Json array.
     * @param jsonArray
     * @param searchTerms
     * @return
     */

    static String searchJSONArray(JSONArray jsonArray, String... searchTerms){
        for (int i = 0; i< jsonArray.size(); i++)
        {
            if (jsonArray.get(i).getClass() == JSONObject.class) {
                return searchJSONObject((JSONObject) jsonArray.get(i), searchTerms);
            }
        }
        return null;
    }

    /**
     * this will parse through a JSON array and return the key value pairs
     * @param jsonObject
     * @param searchTerms
     * @return
     */
    static String searchJSONObject(JSONObject jsonObject, String... searchTerms){
        try{
            for (int i = 0; i < searchTerms.length; i++) {
                String searchTerm = searchTerms[i];
                Class objectClass = jsonObject.get(searchTerm).getClass();
                if (objectClass == Long.class || objectClass == String.class || objectClass == Boolean.class) { //expecting value to be a Long, String or Boolean, may have to update this later to include other types
                    return jsonObject.get(searchTerm).toString();
                }
                else if (jsonObject.get(searchTerm).getClass() == JSONArray.class) //to parse something like errors:[] which will become a JSONArray and not a string or a JSONObject
                {
                    if (i == searchTerms.length - 1){ //no more searchterms left, no more searching possible
                        JSONArray array = (JSONArray) jsonObject.get(searchTerm);
                        try {
                            return array.get(0).toString();
                        }
                        catch (IndexOutOfBoundsException e){ // in the event the array is empty
                            return null;
                        }
                    }
                    else{
                        return searchJSONArray((JSONArray) jsonObject.get(searchTerm), Arrays.copyOfRange(searchTerms, i+1, searchTerms.length)); //remaining searchterms
                    }
                }
                else{ //if not a Long or a String, it's probably a nested JSON, in which case we will search with the next search term
                    jsonObject = (JSONObject) jsonObject.get(searchTerm);
                }
            }
        }
        catch (NullPointerException e){
            //will return null in the event of nullpointerexception
        }
        return null;
    }

}
