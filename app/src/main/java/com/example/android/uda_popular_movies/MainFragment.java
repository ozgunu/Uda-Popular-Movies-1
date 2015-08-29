package com.example.android.uda_popular_movies;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import com.example.android.udacity_project_1.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ozgun Ulusoy on 20.08.2015.
 *
 */
public class MainFragment extends Fragment {

    // Constructor
    public MainFragment(){}

    private MovieAdapter myArrayAdapter;
    private Button buttonSortByPopularity;
    private Button buttonSortByRating;
    private GridView gridView;
    private String sortMethod;


    // Instance of our interface will be used to pass data to activity
    private OnItemSelectedListener mCallback;


    // Container Activity must implement this interface
    // This is used to share data (the position number of the
    // selected item with the Main Activity and then
    // with the other fragments..
    public interface OnItemSelectedListener {
        public void onItemSelected(int position, long id, Movie movie);
    }

    // Here we initialize mCallback (instance of our interface)
    // and make sure that the container activity has implemented the callback interface.
    // If not, it throws an exception.
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnItemSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnItemSelectedListener");
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();

        if (sortMethod == null) {
            fetchMoviesTask.execute("popularity.desc");
        } else {
            fetchMoviesTask.execute(sortMethod);
        }

        myArrayAdapter = new MovieAdapter(
                getActivity(),                      // context
                R.layout.grid_image_item,           // layout for single item
                new ArrayList<Movie>()              // temporary ArrayList for now
        );

        gridView = (GridView) rootView.findViewById(R.id.grid_view);

        // Assign the adapter to the gridView
        gridView.setAdapter(myArrayAdapter);

        // Set the clickListener on the gridView.
        // This way we will know which item in the grid the user selected.
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Below we call the method of our onItemSelected interface. We pass the position information
                // and the Movie object corresponding to the user's selection are passed as the method's parameters.
                // This way we pass the data to the main activity because our main activity is implementing
                // our onListItemSelected interface and actually the instance of the interface mCallback is
                // directly attached to the activity!
                Movie movie = myArrayAdapter.getItem(position);
                mCallback.onItemSelected(position, id, movie);
            }
        });

        return rootView;
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // this is an important method. helps the fragment remember its attribute values
        // when it is reattached to another container etc.. this way every time we rotate
        // the device it can remember what the sortMethod attribute is set to... So user
        // continues to see the same movie posters.
        setRetainInstance(true);

        // Instantiate the buttons
        buttonSortByPopularity = (Button) view.findViewById(R.id.button_sort_popularity);
        buttonSortByRating = (Button) view.findViewById(R.id.button_sort_rating);

        // Click handler for "Sort By Popularity" button
        buttonSortByPopularity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
                fetchMoviesTask.execute("popularity.desc");
                sortMethod = "popularity.desc";
            }
        });

        // Click handler for "Sort By Rating" button
        buttonSortByRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
                fetchMoviesTask.execute("vote_average.desc");
                sortMethod = "vote_average.desc";
            }
        });

    }


    // An inner class for the background thread. The purpose of this class is to
    // access theMovieDb.org website, fetch the top movies sorted by either popularity
    // or user vote.
    //
    // THE API KEY INFORMATION SHOULD BE ASSIGNED TO THE CONSTANT API_KEY BELOW.

    public class FetchMoviesTask extends AsyncTask<String, Void, List<Movie>> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected List<Movie> doInBackground(String... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonString = null;

            try {
                // Defining the elements of the URL for accessing themoviedb.org website
                // The working url will be something like:
                // http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=[YOUR API KEY]
                final String BASE_URL = "http://api.themoviedb.org/3/discover/movie";
                final String SORT_PARAM = "sort_by";
                final String API_KEY_PARAM ="api_key";
                final String API_KEY = "******************* YOUR API KEY FROM THEMOVIEDB.ORG GOES HERE ********************";

                // Building the final URL from the above elements using Uri Class
                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, params[0])
                        .appendQueryParameter(API_KEY_PARAM, API_KEY)
                        .build();
                URL url = new URL(builtUri.toString());

                // Creating the request to The Movie Database, and opening the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  There is no point in parsing.
                    return null;
                }

                // Saving the information to our moviesJsonString variable
                moviesJsonString = buffer.toString();
            }

            catch (IOException e) {
                Log.e(LOG_TAG, "IO Exception --> ", e);
            }

            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            if (moviesJsonString != null) {
                return getDataFromJSon(moviesJsonString);
            } else return null;
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            myArrayAdapter.clear();

            if (movies != null) {    // We check this in order to avoid a crash that occurs
                                     // only on the first time we run the app. Will come back to this later.
                for (Movie movie : movies) {
                    myArrayAdapter.add(movie);
                }
            }
        }


        public List<Movie> getDataFromJSon(String string) {

            List<Movie> movies = new ArrayList<Movie>();

            final String KEY_GENRE_IDS = "genre_ids";
            final String KEY_ID = "id";
            final String KEY_ORIGINAL_LANGUAGE = "original_language";
            final String KEY_TITLE = "title";
            final String KEY_ORIGINAL_TITLE = "original_title";
            final String KEY_OVERVIEW = "overview";
            final String KEY_RELEASE_DATE = "release_date";
            final String KEY_POSTER_PATH = "poster_path";
            final String KEY_POPULARITY = "popularity";
            final String KEY_VIDEO = "video";
            final String KEY_VOTE_AVERAGE = "vote_average";
            final String KEY_VOTE_COUNT = "vote_count";

            try {
                JSONObject moviesJSONObject = new JSONObject(string);
                JSONArray resultsArray = moviesJSONObject.getJSONArray("results");

                for (int i = 0; i < resultsArray.length(); i++) {

                    // For each iteration we create a new Movie object
                    Movie movie = new Movie();

                    // We set the attributes of this new Movie object with the data from JSON
                    movie.setId(resultsArray.getJSONObject(i).getString(KEY_ID));
                    movie.setTitle(resultsArray.getJSONObject(i).getString(KEY_TITLE));
                    movie.setOriginalTitle(resultsArray.getJSONObject(i).getString(KEY_ORIGINAL_TITLE));
                    movie.setOverview(resultsArray.getJSONObject(i).getString(KEY_OVERVIEW));
                    movie.setPopularity(resultsArray.getJSONObject(i).getDouble(KEY_POPULARITY));
                    movie.setPosterPath(resultsArray.getJSONObject(i).getString(KEY_POSTER_PATH));
                    movie.setReleaseDate(resultsArray.getJSONObject(i).getString(KEY_RELEASE_DATE));
                    movie.setVideoStatus(resultsArray.getJSONObject(i).getString(KEY_VIDEO));
                    movie.setVoteAverage(resultsArray.getJSONObject(i).getDouble(KEY_VOTE_AVERAGE));
                    movie.setVoteCount(resultsArray.getJSONObject(i).getInt(KEY_VOTE_COUNT));

                    // We add the new movie object into our 'movies' list
                    movies.add(movie);
                } // end for
            } // end try

            catch (JSONException ex) {
                Log.e(LOG_TAG, "JSON Exceptipn --> ", ex);
            }

            // Return the populated ArrayList
            return movies;
        }
    } // End of inner class FetchMoviesTask (Extends AsyncTask)
}
