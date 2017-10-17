package com.gujjubites;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 9/11/2017.
 */
public class CustomSwiperAdapter extends RecyclerView.Adapter<CustomSwiperAdapter.RecyclerViewHolder> {

    Context ctx;
    private List<addNews> addnews = new ArrayList<>();

    public CustomSwiperAdapter(Context ctx, List<addNews> addnews)
    {
        this.ctx = ctx;
        this.addnews = addnews;
    }
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.swipe_layout, parent, false);
        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);
        return recyclerViewHolder;
    }
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {

        //holder.newsImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Picasso.with(ctx).load(addnews.get(position).getImg_url()).fit().into(holder.newsImage);
        holder.newsTitle.setText(addnews.get(position).getTitle());
        holder.newsDescription.setText(addnews.get(position).getDescription());
        holder.newsSubmitter.setText("Bites by " + addnews.get(position).getUser());
        /*holder..setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setTitle(addnews.get(position).getTitle());
                builder.setMessage(addnews.get(position).getDescription());
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });*/
    }

    public int getItemCount() {
        return addnews.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        ImageView newsImage;
        TextView newsTitle;
        TextView newsDescription;
        TextView newsSubmitter;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            newsImage = (ImageView) itemView.findViewById(R.id.news_image);
            newsDescription = (TextView) itemView.findViewById(R.id.news_description);
            newsTitle = (TextView) itemView.findViewById(R.id.news_title);
            newsSubmitter = (TextView) itemView.findViewById(R.id.news_submitter);
            }
    }
}