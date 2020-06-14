package org.lineageos.eleven.widgets;

import java.util.Hashtable;

import android.content.Context;
import android.graphics.Typeface;

public class Roboto {

    public final static int ROBOTO_LIGHT = 0;
    public final static int ROBOTO_REGULAR = 1;
    public final static int ROBOTO_MEDIUM = 2;
    
    private static Hashtable<Integer, Typeface> sTypefaces = new Hashtable<Integer, Typeface>(3);
    
    public static Typeface getTypeface(Context context, int typefaceValue) throws IllegalArgumentException {
        Typeface typeface = sTypefaces.get(typefaceValue);
        if (typeface == null) {
            typeface = Roboto.createTypeface(context, typefaceValue);
            sTypefaces.put(typefaceValue, typeface);
        }
        return typeface;
    }
    
    private static Typeface createTypeface(Context context, int typefaceValue) throws IllegalArgumentException {
        String path;
        switch (typefaceValue) {
            case ROBOTO_LIGHT:
                path = "Roboto-Light.ttf";
                break;
            case ROBOTO_REGULAR:
                path = "Roboto-Regular.ttf";
                break;
            case ROBOTO_MEDIUM:
                path = "Roboto-Medium.ttf";
                break;
            default:
                throw new IllegalArgumentException("Unknown `typeface` attribute value " + typefaceValue);
        }
        return Typeface.createFromAsset(context.getAssets(), path);
    }

}
