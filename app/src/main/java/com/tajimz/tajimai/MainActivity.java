package com.tajimz.tajimai;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
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
import com.tajimz.tajimai.aiclone.AiCloneIntroActivity;
import com.tajimz.tajimai.database.SqliteDB;
import com.tajimz.tajimai.databinding.ActivityMainBinding;
import com.tajimz.tajimai.databinding.AlertUpdateApiBinding;
import com.tajimz.tajimai.models.ChatModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    List<ChatModel> list = new ArrayList<>();
    ActivityMainBinding binding;
    RecyclerAdapter recyclerAdapter;
    RequestQueue requestQueue ;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    TextToSpeech tts;
    SqliteDB sqliteDB ;

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
        seedDefaultPersonality();



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
        sqliteDB = new SqliteDB(MainActivity.this);

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
            String text = binding.edQuery.getText().toString().trim();
            if (text.isEmpty() ) return;
            binding.edQuery.setText("");
            handleQuerySubmit(text);





        });

        binding.imgThreeDots.setOnClickListener(this::showPopUpMenu);

        binding.imgNav.setOnClickListener(v->{
            Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show();
        });
    }

    private void showPopUpMenu(View v){
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
            }else if (id == R.id.update_personality){
//                startActivity(new Intent(this, AiCloneIntroActivity.class));
                importPersonality();

                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    private static final String TAG = "JSON";

    // 1. Register the Activity Result launcher
    private final ActivityResultLauncher<Intent> pickJsonLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        try (InputStream is = getContentResolver().openInputStream(uri)) {
                            if (is != null) {
                                String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                                JSONObject obj = new JSONObject(json);
                                Log.d("jsonout", obj.toString());
                                insertPersonalityInDB(obj);
                                // handle your imported personality here
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Failed to read JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    // 2. Launch the picker
    private void pickJsonFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");
        pickJsonLauncher.launch(intent);
    }

    // 3. Call this when you want to import
    private void importPersonality() {
        pickJsonFile();
    }
    private void insertPersonalityInDB(JSONObject jsonObject){
        try {
            long person_id = sqliteDB.addPersonality(jsonObject.getString("personality"));
            JSONArray jsonArray = jsonObject.getJSONArray("categories");
            for (int i = 0 ; i < jsonArray.length(); i ++){
                JSONObject obj = jsonArray.getJSONObject(i);
                String categoryName = obj.getString("name");
                long cat_id= sqliteDB.getCategoryId(categoryName);
                JSONArray jsonArray1 = obj.getJSONArray("descriptions");
                for (int j = 0; j < jsonArray1.length(); j++){
                    JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                    sqliteDB.addDescription(jsonObject1.getString("key"), jsonObject1.getString("alias"), cat_id, person_id, jsonObject1.getString("value"));

                }

            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }




    private void handleQuerySubmit(String query){
        addInRecycler(null, query, false);
        binding.loadingBar.setVisibility(VISIBLE);
        disableButton();
        sendToAi(makeJsonObject(query, "Tajim", null), new VolleyListener() {
            @Override
            public void onSuccess(JSONObject result) {
                Log.d("volley",result.toString());
                JSONObject jsonObject = getJsonObjectFromString(getOutputFromServerJson(result));

                boolean needsPersona = jsonObject.optBoolean("needsPersona");

                String answerOrCategory = jsonObject.optString("answerOrCategory");
                if (needsPersona){

                    String name = sqliteDB.getDescriptionByPath(getSelectedPerson()+answerOrCategory);
                    Log.d("volley_cat", answerOrCategory);
                    if (name.equals("no_info")){
                        JSONArray jsonArray = sqliteDB.getAllDescriptionByPathAsJson(getSelectedPerson()+answerOrCategory);
                        handleAiResponse(query, jsonArray);

                    }else{
                        addInRecycler(name , query, true);
                    }


                }else {
                    addInRecycler( answerOrCategory, query, true);

                }


            }

            @Override
            public void onFailed(VolleyError error) {
                Log.e("volley",error.toString());
                apiRequestFailed(error);
            }
        });
    }
    private void handleAiResponse(String query, JSONArray jsonArray){

        sendToAi(makeJsonObject(query, "name", jsonArray), new VolleyListener() {
            @Override
            public void onSuccess(JSONObject result) {
                Log.d("volley2",result.toString());
                String answer = getOutputFromServerJson(result);
                addInRecycler(answer , query, true);
            }

            @Override
            public void onFailed(VolleyError error) {
                apiRequestFailed(error);

            }
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


    private JSONObject makeJsonObject(String query, String name, @Nullable JSONArray jsonArray) {
        JSONObject toSend = new JSONObject();
        try {
            // Build messages array
            JSONArray messages = new JSONArray();

            // Use different system prompt depending on whether jsonArray is provided
            if (jsonArray != null) {
                messages.put(new JSONObject()
                        .put("role", "system")
                        .put("content", SECRETS.getPrompt2(name, jsonArray)));
            } else {
                messages.put(new JSONObject()
                        .put("role", "system")
                        .put("content", SECRETS.getPrompt(name)));
            }

            // Add user message
            messages.put(new JSONObject()
                    .put("role", "user")
                    .put("content", query));

            // Build final JSON
            toSend.put("model", "openai/gpt-oss-20b");
            toSend.put("messages", messages);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return toSend;
    }

    private void seedDefaultPersonality(){
        if (sharedPreferences.getString("first",null) == null){
            Log.d("app_info","data_seeded");
            sqliteDB.seedDefaultPersonality();
            editor.putString("first","no");
            editor.apply();
        }
    }

    private String getSelectedPerson(){
        String toReturn = sharedPreferences.getString("selected","1")+".";
        Log.d("app_info",toReturn);
        return toReturn;
    }







    @Override
    protected void onDestroy() {
        if (tts != null) tts.shutdown();
        super.onDestroy();
    }
}