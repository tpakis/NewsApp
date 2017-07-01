package ai.thanasakis.uda.newsapp.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {
    public static final String LOG_TAG = MainActivity.class.getName();
    private static final int LOADER_ID = 1;
    LoaderManager loaderManager;
    private RecyclerView recyclerView;
    private ArrayList<News> newsList = new ArrayList<>();
    private String API_REQUEST_URL = "http://content.guardianapis.com/search?q=debate&tag=politics/politics&from-date=2014-01-01&api-key=test";
    private NewsAdapter newsAdapter;
    private SwipeRefreshLayout swipeContainer;

    public static List<News> cloneList(List<News> dogList) {
        List<News> clonedList = new ArrayList<News>(dogList.size());
        for (News dog : dogList) {
            clonedList.add(new News(dog));
        }
        return clonedList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            loaderManager = getLoaderManager();
            loaderManager.initLoader(LOADER_ID, null, MainActivity.this);
            recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
            swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    Loader<News> loader = loaderManager.getLoader(LOADER_ID);
                    if (loader != null) {
                        loaderManager.restartLoader(LOADER_ID, null, MainActivity.this);
                    } else {
                        loaderManager.initLoader(LOADER_ID, null, MainActivity.this);
                    }
                }
            });
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.swipe), Toast.LENGTH_SHORT).show();
            recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {

                @Override
                public void onClick(View view, int position) {
                    News itemArticle = newsList.get(position);
                    Uri uri = Uri.parse(itemArticle.getUrl());
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }

                // Have to implement this method
                @Override
                public void onLongClick(View view, int position) {
                    //
                }
            }));

        } else {
            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(MainActivity.this);
            dlgAlert.setMessage(getResources().getString(R.string.not_connected));
            dlgAlert.setTitle("No Internet");
            dlgAlert.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            dlgAlert.setCancelable(false);
            dlgAlert.create().show();
        }
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        return new NewsLoader(this, API_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);
        if (swipeContainer.isRefreshing()) {
            swipeContainer.setRefreshing(false);
        }
        boolean found = false;
        if (data != null) {
            if (!newsList.isEmpty()) {
                for (News nes1 : data) {
                    check2:
                    {
                        found = false;
                        for (News nes2 : newsList) {
                            if (nes1.getTitle().equals(nes2.getTitle())) {
                                found = true;
                                break check2;
                            }
                        }
                    }
                }
            }
            if (!found) {
                newsList.clear();
                for (News dog : data) {
                    newsList.add(new News(dog));
                }
                newsAdapter = new NewsAdapter(newsList);
                recyclerView.setAdapter(newsAdapter);

            } else
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_new), Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.not_found), Toast.LENGTH_SHORT).show();
        loaderManager.destroyLoader(LOADER_ID);
    }

    //Have to implement this method
    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        //
    }

}
