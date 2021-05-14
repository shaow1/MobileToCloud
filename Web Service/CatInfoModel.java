/**
 * Author: Wangning Shao
 * Last Modified: April 9th 2021
 *
 * This is model for our web application which performs all the actions
 */
package ds;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class CatInfoModel {

    /**
     * @param response, HttpServletResponse response
     * @param result, json format result get from 3rd party API
     *  return a string response
     */
    static void sendResponse(HttpServletResponse response, String result) throws IOException {

        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        //write data to response
        out.print(result);
        out.flush();
    }

    /**
     * @param searchURL, an URL provided by user
     *  return a string response
     */
    // Parse html content into String
    static String fetch(String searchURL) {

        String response = "";
        try {
            URL url = new URL(searchURL);
            //Create connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            //Provide API key to pass authentication
            connection.setRequestProperty("x-api-key", "xyz");

            // Read all the text returned by the server
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String str;
            // Read each line of "in" until done, adding each to "response"
            while ((str = in.readLine()) != null) {
                // str is one line of text readLine() strips newline characters
                response += str;
            }
            in.close();
        } catch (IOException e) {
            System.err.println("Something wrong with URL");
            return null;
        }
        return response;
    }
    /**
     * @param breedSearch, an search result provided by search cat breed type
     *  return a string which represent cat ID
     */
    // Parse html content into String
    static String getID(String breedSearch)
    {
        // Convert breed search result into JSONArray
        JSONArray jsonArr = new JSONArray(breedSearch);
        JSONObject jsonObj = jsonArr.getJSONObject(0);
        String id = jsonObj.getString("id"); // Get breed id from JSON result we get from API
        return id;
    }

    /**
     * @param catType, a cat breed user provided
     *  return a string which replace space with '%20'
     */
    // Parse html content into String
    static String spaceConversion(String catType)
    {
        //Replacing space with %20
        String[] test = catType.split(" ");
        String catTypeCast = "";
        for(int i = 0; i < test.length; i++)
        {
            if(i != test.length - 1) {
                catTypeCast += (test[i] + "%20"); //Replacing space with %20
            }
            else
            {
                catTypeCast += test[i]; //If it is the last word don't add anything
            }
        }
        return catTypeCast;
    }

    static MongoDatabase connectMongo()
    {
        //uri will be used to connect to our MongoDB on the cloud
        MongoClientURI uri = new MongoClientURI(
                "xyz");
        //Create a MongoClient
        MongoClient mongoClient = new MongoClient(uri);
        //Get the MongoDB which is called myFirstDatabase
        MongoDatabase database = mongoClient.getDatabase("myFirstDatabase");
        return database;
    }

    static void insertToMongo(MongoDatabase database, String deviceUsed, String id, String catType, Date startDate, Date endDate, long latency)
    {
        //get collection named test in our MongoDB
        MongoCollection<Document> collection = database.getCollection("test");
        //Create a document based on information we get from Android
        Document doc = new Document("deviceUsed", deviceUsed)
                .append("catID", id)
                .append("catType", catType)
                .append("startDate", startDate)
                .append("endDate", endDate)
                .append("latency", latency);
        collection.insertOne(doc); // insert it into MongoDB
    }
}
