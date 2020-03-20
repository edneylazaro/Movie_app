package com.example.mainstreammovieapp.utilities;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
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
                .inflate(R.layout.movie_card, viewGroup, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MovieAdapter.MyViewHolder holder, int position) {

        holder.title.setText(movieList.get(position).getTitle());
        String vote = Double.toString(movieList.get(position).getVoteAverage());
        holder.userRating.setText(vote);

        String poster = movieList.get(position).getPosterPath();

        Glide.with(mContext)
                .load(poster)
                .apply(new RequestOptions()
                .placeholder(R.mipmap.ic_launcher))
                .into(holder.thumbnail);

    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public class  MyViewHolder extends  RecyclerView.ViewHolder{
        public TextView title, userRating;
        public ImageView thumbnail;

        public MyViewHolder( View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            userRating = (TextView) itemView.findViewById(R.id.user_rating);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        Movie clickedDataItem = movieList.get(position);
                        Intent intent = new Intent(mContext, DetailActivity.class);
                        intent.putExtra("title", movieList.get(position).getTitle());
                        intent.putExtra("poster_path", movieList.get(position).getPosterPath());
                        intent.putExtra("release_date", movieList.get(position).getReleaseDate());
                        intent.putExtra("overview", movieList.get(position).getOverView());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                        Toast.makeText(v.getContext(), "You Clicked " + clickedDataItem.getTitle(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
