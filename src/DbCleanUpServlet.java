import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import service.DbCleanupUtils;

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
public class DbCleanUpServlet extends HttpServlet {


    private ServletContext context;
    public DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    private static final String USAGE = "http://imagecomparisonweb.appspot.com/cleanup?guid=12f6143e-92ec-c377-c319-b8998a8ce9fe";
    private static final String USAGE1 = "http://imagecomparisonweb.appspot.com/cleanup?capture";
    private static final String USAGE2 = "http://imagecomparisonweb.appspot.com/cleanup?compare";
    private static final String USAGE3 = "http://imagecomparisonweb.appspot.com/cleanup?all";



    public void init(ServletConfig config) throws ServletException {

        super.init(config);

        context = getServletContext();

    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        Enumeration en = req.getParameterNames();

        String guid = "";
        String capture = "";
        String compare = "";
        String all = "";



        List<String> allowedParams = Arrays.asList("guid","capture","compare","all");


        while (en.hasMoreElements()) {

            String paramName = (String) en.nextElement();

            if (!allowedParams.contains(paramName)) {
                resp.getWriter().println("Syntax Error !!! parameter:" + paramName + " is not allowed.list of allowed parameters is(" + allowedParams.toString() + ")" + "\nUsage:"
                        + USAGE + "\n" + USAGE1 + "\n" + USAGE2 + "\n" + USAGE3 + "\n");
                return;
            }

            if (paramName.equals("guid")) {
                guid = req.getParameter(paramName);
            }

            if (paramName.equals("compare")) {
                compare = "ok";
            }

            if (paramName.equals("capture")) {
                capture = "ok";
            }

            if (paramName.equals("all")) {
                all = "ok";
            }
        }

        if (!capture.isEmpty())
        {
            resp.getWriter().println("Total Capture records Deleted:" + DbCleanupUtils.deleteAllByType(datastore,"capture"));
            return;
        }

        if (!compare.isEmpty())
        {
            resp.getWriter().println("Total Compare records Deleted:" + DbCleanupUtils.deleteAllByType(datastore,"compare"));
            return;
        }

        if (!all.isEmpty())
        {
            resp.getWriter().println("Total Compare and Capture records Deleted:" + DbCleanupUtils.deleteAllByType(datastore,"all"));
            return;
        }

        if (!guid.isEmpty())
        {
            resp.getWriter().println("Total records Deleted by Guid:" + guid + " is:" +DbCleanupUtils.deleteByGuid(datastore,guid));
            return;
        }

        resp.getWriter().println("nothing to do ...");

    }



}
