package com.rohansarkar.letscontri;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by rohan on 29/9/15.
 */
public class HelpAdapter extends PagerAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;
    int[] mResources = { R.drawable.help_home, R.drawable.help_calculate };
    String[] screenNames= { "Home", "Calculate"};

    public HelpAdapter(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mResources.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.element_help, container, false);

        ImageView screenImage = (ImageView) itemView.findViewById(R.id.ivHelp);
        TextView screenName= (TextView) itemView.findViewById(R.id.tvHelpScreenName);
        screenImage.setImageResource(mResources[position]);
        screenName.setText(screenNames[position]);

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}