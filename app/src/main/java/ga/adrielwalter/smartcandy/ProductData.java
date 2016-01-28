package ga.adrielwalter.smartcandy;

import java.util.ArrayList;

/**
 * Created by Adriel on 1/25/2016.
 */
public class ProductData {

    public static String[] placeNameArray = {"Arduino", "Pin Famale", "Stackable", "LCD Screen", "Xbee","NFC RFID"};

    public static ArrayList<Product> placeList() {
        ArrayList<Product> list = new ArrayList<>();
        for (int i = 0; i < placeNameArray.length; i++) {
            Product product = new Product();
            product.name = placeNameArray[i];
            product.imageName = placeNameArray[i].replaceAll("\\s+", "").toLowerCase();
            if (i == 2 || i == 5) {
                product.isFav = true;
            }
            list.add(product);
        }
        return (list);
    }
}
