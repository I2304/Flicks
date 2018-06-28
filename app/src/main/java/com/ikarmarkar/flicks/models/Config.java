package com.ikarmarkar.flicks.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Config {
    // the base url for loading images
    String imageBaseUrl;
    // the poster size to use when fetching images, part of the URL;
    String posterSize;
    // the backdrop size to use when fetching images
    String backdropSize;

    public Config(JSONObject object) throws JSONException
    {
        JSONObject images = object.getJSONObject("images");
        imageBaseUrl = images.getString("secure_base_url");
        // get the poster size
        JSONArray posterSizeOptions = images.getJSONArray("poster_sizes");
        posterSize = posterSizeOptions.optString(3, "w342");
        // parse the backdrop sizes and use the option at index 1 or w780 as a fallback
        JSONArray backdropsizeOptions = images.getJSONArray("backdrop_sizes");
        backdropSize = backdropsizeOptions.optString(1, "w780");
    }

    public String getBackdropSize() {
        return backdropSize;
    }

    public String getImageUrl(String size, String path){
        // concatenate all three
        return String.format("%s%s%s", imageBaseUrl, size, path);
    }

    public String getImageBaseUrl() {
        return imageBaseUrl;
    }

    public String getPosterSize() {
        return posterSize;
    }
}
