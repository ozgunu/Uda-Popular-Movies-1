package com.example.android.uda_popular_movies;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.udacity_project_1.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Ozgun Ulusoy on 25.08.2015.
 */

public class DetailFragment extends Fragment {

    private final String KEY = "MOVIE_KEY_FOR_BUNDLE";
    private ImageView imageView;
    private String posterURL;
    private Movie movie;
    private Context context;

    private LinearLayout rowTitle;
    private LinearLayout rowOriginalTitle;
    private LinearLayout rowReleaseDate;
    private LinearLayout rowPopularity;
    private LinearLayout rowUserRating;
    private LinearLayout rowOverview;

    private TextView movieTitleField;
    private TextView releaseDateField;
    private TextView originalTitleField;
    private TextView popularityField;
    private TextView userRatingField;
    private TextView overviewField;

    ArrayList<LinearLayout> visibleRows = new ArrayList<>();     // All visible rows will be added to this, in order.
                                                                 // Later we can assign a light background color to
                                                                 // the first one, and a dark one to the next, and a
                                                                 // light one to the next etc..

    // Constructor
    public DetailFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();

        if (bundle != null) {
            movie = bundle.getParcelable(KEY);
        }

        if (bundle == null) {

        }

        // Instantiate the arrayList
        visibleRows = new ArrayList<>();

        // Next we instantiate all Rows (LinearLayout).
        // These will be used to hide / show rows, if needed in the future
        rowTitle = (LinearLayout) view.findViewById(R.id.row_title);
        rowOriginalTitle = (LinearLayout) view.findViewById(R.id.row_original_title);
        rowReleaseDate = (LinearLayout) view.findViewById(R.id.row_release_date);
        rowPopularity = (LinearLayout) view.findViewById(R.id.row_popularity);
        rowUserRating = (LinearLayout) view.findViewById(R.id.row_user_rating);
        rowOverview = (LinearLayout) view.findViewById(R.id.row_overview);

        // Instantiate all TextViews
        movieTitleField = (TextView) view.findViewById(R.id.textView_movieTitle_field);
        originalTitleField = (TextView) view.findViewById(R.id.textView_originalTitle_field);
        releaseDateField = (TextView) view.findViewById(R.id.textView_releaseDate_field);
        popularityField = (TextView) view.findViewById(R.id.textView_popularity_field);
        userRatingField = (TextView) view.findViewById(R.id.textView_userRating_field);
        overviewField = (TextView) view.findViewById(R.id.textView_overView_field);

        // Set up the context for later use
        context = view.getContext();

        // Building the poster url for an image with size 780 and placing the image to the imageView
        if (movie != null) {
            posterURL = "http://image.tmdb.org/t/p/w780/" + movie.getPosterPath();
            imageView = (ImageView) view.findViewById(R.id.detail_fragment_image_view);
            Picasso.with(context).load(posterURL).into(imageView);
        }




        // Placing all other text info to the appropriate fields.
        // First we check if the info is there, if so we print it to screen.
        // If not, we print N/A to the corresponding field.

        if (movie != null) {

            if (movie.getTitle().equals("null")) {
                movieTitleField.setText(getString(R.string.unknown));
            } else movieTitleField.setText(movie.getTitle());


            // If the original title is same with title, we simply hide the whole row showing this info
            if (movie.getOriginalTitle().equals(movie.getOriginalTitle()) || movie.getOriginalTitle().equals("null")) {
                rowOriginalTitle.setVisibility(View.GONE);
            } else originalTitleField.setText(movie.getOriginalTitle());

            if (movie.getReleaseDate().equals("null") || movie.getReleaseDate().equals(null))
                releaseDateField.setText(getString(R.string.unknown));
            else releaseDateField.setText(movie.getReleaseDate());

            if (Double.toString(movie.getPopularity()).equals(null))
                popularityField.setText(R.string.unknown);
            else popularityField.setText(Double.toString(movie.getPopularity()));

            if (Double.toString(movie.getVoteAverage()).equals(null))
                userRatingField.setText(R.string.unknown);
            else userRatingField.setText(Double.toString(movie.getVoteAverage()));

            if (movie.getOverview().equals("null") || movie.getOverview().equals(null))
                overviewField.setText(R.string.unknown);
            else overviewField.setText(movie.getOverview());

        }



        // If the row is VISIBLE, we add it to the arrayList so later we can
        // assign background colors to each consecutive row by iterating through the list.

        visibleRows.clear();
        Boolean darkBackground = true;

        if (rowTitle.getVisibility() != View.GONE) {visibleRows.add(rowTitle);}
        if (rowOriginalTitle.getVisibility() != View.GONE) {visibleRows.add(rowOriginalTitle);}
        if (rowReleaseDate.getVisibility() != View.GONE) {visibleRows.add(rowReleaseDate);}
        if (rowPopularity.getVisibility() != View.GONE) {visibleRows.add(rowPopularity);}
        if (rowUserRating.getVisibility() != View.GONE) {visibleRows.add(rowUserRating);}
        if (rowOverview.getVisibility() != View.GONE) {visibleRows.add(rowOverview);}

        for (LinearLayout l : visibleRows) {
            if (darkBackground) {
                l.setBackgroundColor(getResources().getColor(R.color.box_background_dark));
                darkBackground = false;
            }
            else {
                l.setBackgroundColor(getResources().getColor(R.color.box_background_light));
                darkBackground = true;
            }
        }

    }

}



