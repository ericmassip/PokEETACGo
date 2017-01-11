package edu.upc.eetac.dsa.pokeetacgo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Random;

import edu.upc.eetac.dsa.pokeetacgo.squaresGame.GamePanel;

public class CaptureProfemonActivity extends Activity {
    int requestIdOfGeofenceTriggered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Bundle intentData = getIntent().getExtras();
        requestIdOfGeofenceTriggered = intentData.getInt("requestIdOfGeofenceTriggered");

        setContentView(new GamePanel(this, requestIdOfGeofenceTriggered));
    }
}
