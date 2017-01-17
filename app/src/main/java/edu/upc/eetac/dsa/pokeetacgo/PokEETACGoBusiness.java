package edu.upc.eetac.dsa.pokeetacgo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.SparseArray;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import edu.upc.eetac.dsa.pokeetacgo.entity.LocationMarker;
import edu.upc.eetac.dsa.pokeetacgo.entity.serviceLibraryResults.ProfemonLocationResult;

/**
 * Created by ericmassip on 24/12/16.
 */

public class PokEETACGoBusiness {
    private Context context;

    public void init(Context context){
        if(this.context == null){
            this.context = context;
        }
    }

    private Context getContext(){
        return context;
    }

    public Bitmap getProfemonIcon(String profemonName) {
        int height = 150;
        int width = 150;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), getProfemonDrawableResourceId(profemonName));
        return Bitmap.createScaledBitmap(bitmap, width, height, false);
    }

    public int getProfemonDrawableResourceId(String iconName) {
        return this.context.getResources().getIdentifier(iconName.toLowerCase(), "drawable", context.getPackageName());
    }

    public boolean isEmpty(EditText editText) {
        return editText.getText().toString().isEmpty();
    }

    public List<LocationMarker> getLocationMarkersOfCurrentFloor(int floor, SparseArray<LocationMarker> profemonLocationMarkers) {
        List<LocationMarker> profemonLocationMarkersOfCurrentFloor = new ArrayList<>();
        for(int i = 0; i < profemonLocationMarkers.size(); i++) {
            LocationMarker locationMarker = profemonLocationMarkers.get(profemonLocationMarkers.keyAt(i));
            ProfemonLocationResult profemonLocationResult = locationMarker.getProfemonLocationResult();
            if(profemonLocationResult.floor == floor) {
                profemonLocationMarkersOfCurrentFloor.add(locationMarker);
            }
        }
        return profemonLocationMarkersOfCurrentFloor;
    }
}
