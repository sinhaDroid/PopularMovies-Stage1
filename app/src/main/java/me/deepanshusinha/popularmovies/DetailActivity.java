package me.deepanshusinha.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import me.deepanshusinha.popularmovies.model.Movie;
import me.deepanshusinha.popularmovies.utils.Constants;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ImageView fullPosterIv = findViewById(R.id.fullPosterIv);
        TextView title = findViewById(R.id.titleTv);
        TextView releaseDate = findViewById(R.id.releaseDateTv);
        TextView userRating = findViewById(R.id.userRatingTv);
        TextView content = findViewById(R.id.contentTv);

        Movie movie = getIntent().getParcelableExtra(Constants.IntentKeys.EXTRA_MOVIE);

        if (movie != null) {
            Glide.with(this)
                    .load(movie.getPosterUri())
                    .into(fullPosterIv);

            title.setText(movie.getTitle());
            releaseDate.setText(movie.getReleaseDate());
            userRating.setText(movie.getVoteAverage());
            content.setText(movie.getOverview());
        }
    }
}
