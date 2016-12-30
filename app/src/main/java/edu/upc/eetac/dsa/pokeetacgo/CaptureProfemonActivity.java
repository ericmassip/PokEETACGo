package edu.upc.eetac.dsa.pokeetacgo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.Random;

public class CaptureProfemonActivity extends AppCompatActivity {
    TextView randomNumber;
    int randomNum;
    int requestIdOfGeofenceTriggered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_profemon);
        randomNumber = (TextView) findViewById(R.id.randomNumber);

        Bundle intentData = getIntent().getExtras();
        requestIdOfGeofenceTriggered = intentData.getInt("requestIdOfGeofenceTriggered");

        Random random = new Random();
        randomNum = random.nextInt(100);
        randomNumber.setText(String.valueOf(randomNum));
    }

    public void goBackToMaps(View view) {
        Intent goBackToMaps = new Intent(this, MapsActivity.class);
        boolean capturadoIsSuccessful = randomNum > 40;
        goBackToMaps.putExtra("capturadoIsSuccessful", capturadoIsSuccessful);
        goBackToMaps.putExtra("requestIdOfGeofenceTriggered", requestIdOfGeofenceTriggered);
        goBackToMaps.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(goBackToMaps);
        finish();
    }
}
