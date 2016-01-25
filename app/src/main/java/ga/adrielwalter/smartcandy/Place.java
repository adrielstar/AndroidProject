package ga.adrielwalter.smartcandy;

import android.content.Context;

/**
 * Created by Adriel on 1/25/2016.
 */
public class Place {

    public String name;
    public String imageName;
    public boolean isFav;

    public int getImageResourceId(Context context) {
        return context.getResources().getIdentifier(this.imageName, "drawable", context.getPackageName());
    }
}
