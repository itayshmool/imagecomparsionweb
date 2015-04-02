import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: itays
 * Date: 4/18/13
 * Time: 12:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class UrlCompareEntry {

    public static String web = "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "  <head>\n" +
            "    <meta charset=\"utf-8\">\n" +
            "</head>\n" +
            "<body>\n" +

            "<p>date: __DATE__ </p> \n" +
            "<p>comments: __COMMENTS__ </p> \n" +
            "<p>Comapre Guid: __COMPARE_GUID__ </p> \n" +
            "<p>Capture1 Guid: __CAPTURE1_GUID__ </p> \n" +
            "<p>Capture2 Guid: __CAPTURE2_GUID__ </p> \n" +
            "<p>total Sites Compared: __TOTAL_SITES__ </p> \n" +
            "<p>number Of diffs found: __DIFF_NUM__ </p> \n" +
            "\n<a href=\"__DIFF_FILE__\" target=\"_blank\" >__DIFF_FILE__</a> \n" +

            "  </body>\n" +
            "</html>";



    public UrlCompareEntry(String json) throws JSONException {

        JSONObject myjson = null;
        try {
            myjson = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        // get list of commits

        UrlCompareEntry ret = new UrlCompareEntry();

        this.Guid = myjson.get("Guid").toString();
        this.comments = myjson.get("comments").toString();
        this.firstCaptureGuid = myjson.get("firstCaptureGuid").toString();
        this.secondCaptureGuid = myjson.get("secondCaptureGuid").toString();
        this.numDiffs = myjson.get("numDiffs").toString();
        this.totalFileCompared = myjson.get("totalFileCompared").toString();
        this.DiffFileFullPath = myjson.get("DiffFileFullPath").toString();

    }

    public UrlCompareEntry() {
    }

    public String getGuid() {
        return Guid;
    }

    public String getFirstCaptureGuid() {
        return firstCaptureGuid;
    }

    public String getSecondCaptureGuid() {
        return secondCaptureGuid;
    }

    public String getNumDiffs() {
        return numDiffs;
    }

    public String getTotalFileCompared() {
        return totalFileCompared;
    }

    public String getDiffFileFullPath() {
        return DiffFileFullPath;
    }

    public String Guid;
    public String firstCaptureGuid;
    public String secondCaptureGuid;
    public String numDiffs;
    public String totalFileCompared;
    public String DiffFileFullPath;

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String comments;




    public static void logCompareEntryToDb(DatastoreService datastoreService, UrlCompareEntry record) {

        Date date = new Date();
        Entity entry = new Entity("CompareEntry");

        entry.setProperty("date", date);
        entry.setProperty("Guid", record.getGuid());
        entry.setProperty("Comments", record.getComments());
        entry.setProperty("firstCaptureGuid", record.getFirstCaptureGuid());
        entry.setProperty("secondCaptureGuid", record.getSecondCaptureGuid());
        entry.setProperty("numDiffs", record.getNumDiffs());
        entry.setProperty("totalFileCompared", record.getTotalFileCompared());
        entry.setProperty("DiffFileFullPath", record.getDiffFileFullPath());



        datastoreService.put(entry);
    }

   public static Entity getUrlCaptureEntry(DatastoreService datastoreService, String Guid) throws IOException {


        Query query = new Query("CompareEntry");

        Query.Filter issueFilter =
                new Query.FilterPredicate("Guid",
                        Query.FilterOperator.EQUAL,
                        Guid);


        query.setFilter(issueFilter);

        List<Entity> records = datastoreService.prepare(query).asList(FetchOptions.Builder.withLimit(100));
       if (records.size() == 0)
           return null;

        return records.get(0);

    }
//
//    public static String buildJsonFromEntry(Entity record) throws JSONException {
//
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("Guid", record.getProperty("Guid").toString());
//        jsonObject.put("firstCaptureGuid", record.getProperty("firstCaptureGuid").toString());
//        jsonObject.put("secondCaptureGuid", record.getProperty("secondCaptureGuid").toString());
//        jsonObject.put("numDiffs", record.getProperty("numDiffs").toString());
//        jsonObject.put("totalFileCompared", record.getProperty("totalFileCompared").toString());
//        jsonObject.put("DiffFileFullPath", record.getProperty("DiffFileFullPath").toString());
//
//
//        return jsonObject.toString();
//
//
//    }

    public static String buildHtmlFromEntry(Entity record) throws JSONException {

        return web.replaceAll("__DATE__",record.getProperty("date").toString()).
                replaceAll("__COMPARE_GUID__",record.getProperty("Guid").toString()).
                replaceAll("__COMMENTS__",record.getProperty("Comments").toString()).
                replaceAll("__CAPTURE1_GUID__",record.getProperty("firstCaptureGuid").toString()).
                replaceAll("__CAPTURE2_GUID__",record.getProperty("secondCaptureGuid").toString()).
                replaceAll("__TOTAL_SITES__",record.getProperty("totalFileCompared").toString()).
                replaceAll("__DIFF_NUM__",record.getProperty("numDiffs").toString()).
                replaceAll("__DIFF_FILE__",createLinkTofile(record.getProperty("DiffFileFullPath").toString()));

    }

//    public static UrlCompareEntry buildCompareEntryFromJson(String json) throws JSONException {
//
//        JSONObject myjson = null;
//        try {
//            myjson = new JSONObject(json);
//        } catch (JSONException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
//        // get list of commits
//
//        UrlCompareEntry ret = new UrlCompareEntry();
//
//        ret.Guid = myjson.get("Guid").toString();
//        ret.firstCaptureGuid = myjson.get("firstCaptureGuid").toString();
//        ret.secondCaptureGuid = myjson.get("secondCaptureGuid").toString();
//        ret.numDiffs = myjson.get("numDiffs").toString();
//        ret.totalFileCompared = myjson.get("totalFileCompared").toString();
//        ret.DiffFileFullPath = myjson.get("DiffFileFullPath").toString();
//
//
//        return ret;
//
//    }

    public static String createLinkTofile(String file)

    {
       // bad link

       // "file://C:image12f6f1c8-868e-1647-a1d2-0055cad83543diff.html"

       // good link

       // "file:///C:/image/12f6f2a7-d82e-0ef8-c376-4c8d1f774b2a/diff.html"
        return  file;

    }


}
