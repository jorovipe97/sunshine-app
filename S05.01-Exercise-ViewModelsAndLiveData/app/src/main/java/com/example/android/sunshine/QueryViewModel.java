package com.example.android.sunshine;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.sunshine.utilities.NetworkUtils;
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils;

import java.net.URL;

// Loader are deprecated since api18
// https://stackoverflow.com/questions/51408098/what-is-the-appropriate-replacement-of-deprecated-getsupportloadermanager
// Basic introduction to view models
// https://medium.com/androiddevelopers/lifecycle-aware-data-loading-with-android-architecture-components-f95484159de4
public class QueryViewModel extends AndroidViewModel {
    private final QueryLiveData queryLiveData;

    public QueryViewModel(@NonNull Application application, String location) {
        super(application);
        queryLiveData = new QueryLiveData(application, location);
    }

    /**
     * Returns an observable
     * @return
     */
    public LiveData<String[]> getData() {
        return queryLiveData;
    }

    // Se how to pass custom arguments to a view model
    // https://medium.com/@iambryanwho/passing-custom-arguments-to-a-viewmodel-livedata-a9fb5f9af2e5
    public static class QueryViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {
        private final String location;
        private final Application application;

        /**
         * Creates a {@code AndroidViewModelFactory}
         *
         * @param application an application to pass in {@link AndroidViewModel}
         */
        public QueryViewModelFactory(@NonNull Application application, String location) {
            super(application);
            this.application = application;
            this.location = location;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new QueryViewModel(this.application, this.location);
        }
    }
}

class QueryLiveData extends LiveData<String[]> {
    private static final String TAG = QueryLiveData.class.getSimpleName();
    private final Context context;
    private final String location;
    public QueryLiveData(Context context, String location) {
        this.context = context;
        this.location = location;
        loadData();
    }

    void loadData() {
        // TODO: use room for observe for changes on the api request
        new AsyncTask<String, Void, String[]>() {
            // TODO: See this to get an idea of how to emit intermediate events
            // https://stackoverflow.com/questions/51757164/how-to-create-livedata-which-emits-a-single-event-and-notifies-only-last-subscri
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String[] strings) {
                super.onPostExecute(strings);
                setValue(strings);
            }

            @Override
            protected String[] doInBackground(String... locations) {
                /* If there's no zip code, there's nothing to look up. */
                String location = locations[0];
                // String location = bundle.getString(EXTRA_LOCATION);
                if (location == null || location.equals("")) return null;

                URL weatherRequestUrl = NetworkUtils.buildUrl(location);

                try {
                    String jsonWeatherResponse = NetworkUtils
                            .getResponseFromHttpUrl(weatherRequestUrl);

                    String[] simpleJsonWeatherData = OpenWeatherJsonUtils
                            .getSimpleWeatherStringsFromJson(context, jsonWeatherResponse);

                    return simpleJsonWeatherData;

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }.execute(this.location);
    }

    @Override
    protected void onActive() {
        Log.d(TAG, "onActive()");
    }

    @Override
    protected void onInactive() {
        Log.d(TAG, "onInactive()");
    }
}
