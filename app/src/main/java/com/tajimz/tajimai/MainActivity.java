package com.tajimz.tajimai;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.PopupMenu;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.tajimz.tajimai.adapters.RecyclerAdapter;
import com.tajimz.tajimai.databinding.ActivityMainBinding;
import com.tajimz.tajimai.databinding.AlertUpdateApiBinding;
import com.tajimz.tajimai.models.ChatModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    List<ChatModel> list = new ArrayList<>();
    ActivityMainBinding binding;
    String history = "";
    RecyclerAdapter recyclerAdapter;
    RequestQueue requestQueue ;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    TextToSpeech tts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupWindow();
        //SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

//        splashScreen.setKeepOnScreenCondition(() -> {
//            // Return true if you want to keep showing splash
//            return true; // change to true to delay
//        });
        sharedPreferences = getSharedPreferences("api_info", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        setupTextToSpeech();
        initAdapter();
        initButtonListeners();
        requestQueue = Volley.newRequestQueue(this);
        showWelcomeContent();






    }


    private void setupWindow() {
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        // Make status bar icons dark
        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(true);

        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            // System bars insets (status bar + nav bar)
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            // IME insets (keyboard)
            Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime());

            // Apply padding: top = status bar, bottom = keyboard + navigation
            v.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    Math.max(systemBars.bottom, imeInsets.bottom)
            );

            return insets;
        });
    }

    private void setupTextToSpeech(){

        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);
                tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override public void onStart(String utteranceId) {}
                    @Override public void onDone(String utteranceId) {
                        new Handler(Looper.getMainLooper()).post(() -> recyclerAdapter.speechEnabled = false);
                    }
                    @Override public void onError(String utteranceId) {
                        new Handler(Looper.getMainLooper()).post(() -> recyclerAdapter.speechEnabled = false);
                    }
                });
            }
        });

    }

    private void initAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        binding.recyclerView.setLayoutManager(layoutManager);
        recyclerAdapter = new RecyclerAdapter(this, list, binding.recyclerView, tts);
        binding.recyclerView.setAdapter(recyclerAdapter);
    }

    private void addInRecycler (String ai, String user, Boolean isAi){

        if (binding.welcomeContent.getVisibility()==VISIBLE) {

            hideWelcomeContent();
        }
        list.add(new ChatModel(ai, user, isAi, true));
        recyclerAdapter.notifyItemInserted(list.size() -1);
        binding.recyclerView.scrollToPosition(list.size()-1);
        history += "\n"+user;
        binding.loadingBar.setVisibility(GONE);
    }

    public interface VolleyListener{
        void onSuccess(JSONObject result);
        void onFailed(VolleyError error);
    }
    public void sendToAi(JSONObject jsonObject, VolleyListener volleyListener) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "https://api.groq.com/openai/v1/chat/completions", jsonObject,
                volleyListener::onSuccess, volleyListener::onFailed){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers =new HashMap<>();
                headers.put("Authorization", "Bearer "+getApi());
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);



    }


    private void initButtonListeners(){
        binding.btnLayout.setOnClickListener(v->{
            String text = binding.edQuery.getText().toString();
            if (text.isEmpty() ) return;
            addInRecycler(null, text, false);
            binding.edQuery.setText("");
            //building json

            //asking to ai the keyword -----------
            binding.loadingBar.setVisibility(VISIBLE);
            disableButton();

            sendToAi(makeJsonObject(text, true, null, null), new VolleyListener() {
                @Override
                public void onSuccess(JSONObject result) {
                    Log.d("volley", result.toString());
                    JSONObject jsonObject = getJsonObjectFromString(getOutputFromServerJson(result));

                    Boolean needsPersona = jsonObject.optBoolean("needsPersona");
                    String fieldOrAnswer = jsonObject.optString("fieldOrAnswer");


                    if (!needsPersona){
                        addInRecycler(fieldOrAnswer, text, true);
                    }else {
                        String question = jsonObject.optString("question");
                        sendToAi(makeJsonObject(text, false, fieldOrAnswer, question), new VolleyListener() {
                            @Override
                            public void onSuccess(JSONObject result) {
                                Log.d("volley",result.toString());

                                String output = getOutputFromServerJson(result);
                                addInRecycler(output, text, true);


                            }

                            @Override
                            public void onFailed(VolleyError error) {
                                apiRequestFailed(error);
                            }
                        });
                    }

                }

                @Override
                public void onFailed(VolleyError error) {
                    Log.e("volley",error.toString());
                    apiRequestFailed(error);
                }
            });






        });

        binding.imgThreeDots.setOnClickListener(v->{
            PopupMenu popupMenu = new PopupMenu(this, v);
            popupMenu.getMenuInflater().inflate(R.menu.pop_up_menu_main, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(menuItem->{

                int id = menuItem.getItemId();
                if (id == R.id.update_api){

                    showUpdateApiAlert();

                    return true;
                }else if (id == R.id.about_us){
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://tajimz.github.io")));

                    return true;
                }else if (id == R.id.privacy_policy){
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/tajimz/tajim-ai")));

                    return true;
                }
                return false;
            });
            popupMenu.show();
        });
    }

    private void apiRequestFailed(VolleyError error){
        binding.loadingBar.setVisibility(GONE);
        enableButton();
        Log.e("volley",error.toString());
        Toast.makeText(MainActivity.this, "Error "+error.networkResponse.statusCode+" :"+error.toString(), Toast.LENGTH_SHORT).show();
        if (!list.isEmpty()) {
            int lastIndex = list.size() - 1;
            list.remove(lastIndex);
            recyclerAdapter.notifyItemRemoved(lastIndex);
        }



    }

    private String getOutputFromServerJson(JSONObject jsonObject) {
        return jsonObject
                .optJSONArray("choices")
                .optJSONObject(0)
                .optJSONObject("message")
                .optString("content", "");
    }

    private JSONObject makeJsonObject(String query, Boolean system, String keyword,String question) {
        String system_content = "";
        String modelToUse = "";
        if (system){
            system_content = SECRETS.small_llm_prompt + history;
            modelToUse = "llama-3.1-8b-instant";


        }else {
            modelToUse = "openai/gpt-oss-20b";
            system_content =  "You are a virtual clone of tajim. user want to know about you, his question : "+question+" info you have: "+DemoInfo.getInfo(keyword)+". Just reply only the answer, nothing else. Reply I don't know if you don't have the answer" ;
        }
        JSONObject toSend = new JSONObject();
        try {
            JSONArray messages = new JSONArray()
                    .put(new JSONObject()
                            .put("role", "system")
                            .put("content",system_content))
                    .put(new JSONObject()
                            .put("role", "user")
                            .put("content", query));

            toSend.put("model", modelToUse);
            toSend.put("messages", messages);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return toSend;
    }


    private void showUpdateApiAlert(){
        AlertUpdateApiBinding apiBinding = AlertUpdateApiBinding.inflate(getLayoutInflater());
        AlertDialog alertDialog = new AlertDialog.Builder(this).setView(apiBinding.getRoot()).setCancelable(true).create();
        alertDialog.show();

        apiBinding.btnUpdate.setOnClickListener(v->{
            String text = apiBinding.edApi.getText().toString().trim();
            if (text.isEmpty()) return;
            updateApi(text);
            alertDialog.dismiss();

        });

        apiBinding.layoutRedirect.setOnClickListener(v->{
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://console.groq.com/keys")));

        });
    }
    private void updateApi(String api){
        editor.putString("api",api);
        editor.apply();
        Toast.makeText(this, "Api Updated", Toast.LENGTH_SHORT).show();
    }
    private String getApi(){
        return sharedPreferences.getString("api", SECRETS.myApi);
    }

    private void disableButton(){
        binding.imageSubmit.setImageResource(R.drawable.stop);
        binding.btnLayout.setEnabled(false);
    }
    public void enableButton(){
        binding.imageSubmit.setImageResource(R.drawable.arrow_up);
        binding.btnLayout.setEnabled(true);

    }

    private void hideWelcomeContent() {

        binding.welcomeContent.animate()
                .alpha(0f)
                .translationY(-120f)
                .scaleX(0.85f)
                .scaleY(0.85f)
                .setDuration(600)
                .setInterpolator(new AccelerateInterpolator())
                .withEndAction(() -> {
                    binding.welcomeContent.setVisibility(View.GONE);


                })
                .start();
    }

    private void showWelcomeContent() {
        binding.welcomeContent.setVisibility(GONE);

        binding.welcomeContent.postDelayed(() -> {

            binding.welcomeContent.setVisibility(VISIBLE);

            // Initial state
            binding.welcomeContent.setAlpha(0f);
            binding.welcomeContent.setTranslationY(120f);
            binding.welcomeContent.setScaleX(0.85f);
            binding.welcomeContent.setScaleY(0.85f);

            binding.welcomeContent.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(700)
                    .setInterpolator(new OvershootInterpolator(1.2f))
                    .start();

        }, 600);
    }

    private JSONObject getJsonObjectFromString(String obj){
        try {
            return new JSONObject(obj);
        } catch (JSONException e) {
            return new JSONObject();
        }
    }






    @Override
    protected void onDestroy() {
        if (tts != null) tts.shutdown();
        super.onDestroy();
    }
}