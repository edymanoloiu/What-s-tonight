package com.soft.edi.whatstonight;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.edi.myapplication.backend.eventApi.model.Event;
import com.google.gson.Gson;
import com.soft.edi.whatstonight.asynctasks.EventAsyncGetter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        final ImageView iv = (ImageView) findViewById(R.id.imageView2);
        final Animation an = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate);
        final Animation an2 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.abc_fade_out);

        iv.startAnimation(an);


        an.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                SharedPreferences settings = getSharedPreferences("whatstonight", 0);
                Map<String, ?> filterOptions = settings.getAll();
                List<Event> events = null;
                String category = filterOptions.containsKey("category") ? (String) filterOptions.get("category") : null;
                String city = filterOptions.containsKey("city") ? (String) filterOptions.get("city") : null;
                String owner = filterOptions.containsKey("evOwner") ? (String) filterOptions.get("evOwner") : null;
                try {
                    events = new EventAsyncGetter(category, city, owner).execute().get();
                } catch (InterruptedException e) {
                    Toast.makeText(getBaseContext(), "Unable to get list of events", Toast.LENGTH_LONG);
                    return;
                } catch (ExecutionException e) {
                    Toast.makeText(getBaseContext(), "Unable to get list of events", Toast.LENGTH_LONG);
                    return;
                }
                String listSerializedToJson = new Gson().toJson(events);
                iv.startAnimation(an2);
                finish();
                Intent i = new Intent(getBaseContext(), MainActivity.class);
                i.putExtra("list", listSerializedToJson);
                startActivity(i);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
