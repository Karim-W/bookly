package com.cmp354.bookly;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class PostAdapter extends ArrayAdapter<Book> {
    private static final String TAG = "PersonListAdapter";

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;


    private static class ViewHolder {
        TextView tv_bookTitle;
        TextView tv_date;
        ImageView iv_bookposter;
    }

    /**
     * Default constructor for the PersonListAdapter
     * @param context
     * @param resource
     * @param objects
     */
    public PostAdapter(Context context, int resource, ArrayList<Book> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //get the persons information
        String title = getItem(position).title;
        String date = getItem(position).year+"";
        Bitmap image = getItem(position).image;

        //create the view result for showing the animation
        final View result;

        //ViewHolder object
        ViewHolder holder;


        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder= new ViewHolder();
            holder.tv_bookTitle = (TextView) convertView.findViewById(R.id.tv_bookTitle);
            holder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
            holder.iv_bookposter = (ImageView) convertView.findViewById(R.id.iv_bookposter);

            result = convertView;

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }


        @SuppressLint("ResourceType") Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.animator.load_down_anim : R.animator.load_up_anim);
        result.startAnimation(animation);
        lastPosition = position;

        holder.tv_bookTitle.setText(title);
        holder.tv_date.setText(date);
        holder.iv_bookposter.setImageBitmap(image);

        return convertView;
    }
}
