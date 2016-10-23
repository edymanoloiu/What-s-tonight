package com.soft.edi.whatstonight.asynctasks;

import android.os.AsyncTask;

import com.example.edi.myapplication.backend.cityApi.CityApi;
import com.example.edi.myapplication.backend.cityApi.model.City;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Created by Edi on 25.09.2016.
 */

public class CityAsyncGetter extends AsyncTask<Void, Void, List<City>> {
    private static CityApi myApiService = null;

    public CityAsyncGetter() {
    }

    @Override
    protected List<City> doInBackground(Void... params) {
        if (myApiService == null) { // Only do this once
            CityApi.Builder builder = new CityApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://whats-tonight.appspot.com/_ah/api/");

            myApiService = builder.build();
        }

        try {
            return myApiService.list().execute().getItems();
        } catch (IOException e) {
            return Collections.EMPTY_LIST;
        }
    }
}
