package me.deepanshusinha.popularmovies.utils;

public interface Constants {
    String SAVE_LAST_ORDER = "saveLastUpdateOrder";

    // THE MOVIE DB API
    interface API {
        String BASE_URL = "https://api.themoviedb.org/3/movie";
        String POPULAR_MOVIES_URL = "/popular?";
        String TOP_RATED_MOVIES_URL = "/top_rated?";
        String POSTER_MOVIES_BASE_URL = "http://image.tmdb.org/t/p/";
        String POSTER_SIZE = "w185/";
        String KEY_PARAM = "api_key";
        String KEY = "e1ca6f73f69d8963b45dc279c6d97305";
    }

    // THE MOVIE DB JSON
    interface JSON {
        String RESULTS = "results";
        String ID = "id";
        String TITLE = "title";
        String RELEASE_DATE = "release_date";
        String VOTE_AVERAGE = "vote_average";
        String OVERVIEW = "overview";
        String POSTER_PATH = "poster_path";
    }

    interface IntentKeys {
        String EXTRA_MOVIE = "extraMovie";
    }
}
