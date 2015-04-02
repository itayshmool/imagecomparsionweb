import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.labs.repackaged.org.json.JSONException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: itays
 * Date: 4/18/13
 * Time: 10:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class SaveCaptureResultsServlet extends HttpServlet {


    private ServletContext context;
    public DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    public void init(ServletConfig config) throws ServletException {

        super.init(config);

        context = getServletContext();

    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.getWriter().println("I am alive ... :)");
    }

    /**
     * Process the HTTP doPost request.
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        StringBuffer jb = new StringBuffer();
        String line = null;
        //response.getWriter().println("Hi Igal :)");
        // response.getWriter().println(request);


        try {
            BufferedReader reader = request.getReader();


            while ((line = reader.readLine()) != null)
                jb.append(line);
        } catch (Exception e) {

            response.getWriter().println(jb.toString());


        }

    //build_name=Html Web Skins&ga=1.211.0&rc=1.211.1&snapshot=1.212.0&artifactList=[{"artifactId":"html-web-skins","groupId":"com.wixpress.html","buildTypeId":"bt201","production":true,"includeStatic":true,"newRelicId":"","servers":[]}]

        response.getWriter().println(jb.toString());
        List<UrlCaptureEntry> list = null;
        try {
            list = UrlCaptureEntry.parseCaptureURLMap(jb.toString());
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        UrlCaptureEntry.pushListOfEntriesToDb(this.datastore , list);
//
//        LifeCycleRequest lifeCycleRequest = null;
//        try {
//
//
//            String req = URLDecoder.decode(jb.toString(), "UTF-8");
//            // lifeCycleRequest = new LifeCycleRequest(new String(jb.toString().getBytes("UTF-8"),"ASCII"));
//            lifeCycleRequest = new LifeCycleRequest(req);
//
//
//            PushActivityRecord.logLifeCycleActivityToDb(datastore, lifeCycleRequest);
//
//            // get projectBuild entity from map according to build name from life cycle
//            projectBuild projectBuildElm = this.projectBuildParser.getProjectMap().get(lifeCycleRequest.getBuild_name());
//            if (projectBuildElm == null) {
//                response.getWriter().println("Project:" + lifeCycleRequest.getBuild_name() + " Not supported yet....");
//                return;
//            }
//
//            String resp = "";
//            for (int i = 0; i < projectBuildElm.getJira_projects().size(); i++) {
//
//
//                JiraBaseRequest jiraBaseRequest = JiraRequestFactory.getLifeCycleRequest(JIRA_BASE_URL, JIRA_USER, JIRA_PASSOWRD, lifeCycleRequest, projectBuildElm, datastore, projectBuildElm.getJira_projects().get(i));
//                try {
//                    resp += jiraBaseRequest.execute();
//                    resp += "\n";
//                } catch (ParseException e) {
//                    e.printStackTrace();  //To change body of catch statement use File | SettingsServlet | File Templates.
//                }
//
//            }
//
//            response.getWriter().println(resp);
//
//        }catch(JSONException e){
//            e.printStackTrace();  //To change body of catch statement use File | SettingsServlet | File Templates.
//        }

    }




}
