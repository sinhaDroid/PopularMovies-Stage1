package me.deepanshusinha.popularmovies.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import java.util.Arrays;
import java.util.List;

import me.deepanshusinha.popularmovies.R;
import me.deepanshusinha.popularmovies.adapter.MyMovieRecyclerViewAdapter;
import me.deepanshusinha.popularmovies.model.Movie;

import static android.net.Uri.parse;
import static me.deepanshusinha.popularmovies.utils.Constants.API;
import static me.deepanshusinha.popularmovies.utils.Constants.JSON;
import static me.deepanshusinha.popularmovies.utils.Constants.SAVE_LAST_ORDER;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class MovieFragment extends Fragment {

    private int mColumnCount = 2;
    private OnListFragmentInteractionListener mListener;
    private MyMovieRecyclerViewAdapter mAdapter;
    private List<Movie> mMoviesList;
    private String mLastUpdateOrder;
    private FetchMovieApiTask mTask;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieFragment() {
    }

    public static MovieFragment newInstance() {
        return new MovieFragment();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVE_LAST_ORDER, mLastUpdateOrder);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            Resources resources = getActivity().getResources();
            if (resources != null) {
                if (resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                    mColumnCount = 2;
                else
                    mColumnCount = 3;
            }

            mMoviesList = new ArrayList<>();
            mAdapter = new MyMovieRecyclerViewAdapter(mMoviesList, mListener);

            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            recyclerView.setAdapter(mAdapter);
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null)
            mLastUpdateOrder = savedInstanceState.getString(SAVE_LAST_ORDER);

        updateMovieList();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        // Cancel task when Fragment is destroyed.
        cancelDownload();
        super.onDestroy();
    }

    // Starts AsyncTask to fetch The Movie DB API
    public void updateMovieList() {
        cancelDownload();

        mTask = new FetchMovieApiTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOrder = prefs.getString(
                getString(R.string.pref_sort_order_key),
                getString(R.string.pref_popular_value)
        );
        mLastUpdateOrder = sortOrder;
        mTask.execute(sortOrder);
    }

    /**
     * Cancel (and interrupt if necessary) any ongoing DownloadTask execution.
     */
    public void cancelDownload() {
        if (mTask != null) {
            mTask.cancel(true);
            mTask = null;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Movie item);

        /**
         * Get the device's active network status in the form of a NetworkInfo object.
         */
        NetworkInfo getActiveNetworkInfo();
    }

    // AsyncTask to fetch The Movie DB data
    @SuppressLint("StaticFieldLeak")
    public class FetchMovieApiTask extends AsyncTask<String, Void, Movie[]> {

        private Movie[] getMoviesDataFromJson(String moviesJsonStr) throws JSONException {

            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray jsonMoviesArray = moviesJson.getJSONArray(JSON.RESULTS);

            Movie[] moviesArray = new Movie[jsonMoviesArray.length()];

            for (int i = 0; i < jsonMoviesArray.length(); i++) {
                String id = jsonMoviesArray.getJSONObject(i).getString(JSON.ID);
                String title = jsonMoviesArray.getJSONObject(i).getString(JSON.TITLE);
                String releaseDate = jsonMoviesArray.getJSONObject(i).getString(JSON.RELEASE_DATE);
                String voteAverage = jsonMoviesArray.getJSONObject(i).getString(JSON.VOTE_AVERAGE);
                String overview = jsonMoviesArray.getJSONObject(i).getString(JSON.OVERVIEW);
                Uri posterUri = createPosterUri(jsonMoviesArray.getJSONObject(i).getString(JSON.POSTER_PATH));

                moviesArray[i] = new Movie(id, title, releaseDate, voteAverage, overview,
                        posterUri);
            }
            return moviesArray;
        }

        // Creates Uri based on sort order
        private Uri createMoviesUri(String sortOrder) {
            Uri builtUri;
            StringBuilder url = new StringBuilder(API.BASE_URL);

            if (sortOrder.equals(getString(R.string.pref_popular_value))) {
                builtUri = parse(url.append(API.POPULAR_MOVIES_URL).toString());
            } else if (sortOrder.equals(getString(R.string.pref_top_rated_value))) {
                builtUri = parse(url.append(API.TOP_RATED_MOVIES_URL).toString());
            } else {
                builtUri = parse(url.append(API.POPULAR_MOVIES_URL).toString());

            }

            return builtUri.buildUpon()
                    .appendQueryParameter(API.KEY_PARAM, API.KEY)
                    .build();
        }

        // Method to create poster thumbnail Uri
        private Uri createPosterUri(String posterPath) {
            return parse(API.POSTER_MOVIES_BASE_URL).buildUpon()
                    .appendEncodedPath(API.POSTER_SIZE).appendEncodedPath(posterPath)
                    .build();
        }

        @Override
        protected void onPreExecute() {
            if (mListener != null) {
                NetworkInfo networkInfo = mListener.getActiveNetworkInfo();
                if (networkInfo == null || !networkInfo.isConnected() ||
                        (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                                && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
                    // If no connectivity, cancel task and update Callback with null data.
                    Toast.makeText(getActivity(),
                            R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                    cancel(true);
                }
            }
        }

        @Override
        protected Movie[] doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr;

            try {
                Uri moviesUri = createMoviesUri(params[0]);
                URL url = new URL(moviesUri.toString());

                // Create the request to The Movie DB, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder builder = new StringBuilder();
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
                    builder.append(line).append("\n");
                }

                if (builder.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = builder.toString();
            } catch (IOException e) {
                e.printStackTrace();
                // If the code 't successfully get the movies data, there's no point to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                return getMoviesDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the movies.
            return null;
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            if (movies != null) {
                mAdapter.clearRecyclerViewData();
                mMoviesList.addAll(Arrays.asList(movies));
                mAdapter.notifyItemRangeInserted(0, movies.length);
            }
        }
    }
}
