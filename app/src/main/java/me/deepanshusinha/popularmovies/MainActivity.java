package me.deepanshusinha.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import me.deepanshusinha.popularmovies.fragment.MovieFragment;
import me.deepanshusinha.popularmovies.model.Movie;
import me.deepanshusinha.popularmovies.utils.Constants;

public class MainActivity extends AppCompatActivity implements MovieFragment.OnListFragmentInteractionListener {

    MovieFragment mFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        mFragment = MovieFragment.newInstance();
        fragmentTransaction.replace(R.id.movieFl, mFragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = prefs.edit();
        int id = item.getItemId();

        switch (id) {
            case R.id.top_rated:
                edit.putString(getString(R.string.pref_sort_order_key),
                        getString(R.string.pref_top_rated_value));
                edit.apply();
                mFragment.updateMovieList();
                return true;
            case R.id.popular:
                edit.putString(getString(R.string.pref_sort_order_key),
                        getString(R.string.pref_popular_value));
                edit.apply();
                mFragment.updateMovieList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return networkInfo;
    }

    @Override
    public void onListFragmentInteraction(Movie item) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(Constants.IntentKeys.EXTRA_MOVIE, item);
        startActivity(intent);
    }
}
