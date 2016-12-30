package edu.upc.eetac.dsa.pokeetacgo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import edu.upc.eetac.dsa.pokeetacgo.entity.serviceLibraryResults.ProfemonLocationResult;

public class ProfemonAppearedActivity extends AppCompatActivity {
    ImageView profemonImage;
    PokEETACGo pokEETACGo = PokEETACGo.getInstance();
    PokEETACGoBusiness pokEETACGoBusiness = new PokEETACGoBusiness();
    int requestIdOfGeofenceTriggered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profemon_appeared);
        profemonImage = (ImageView) findViewById(R.id.profemonImage);
        pokEETACGoBusiness.init(getApplicationContext());

        Bundle intentData = getIntent().getExtras();
        requestIdOfGeofenceTriggered = intentData.getInt("requestIdOfGeofenceTriggered");
        ProfemonLocationResult profemonLocationAppeared = pokEETACGo.profemonLocationMarkers.get(requestIdOfGeofenceTriggered).getProfemonLocationResult();
        profemonImage.setImageResource(pokEETACGoBusiness.getProfemonDrawableResourceId(profemonLocationAppeared.name));
    }

    public void captureMe(View view) {
        Intent captureProfemonIntent = new Intent(this, CaptureProfemonActivity.class);
        captureProfemonIntent.putExtra("requestIdOfGeofenceTriggered", requestIdOfGeofenceTriggered);
        startActivity(captureProfemonIntent);
    }
}
