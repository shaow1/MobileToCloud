/**
 * Author: Wangning Shao
 * Last Modified: April 9th 2021
 *
 * This is a web service which wil provide information send back to our
 * Android Application
 */
package ds;

import com.mongodb.client.MongoDatabase;

import java.io.*;
import java.util.Date;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

public class CatInfoServlet extends HttpServlet {
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
        long start = System.currentTimeMillis();
        Date startDate = new java.util.Date(start);

        //Get user provided cat type which will be used for our search
        String catType = request.getParameter("type");
        String breedSearch;
        //Get cat information by bread type from 3rd party API
        if(catType.contains(" "))
        {
            //This could also achieved by using URLEncoder.encode(breedSearch, StandardCharsets.UTF_8);
            String catTypeCast = cim.spaceConversion(catType);
            //Assign search result to breedSearch
            breedSearch = cim.fetch("https://api.thecatapi.com/v1/breeds/search?q=" + catTypeCast);
        }
        else
        {
            //Assign search result to breedSearch
            breedSearch = cim.fetch("https://api.thecatapi.com/v1/breeds/search?q=" + catType);
        }
        String result = "";
        String id = "";
        if(breedSearch.length() != 2) {
            //get cat ID
            id = cim.getID(breedSearch);
            // Do a second search to using bread_id which will give us the result which also contains an image
            result = cim.fetch("https://api.thecatapi.com/v1/images/search?breed_id=" + id);
        }
        //Send back response
        cim.sendResponse(response, result);
        //create connection to our MongoDB
        MongoDatabase db = cim.connectMongo();
        // end represents response time
        long end = System.currentTimeMillis();
        Date endDate = new java.util.Date(end);
        //Calculate the latency
        long latency = end - start;
        //Get user device
        String deviceUsed = request.getHeader("User-Agent");
        //Insert data into MongoDB
        cim.insertToMongo(db, deviceUsed, id, catType, startDate, endDate, latency);
    }
}
