import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: itays
 * Date: 4/18/13
 * Time: 12:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class UrlCaptureEntry {

    public UrlCaptureEntry(String guid, String url, String captureFileFullPath,String comments) {
        Guid = guid;
        Url = url;
        CaptureFileFullPath = captureFileFullPath;
        this.comments = comments;
    }

    public String getGuid() {
        return Guid;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String comments;

    public void setGuid(String guid) {
        Guid = guid;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getCaptureFileFullPath() {
        return CaptureFileFullPath;
    }

    public void setCaptureFileFullPath(String captureFileFullPath) {
        CaptureFileFullPath = captureFileFullPath;
    }

    public String Guid;
    public String Url;
    public String CaptureFileFullPath;


    public static List<UrlCaptureEntry> parseCaptureURLMap(String json) throws JSONException {
        List<UrlCaptureEntry> list = new ArrayList<UrlCaptureEntry>();


        JSONObject myjson = null;
        try {
            myjson = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        // get list of commits
        JSONArray the_json_array = myjson.getJSONArray("entries");
        String guid = myjson.get("Guid").toString();
        String comments = myjson.get("comments").toString();
        String total = myjson.get("Total").toString();

        int size = the_json_array.length();
        for (int i = 0; i < size; i++) {

            JSONObject another_json_object = the_json_array.getJSONObject(i);
            String urlAndFile = (String) another_json_object.get("entry");


            UrlCaptureEntry urlCaptureEntry = new UrlCaptureEntry(guid, urlAndFile.split(";")[1], urlAndFile.split(";")[0],comments);
            list.add(urlCaptureEntry);
        }


        return list;
    }

    public static void logUrlCaptureEntryToDb(DatastoreService datastoreService, UrlCaptureEntry record) {

        Date date = new Date();
        Entity entry = new Entity("UrlCaptureEntry");

        entry.setProperty("date", date);
        entry.setProperty("Guid", record.getGuid());
        entry.setProperty("Comments", record.getComments());
        entry.setProperty("Url", record.getUrl());
        entry.setProperty("CaptureFileFullPath", record.getCaptureFileFullPath());

        datastoreService.put(entry);
    }

    public static void pushListOfEntriesToDb(DatastoreService datastoreService, List<UrlCaptureEntry> urlCaptureEntryList) {
        for (int i = 0; i < urlCaptureEntryList.size(); i++)
            logUrlCaptureEntryToDb(datastoreService, urlCaptureEntryList.get(i));

    }

    public static List<Entity> getUrlCaptureEntry(DatastoreService datastoreService, String Guid) throws IOException {


        Query query = new Query("UrlCaptureEntry").addSort("date", Query.SortDirection.ASCENDING);

        Query.Filter issueFilter =
                new Query.FilterPredicate("Guid",
                        Query.FilterOperator.EQUAL,
                        Guid);


        query.setFilter(issueFilter);

        List<Entity> records = datastoreService.prepare(query).asList(FetchOptions.Builder.withLimit(1000));
        return records;

    }


    public static String getUrlVsFileCaptureList(DatastoreService datastoreService, String guid) {


        Query query = new Query("UrlCaptureEntry");


        Query.Filter issueFilter =
                new Query.FilterPredicate("Guid",
                        Query.FilterOperator.EQUAL,
                        guid);


        query.setFilter(issueFilter);


        List<Entity> records = datastoreService.prepare(query).asList(FetchOptions.Builder.withLimit(1000));
        String ret = null;
        try {
            ret = buildJsonFromEntryList(records, guid);
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return ret;

    }

    public static String buildJsonFromEntryList(List<Entity> records, String guid) throws JSONException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Guid", guid);
        jsonObject.put("Total", records.size());

        JSONArray jsonArray = new JSONArray();


        for (Entity record : records) {

            JSONObject jsonEntry = new JSONObject();
            jsonEntry.put("entry", record.getProperty("Url") + ";" + record.getProperty("CaptureFileFullPath"));
            jsonArray.put(jsonEntry);

        }

        jsonObject.put("entries", jsonArray);

        return jsonObject.toString();


    }

}
