package com.sujan.calculatedistance;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;



public class MainActivity extends AppCompatActivity {
    //initialize variables
    EditText etSource,etDestination;
    TextView textView;
    String sType;
    double lat1=0, long1 = 0, lat2 =0, long2=0;
    int flag =0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //assign variables
        etSource = findViewById(R.id.et_source);
        etDestination = findViewById(R.id.et_destination);
        textView = findViewById(R.id.text_view);

        //AIzaSyA6YLVzDtHDvu-kcc5qiCmK1zdF0YGdNV0
        //initialize places //AIzaSyCHDpzM2OhaHUgWEiPZYn9l3US5JFzdoGU
        Places.initialize(getApplicationContext(),"AIzaSyCHDpzM2OhaHUgWEiPZYn9l3US5JFzdoGU");


        //set edit text non focusable
        etSource.setFocusable(false);
        etSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //define type
                sType ="source";

                //initialize place feild list
                List<Place.Field> fields = Arrays.asList(Place.Field.ADDRESS,Place.Field.LAT_LNG);

                //crrete intent
                Intent intent= new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.OVERLAY,fields
                ).build(MainActivity.this);

                //Start activity
                startActivityForResult(intent,0);
            }
        });

        //set edit text non focusable
        etDestination.setFocusable(false);
        etDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //define type
                sType = "destination";

                //initialize palce feild list
                List<Place.Field> fields = Arrays.asList(Place.Field.ADDRESS,Place.Field.LAT_LNG);

                //create intent
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.OVERLAY,fields
                ).build(MainActivity.this);

                //start activity result
                startActivityForResult(intent,0);
            }
        });

        //set text on text view
        textView.setText("0.0 Kilometers");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //check condition
        if(requestCode == 100 && resultCode == RESULT_OK){
            //WHEN SUCCESS
            //INITIALIZE PLACE
            Place place = Autocomplete.getPlaceFromIntent(data);
            if(sType.equals("source")){
                //when type is source
                //increse flage value
                flag++;

                //set address on edit
                etSource.setText(place.getAddress());

                //get Latitude and Longitude
                String sSource = String.valueOf(place.getLatLng());
                sSource = sSource.replaceAll("lat/lng","");
                sSource = sSource.replace("(","");
                sSource = sSource.replace("","");
                String[] split = sSource.split(",");
                lat1 = Double.parseDouble(split[0]);
                long1 = Double.parseDouble(split[1]);
            }else {
                //when type is destination
                //increase flag value
                flag++;
                //set address on edit text
                etDestination.setText(place.getAddress());

                //get Latitude and Longitude
                String sDestination = String.valueOf(place.getLatLng());
                sDestination = sDestination.replaceAll("lat/lng","");
                sDestination = sDestination.replace("(","");
                sDestination = sDestination.replace("","");
                String[] split = sDestination.split(",");
                lat2 = Double.parseDouble(split[0]);
                long2 = Double.parseDouble(split[1]);
            }

            //check condition
            if(flag >= 2){
                //when flag is grater than and equal to 2
                //calculate distance
                distance(lat1,long1,lat2,long2);
            }else if(requestCode == AutocompleteActivity.RESULT_ERROR){
                //when error
                //initialize status
                Status status = Autocomplete.getStatusFromIntent(data);

                //display toast
                Toast.makeText(getApplicationContext(),status.getStatusMessage(),Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void distance(double lat1, double long1, double lat2, double long2) {
        //calculate longitude diffetrence
        double longDiff = long1-long2;
        
        //calculate distance
        double distance = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(longDiff));

        distance = Math.acos(distance);
        //convert distance radian to degree
        distance = rag2deg(distance);

        //distance in miles
        distance = distance * 60 * 1.1515;

        //distance in kilometers
        distance = distance * 1.609344;

        //set distance on text view
        textView.setText(String.format(Locale.US,"%2f Kilometers",distance));
        
    }

    //convert radian to degree
    private double rag2deg(double distance) {
        return  (distance * 180.0 / Math.PI);
    }


    //convert degree to radium
    private double deg2rad(double lat1) {
        return (lat1*Math.PI/180.0);
    }
}