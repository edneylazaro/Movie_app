package com.example.mainstreammovieapp.utilities;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mainstreammovieapp.BuildConfig;
import com.example.mainstreammovieapp.DetailActivity;
import com.example.mainstreammovieapp.R;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MyViewHolder> {

    private Context mContext;
    private List<Movie> movieList;

   public MovieAdapter(Context context, List<Movie> list){
       this.mContext = context;
       this.movieList = list;
   }



    @Override
    public MovieAdapter.MyViewHolder onCreateViewHolder(final ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.main_card, viewGroup, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MovieAdapter.MyViewHolder holder, int position) {
       String poster = BuildConfig.BASE_IMAGE_URL + movieList.get(position).getPosterPath();

       Glide.with(mContext)
                .load(poster)
                .centerCrop()
                .apply(new RequestOptions()
                .placeholder(R.mipmap.ic_launcher))
                .apply(RequestOptions.overrideOf(1000, 1000))
                .into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public class  MyViewHolder extends  RecyclerView.ViewHolder{
        public ImageView thumbnail;

        public MyViewHolder( View itemView) {
            super(itemView);

            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        Movie clickedDataItem = movieList.get(position);
                        Intent intent = new Intent(mContext, DetailActivity.class);
                        intent.putExtra("movies", clickedDataItem );
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    }
                }
            });
        }
    }
}
