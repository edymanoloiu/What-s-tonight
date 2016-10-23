package com.soft.edi.whatstonight.asynctasks;

import android.os.AsyncTask;

import com.example.edi.myapplication.backend.eventApi.EventApi;
import com.example.edi.myapplication.backend.eventApi.model.Event;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

/**
 * Created by Edi on 18.09.2016.
 */
public class EventAsyncSetter extends AsyncTask<Void, Void, Boolean> {
    private static EventApi myApiService = null;
    private Event event = null;

    public EventAsyncSetter(Event event) {
        this.event = event;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (myApiService == null) { // Only do this once
            EventApi.Builder builder = new EventApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://whats-tonight.appspot.com/_ah/api/");

            myApiService = builder.build();
        }

        try {
            myApiService.insert(event).execute();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
