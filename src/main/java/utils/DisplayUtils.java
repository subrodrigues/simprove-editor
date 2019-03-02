/*
 * Created by Filipe André Rodrigues on 21-02-2019 16:54
 */

package utils;

import java.awt.*;
import java.util.Random;

public class DisplayUtils {
    public static int NUM_COLORS = 13;

    public static String getRandomColor(){
        int index = new Random().nextInt(13);

        return getColorByIndex(index);
    }

    public static String getRandomColorByIndex(int index){
        int i = index % (NUM_COLORS -1);
        return getColorByIndex(i);
    }

    public static String getColorByIndex(int i) {
        String color = "#FFFFFF";
        switch (i) {
            case 0:
                color = "#8F3F7E";
                break;
            case 1:
                color = "#B5305F";
                break;
            case 2:
                color = "#CE584A";
                break;
            case 3:
                color = "#DB8D5C";
                break;
            case 4:
                color = "#DA854E";
                break;
            case 5:
                color = "#E9AB44";
                break;
            case 6:
                color = "#FEE435";
                break;
            case 7:
                color = "#99C286";
                break;
            case 8:
                color = "#01A05E";
                break;
            case 9:
                color = "#4A8895";
                break;
            case 10:
                color = "#16669B";
                break;
            case 11:
                color = "#2F65A5";
                break;
            case 12:
                color = "#4E6A9C";
                break;
            default:
                break;
        }
        return color;
    }

    public static String getRandomBrightPastelColor(){
        Random random = new Random();

        // to create lighter colours:
        // take a random integer between 0 & 128 (rather than between 0 and 255)
        // and then add 127 to make the colour lighter
        byte[] colorBytes = new byte[3];
        colorBytes[0] = (byte)(random.nextInt(156) + 60);
        colorBytes[1] = (byte)(random.nextInt(156) + 60);
        colorBytes[2] = (byte)(random.nextInt(156) + 60);

        return String.format("#%02x%02x%02x", colorBytes[0], colorBytes[1], colorBytes[2]);
    }

}
