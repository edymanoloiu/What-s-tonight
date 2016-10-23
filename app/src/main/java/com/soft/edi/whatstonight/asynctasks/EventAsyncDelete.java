package com.soft.edi.whatstonight.asynctasks;

import android.os.AsyncTask;

import com.example.edi.myapplication.backend.eventApi.EventApi;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

/**
 * Created by Edi on 24.09.2016.
 */

public class EventAsyncDelete extends AsyncTask<Void, Void, Boolean> {
    private static EventApi myApiService = null;
    private long ID;

    public EventAsyncDelete(long id) {
        ID = id;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (myApiService == null) { // Only do this once
            EventApi.Builder builder = new EventApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://whats-tonight.appspot.com/_ah/api/");

            myApiService = builder.build();
        }

        try {
            myApiService.remove(ID).execute();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
