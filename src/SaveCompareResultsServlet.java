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

/**
 * Created with IntelliJ IDEA.
 * User: itays
 * Date: 4/18/13
 * Time: 10:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class SaveCompareResultsServlet extends HttpServlet {


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


        try {
            BufferedReader reader = request.getReader();


            while ((line = reader.readLine()) != null)
                jb.append(line);
        } catch (Exception e) {

            response.getWriter().println(jb.toString());


        }


        UrlCompareEntry entry = null;
        try {
            entry = new UrlCompareEntry(jb.toString());
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        UrlCompareEntry.logCompareEntryToDb(datastore,entry);

        response.getWriter().println(jb.toString());




    }




}
