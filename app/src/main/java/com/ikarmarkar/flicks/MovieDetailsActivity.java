package com.ikarmarkar.flicks;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ikarmarkar.flicks.models.Movie;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

public class MovieDetailsActivity extends AppCompatActivity {

    // the movie to display
    Movie movie;

    // the view objects
    TextView tvTitle;
    TextView tvOverview;
    RatingBar rbVoteAverage;
    RatingBar ratingBar;

    public final static String API_BASE_URl = "https://api.themoviedb.org/3";
    public final static String API_KEY_PARAM = "api_key";
    public final static String TAG = "MovieTrailerActivity";

    String key;
    String url;

    AsyncHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        client = new AsyncHttpClient();
        // resolve the view objects
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvOverview = (TextView) findViewById(R.id.tvOverview);
        rbVoteAverage = (RatingBar) findViewById(R.id.rbVoteAverage);

        // unwrap the movie passed in via intent, using its simple name as a key
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        Log.d("MovieDetailsActivity", String.format("Showing details for '%s'", movie.getTitle()));

        // set the title and overview
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());

        // vote average is 0..10, convert to 0..5 by dividing by 2
        float voteAverage = (float) movie.getVoteAverage();
        rbVoteAverage.setRating(voteAverage = voteAverage > 0 ? voteAverage / 2.0f : voteAverage);
        ratingBar = (RatingBar) findViewById(R.id.rbVoteAverage);
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);

        getTrailer();
    }

    public void getTrailer() {
        url = API_BASE_URl + "/movie/" + movie.getId() + "/videos";
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key));
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[]headers, JSONObject response) {
                try {
                    JSONArray results = response.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++)
                    {
                        JSONObject jsonObject = results.getJSONObject(i);
                        key = jsonObject.getString("key");
                    }
                    Log.i(TAG, String.format("Loaded %s movies", results.length()));
                }
                catch (JSONException e){
                    Log.e(TAG, "Failed to parse trailer activity");
                }
            }
        });
    }

    public void onTrailerClick(View v)
    {
        Intent intent = new Intent(this, MovieTrailerActivity.class);
        intent.putExtra("Movie Key", key);
        startActivity(intent);
    }
}
