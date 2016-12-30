package edu.upc.eetac.dsa.pokeetacgo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.EditText;

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
}
