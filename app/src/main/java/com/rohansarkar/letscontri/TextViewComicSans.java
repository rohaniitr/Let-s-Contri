package com.rohansarkar.letscontri;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by rohan on 13/1/16.
 */
public class TextViewComicSans extends TextView {

    public TextViewComicSans(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "Comic San.ttf"));
    }

}
