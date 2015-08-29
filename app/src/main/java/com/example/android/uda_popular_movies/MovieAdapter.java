package com.example.android.uda_popular_movies;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.android.udacity_project_1.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * This is a class which represents a special ArrayAdapter tailored for our purpose.
 * Notice we are extending ArrayAdapter<Movie>.
 *
 * In the constructor, we pass two pieces of data to this Class: the context and the arrayList
 * holding the movies. Later, we override the getView() method as you see below. Inside this
 * method we fetch the data from the ArrayList and create the final url for the poster. Then by
 * the help of Picasso, we call the poster from that URL, place it into our singe item layout,
 * and return the view.
 *
 * Created by Ozgun Ulusoy on 22.08.2015.
 */

public class MovieAdapter extends ArrayAdapter<Movie>{

    private List<Movie> movies;
    private Context context;

    public MovieAdapter(Context context, int resource, List<Movie> movies) {
        super(context, resource, movies);
        this.movies = movies;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = movies.get(position);
        String url = "http://image.tmdb.org/t/p/w185/" + movie.getPosterPath();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.grid_image_item, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.image_view_for_grid_posters);
        Picasso.with(context).load(url).into(imageView);

        return view;
    }
}