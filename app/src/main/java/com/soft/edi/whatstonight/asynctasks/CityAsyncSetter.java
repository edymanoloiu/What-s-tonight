package com.soft.edi.whatstonight.asynctasks;

import android.os.AsyncTask;

import com.example.edi.myapplication.backend.cityApi.CityApi;
import com.example.edi.myapplication.backend.cityApi.model.City;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;


public class CityAsyncSetter extends AsyncTask<Void, Void, Boolean> {
    private static CityApi myApiService = null;
    private City city;

    public CityAsyncSetter(City city) {
        this.city = city;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (myApiService == null) { // Only do this once
            CityApi.Builder builder = new CityApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://whats-tonight.appspot.com/_ah/api/");

            myApiService = builder.build();
        }

        try {
            myApiService.insert(city).execute();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
