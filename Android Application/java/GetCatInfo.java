/**
 * Author: Wangning Shao
 * Last Modified: April 9th 2021
 *
 * This is main Activity for our Android Application
 */
package edu.cmu.project4task1android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/*
 * This class provides capabilities to search for information about specific cat breed given a breed type.  The method "search" is the entry to the class.
 * Network operations cannot be done from the UI thread, therefore this class makes use of an AsyncTask inner class that will do the network
 * operations in a separate worker thread.  However, any UI updates should be done in the UI thread so avoid any synchronization problems.
 * onPostExecution runs in the UI thread, and it calls the ImageView pictureReady method to do the update.
 *
 */
public class GetCatInfo {
    CatInfo ci = null;

    /*
     * search is the public GetPicture method.  Its arguments are the search term, and the InterestingPicture object that called it.  This provides a callback
     * path such that the pictureReady method in that object is called when the picture is available from the search.
     */
    public void search(String searchTerm, CatInfo ci){
        this.ci = ci;
        new AsyncFlickrSearch().execute(searchTerm);
    }
    JSONArray jsonArr = null;
    /*
     * AsyncTask provides a simple way to use a thread separate from the UI thread in which to do network operations.
     * doInBackground is run in the helper thread.
     * onPostExecute is run in the UI thread, allowing for safe UI updates.
     */
    private class AsyncFlickrSearch extends AsyncTask<String, Void, Bitmap> {
        protected Bitmap doInBackground(String... urls) {
            try {
                return search(urls[0]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Bitmap picture) {
            ci.pictureReady(picture); //set up image
            try {
                ci.setCatInfo(jsonArr); //set up UI content
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /*
         * Search on our web application for the cat breed argument, and return a Bitmap that can be put in an ImageView
         */
        private JSONArray getCatInfo(String breed) throws JSONException {

            String response = "";
            HttpURLConnection conn;
            int status = 0;
            try {
                // pass the name on the URL line
                URL url = new URL("https://cryptic-river-08340.herokuapp.com/getCatInfo?type=" + breed);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                // tell the server what format we want back
                conn.setRequestProperty("Accept", "text/plain");

                // wait for response
                status = conn.getResponseCode();

                // If things went poorly, don't try to read any response, just return.
                if (status != 200) {
                    // not using msg
                    String msg = conn.getResponseMessage();
                    System.out.println("Error: " + conn.getResponseCode());
                }
                String output = "";
                // things went well so let's read the response
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));
                //read data to response
                while ((output = br.readLine()) != null) {
                    response += output;
                }
                conn.disconnect();
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }   catch (IOException e) {
                e.printStackTrace();
            }
            //If the response if null we create an empty array using '[]'
            if(response.length() == 0)
            {
                response = "[]";
            }
            JSONArray jsonArr = new JSONArray(response);
            return jsonArr;
        }

        private Bitmap search(String searchTerm) throws JSONException {
            // Get API response in JSON format
            jsonArr = getCatInfo(searchTerm);
            //If no cat information found return null
            if(jsonArr.length() == 0)
            {
                return null;
            }
            else
            {
                // Get image url inside JSON array
                JSONObject jsonObj = jsonArr.getJSONObject(0);
                String pictureURL = jsonObj.getString("url");

                // At this point, we have the URL of the picture that resulted from the search.  Now load the image itself.
                try {
                    URL u = new URL(pictureURL);
                    return getRemoteImage(u);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null; // so compiler does not complain
                }
            }
        }


        /*
         * Given a URL referring to an image, return a bitmap of that image
         */
        private Bitmap getRemoteImage(final URL url) {
            try {
                final URLConnection conn = url.openConnection(); // start the connection
                conn.connect();
                BufferedInputStream bis = new BufferedInputStream(conn.getInputStream()); //read input
                Bitmap bm = BitmapFactory.decodeStream(bis);
                bis.close();
                return bm;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
