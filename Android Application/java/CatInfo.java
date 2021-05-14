/**
 * Author: Wangning Shao
 * Last Modified: April 9th 2021
 *
 * All Android application action will be handled in this class
 */
package edu.cmu.project4task1android;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CatInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
         * The click listener will need a reference to this object, so that upon successfully finding information about cats breed, it
         * can callback to this object with the resulting picture Bitmap.  The "this" of the OnClick will be the OnClickListener, not
         * this CatInfo.
         */
        final CatInfo ma = this;

        /*
         * Find the "submit" button, and add a listener to it
         */
        Button submitButton = (Button) findViewById(R.id.submit);

        // Add a listener to the send button
        submitButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View viewParam) {
                String searchTerm = ((EditText) findViewById(R.id.search)).getText().toString(); // get user input text
                GetCatInfo gp = new GetCatInfo();
                gp.search(searchTerm, ma); // Done asynchronously in another thread.  It calls ip.pictureReady() in this thread when complete.
            }
        });
    }

    /*
     * This is called by the GetPicture object when the picture is ready.  This allows for passing back the Bitmap picture for updating the ImageView
     */
    public void pictureReady(Bitmap picture) {
        ImageView pictureView = (ImageView) findViewById(R.id.catPicture);
        TextView searchView = (EditText)findViewById(R.id.search);
        if (picture != null) {
            TextView resultView = findViewById(R.id.resultView);
            resultView.setText("Here is a picture of a " + searchView.getText());
            pictureView.setImageBitmap(picture);
            pictureView.setVisibility(View.VISIBLE);
        } else {
            TextView resultView = findViewById(R.id.resultView);
            resultView.setText("Sorry, I could not find a picture of a " + searchView.getText());
            pictureView.setImageResource(R.mipmap.ic_launcher);
            pictureView.setVisibility(View.INVISIBLE);
        }
        searchView.setText("");
        pictureView.invalidate();
    }

    public void setCatInfo(JSONArray jsonArr) throws JSONException {

        //Information regarding cat breed is located inside "breed" its an array of dictionary
        JSONObject jsonObj = jsonArr.getJSONObject(0);
        String breedsJSON = jsonObj.get("breeds").toString();
        JSONArray jsonArrBreeds = new JSONArray(breedsJSON);
        JSONObject jsonObj2 = jsonArrBreeds.getJSONObject(0);

        String name = jsonObj2.getString("name"); // Get breed type from JSON result we get from API
        String origin = jsonObj2.getString("origin"); // Get origin from JSON result we get from API
        String temperament = jsonObj2.getString("temperament"); // Get temperament from JSON result we get from API
        String cfaURL = jsonObj2.getString("cfa_url"); // Get cfa URL from JSON result we get from API
        String lifeSpan = jsonObj2.getString("life_span"); // Get life span from JSON result we get from API
        String adaptability = Integer.toString(jsonObj2.getInt("adaptability")); // Get adaptability from JSON result we get from API
        String childFriendly = Integer.toString(jsonObj2.getInt("child_friendly")); // Get child friend level from JSON result we get from API
        String dogFriendly = Integer.toString(jsonObj2.getInt("dog_friendly")); // Get dog friend level from JSON result we get from API
        String healthIssues = Integer.toString(jsonObj2.getInt("health_issues")); // Get health rate from JSON result we get from API
        String socialNeeds =  Integer.toString(jsonObj2.getInt("social_needs")); // Get social need level from JSON result we get from API

        TextView breadName = findViewById(R.id.breed); //find the textView
        breadName.setText(name); //set textView value to value we get from our API
        TextView originFrom = findViewById(R.id.origin); //find the textView
        originFrom.setText(origin); //set textView value to value we get from our API
        TextView temp = findViewById(R.id.temperament); //find the textView
        temp.setText(temperament); //set textView value to value we get from our API
        TextView cfaUrl = findViewById(R.id.cfa); //find the textView
        cfaUrl.setText(cfaURL); //set textView value to value we get from our API
        TextView lifeS = findViewById(R.id.lifeSpan); //find the textView
        lifeS.setText(lifeSpan); //set textView value to value we get from our API
        TextView adapt = findViewById(R.id.adaptability); //find the textView
        adapt.setText(adaptability); //set textView value to value we get from our API
        TextView childF = findViewById(R.id.childFriendly); //find the textView
        childF.setText(childFriendly); //set textView value to value we get from our API
        TextView dogF = findViewById(R.id.dogFriendly); //find the textView
        dogF.setText(dogFriendly); //set textView value to value we get from our API
        TextView healthy = findViewById(R.id.healthIssue); //find the textView
        healthy.setText(healthIssues); //set textView value to value we get from our API
        TextView social = findViewById(R.id.socialNeeds); //find the textView
        social.setText(socialNeeds); //set textView value to value we get from our API
    }
}
