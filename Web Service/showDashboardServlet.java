/**
 * Author: Wangning Shao
 * Last Modified: April 11th 2021
 *
 * This is a web service which wil provide information which will be used for our dashboard
 */
package ds;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "showDashboardServlet", urlPatterns = {"/showDashboard"})
public class showDashboardServlet extends HttpServlet {
    CatInfoModel cim = null;  // The "business model" for this app

    // Initiate this servlet by instantiating the model that it will use.
    @Override
    public void init() {
        cim = new CatInfoModel();
    }
    /**
     * @param request, response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //Call connectMongo method in CatInforModel to initiate the connection to MongoDB
        MongoDatabase db = cim.connectMongo();
        // get collection named test
        MongoCollection<Document> collection = db.getCollection("test");
        //Iterate through collection to find all documents
        MongoCursor<Document> cursor = collection.find().iterator();
        // Each document will be stored in result arraylist which later will be send to dashboard.jsp
        List<String> result = new ArrayList<>();
        try {
            while (cursor.hasNext()) {
                result.add(cursor.next().toJson());
            }
        } finally {
            cursor.close();
        }
        request.setAttribute("result", result); // setAttribute so we can use it in dashboard.jsp
        // Transfer control over the the correct "view"
        RequestDispatcher view = request.getRequestDispatcher("dashboard.jsp");
        view.forward(request, response);
    }
}
