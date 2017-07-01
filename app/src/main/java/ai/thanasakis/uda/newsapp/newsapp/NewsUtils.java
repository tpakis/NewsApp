package ai.thanasakis.uda.newsapp.newsapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import static ai.thanasakis.uda.newsapp.newsapp.MainActivity.LOG_TAG;

/**
 * Created by programbench on 6/27/2017.
 */

public class NewsUtils {
    public static final int READ_TIME_OUT = 10000;
    public static final int CONNECT_TIME_OUT = 15000;
    public static final int SUCCESS_CODE = 200;

    //An empty private constructor makes sure that the class is not going to be initialised.
    private NewsUtils() {
    }

    public static ArrayList<News> fetchData(String requrl) {
        URL url = createURL(requrl);
        String jsonResponse = null;

        try {
            jsonResponse = makeHTTPRequest(url);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing connection", e);
        }

        ArrayList<News> booksFoundList = extractList(jsonResponse);

        return booksFoundList;

    }

    private static String makeHTTPRequest(URL url) throws IOException {

        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(READ_TIME_OUT /* milliseconds */);
            urlConnection.setConnectTimeout(CONNECT_TIME_OUT /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == SUCCESS_CODE) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;

    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    private static URL createURL(String requrl) {

        URL url = null;

        try {
            url = new URL(requrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error creating url", e);
        }
        return url;
    }

    public static ArrayList<News> extractList(String json) {

        ArrayList<News> newsFoundList = new ArrayList<>();

        try {
            JSONObject list = new JSONObject(json);
            JSONObject response = list.getJSONObject("response");
            JSONArray newsArray = null;
            if (!response.isNull("results")) {
                newsArray = response.getJSONArray("results");

                for (int i = 0; i < newsArray.length(); i++) {
                    JSONObject article = newsArray.getJSONObject(i);
                    String mWebtitle = "Title Empty";
                    String mSectionName = "Section Empty";
                    String mPublicationDate = "Empty Date";
                    String mWebUrl = "-";

                    if (article.has("webTitle"))
                        mWebtitle = article.getString("webTitle");
                    if (article.has("sectionName"))
                        mSectionName = article.getString("sectionName");
                    if (article.has("webPublicationDate"))
                        mPublicationDate = article.getString("webPublicationDate");
                    if (article.has("webUrl"))
                        mWebUrl = article.getString("webUrl");

                    News articleToAdd = new News(mWebtitle, mSectionName, mPublicationDate, mWebUrl);
                    newsFoundList.add(articleToAdd);
                }
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem parsing JSON results", e);
        }
        return newsFoundList;
    }


}
