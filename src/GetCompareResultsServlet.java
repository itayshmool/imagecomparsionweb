import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.labs.repackaged.org.json.JSONException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: itays
 * Date: 4/18/13
 * Time: 10:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class GetCompareResultsServlet extends HttpServlet {


    private ServletContext context;
    public DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    private static final String USAGE = "http://imagecomparisonweb.appspot.com/getcompareresults?guid=12f6143e-92ec-c377-c319-b8998a8ce9fe";

    public void init(ServletConfig config) throws ServletException {

        super.init(config);

        context = getServletContext();

    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        Enumeration en = req.getParameterNames();

        String guid = "";
        String comments = "";
        boolean isComment = false;


        List<String> allowedParams = Arrays.asList("guid","comments");


        while (en.hasMoreElements()) {

            String paramName = (String) en.nextElement();

            if (!allowedParams.contains(paramName)) {
                resp.getWriter().println("Syntax Error !!! parameter:" + paramName + " is not allowed.list of allowed parameters is(" + allowedParams.toString() + ")" + "\nUsage:" + USAGE);
                return;
            }

            if (paramName.equals("guid")) {
                guid = req.getParameter(paramName);
            }

            if (paramName.equals("comments")) {
                isComment=true;
            }
        }

        if (guid.isEmpty()) {
            resp.getWriter().println("must provide Capture Guid !!!\nUsage:" + USAGE);
            return;
        }

        Entity urlCompareRecord = UrlCompareEntry.getUrlCaptureEntry(datastore, guid);

        if (urlCompareRecord == null)
        {
            resp.getWriter().println("can't find compare record for Guid:" + guid);
            return;
        }


        if (isComment)
        {

            resp.getWriter().println(urlCompareRecord.getProperty("Comments").toString());
            return;
        }

        try {


            resp.getWriter().println(UrlCompareEntry.buildHtmlFromEntry(urlCompareRecord));
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }



}
