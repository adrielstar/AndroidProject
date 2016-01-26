package ga.adrielwalter.smartcandy;

import java.util.ArrayList;

/**
 * Created by Adriel on 1/25/2016.
 */
public class PlaceData {

    public static String[] placeNameArray = {"Arduino", "Pin Famale", "Stackable", "LCD Screen", "Xbee","NFC RFID"};

    public static ArrayList<Place> placeList() {
        ArrayList<Place> list = new ArrayList<>();
        for (int i = 0; i < placeNameArray.length; i++) {
            Place place = new Place();
            place.name = placeNameArray[i];
            place.imageName = placeNameArray[i].replaceAll("\\s+", "").toLowerCase();
            if (i == 2 || i == 5) {
                place.isFav = true;
            }
            list.add(place);
        }
        return (list);
    }
}
