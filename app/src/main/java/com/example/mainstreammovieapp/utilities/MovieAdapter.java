package com.example.mainstreammovieapp.utilities;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
    @NonNull
    @Override
    public MovieAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
        .inflate(R.layout.movie_layout, viewGroup, false);
       return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MovieAdapter.MyViewHolder holder, int position) {

        holder.title.setText(movieList.get(position).getTitle());

        Glide.with(mContext)
                .load(movieList.get(position).getPosterPath()).into(holder.thumbnail);

    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public class  MyViewHolder extends  RecyclerView.ViewHolder{
        public TextView title;
        public ImageView thumbnail;

        public MyViewHolder( View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.);
            thumbnail = (ImageView) itemView.findViewById(R.id.movie_thumbnail);

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
