package com.bigooit.marketing.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bigooit.marketing.Adapter.RecyclerView.FbPagesAdapter;
import com.bigooit.marketing.DataModels.FbPage;
import com.bigooit.marketing.databinding.ActivityMainBinding;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements FbPagesAdapter.FbPageHandler {

    private AccessToken accessToken;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        binding.loginButton.setPermissions(
                "email",
                "public_profile",
                "ads_management",
                "instagram_basic",
                "pages_show_list",
                "business_management",
                "pages_read_engagement",
                "instagram_content_publish",
                "instagram_manage_comments",
                "instagram_manage_insights");

        binding.loginButton.registerCallback(CallbackManager.Factory.create(),
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // User logged in using facebook account
                        // Now just save or update the user info to firebase firestore
                        saveFbUserInfoToFireStore(loginResult.toString());

                        loadFbPages();
                    }
                    @Override
                    public void onCancel() {Snackbar.make(binding.loginButton, "Login cancelled", Snackbar.LENGTH_LONG).show();}
                    @Override
                    public void onError(@NonNull FacebookException exception) {Snackbar.make(binding.loginButton, "Error on login", Snackbar.LENGTH_LONG).show();}
                });

        accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        if(isLoggedIn){loadFbPages();}

        binding.fab.setOnClickListener(view -> {
            if(isLoggedIn){
                Snackbar.make(view, "You have logged in", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }else{
                Snackbar.make(view, "Please login first", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void saveFbUserInfoToFireStore(String loginResult) {
        GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                (jsonObject, graphResponse) -> {


                    try {
                        Map<String,String> user = new HashMap<>();
                        user.put("name",jsonObject.getString("name"));
                        user.put("id",jsonObject.getString("id"));

                        FirebaseFirestore.getInstance()
                                .collection("dummyUser")
                                .document(user.get("id"))
                                .set(user)
                                .addOnCompleteListener(task -> {
                                    Log.d("Added User",task.toString());
                                    Toast.makeText(this, "Completed the task", Toast.LENGTH_SHORT).show();
                                });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
        ).executeAsync();

    }

    private void loadFbPages() {
        // get the user's pages
        GraphRequest request = GraphRequest.newGraphPathRequest(
                accessToken,
                "/me/accounts",
                response -> {
                    ArrayList<FbPage> fbPageArrayList = new ArrayList<>();

                    try {
                        JSONArray fbPages = Objects.requireNonNull(response.getJsonObject()).getJSONArray("data");
                        for (int i = 0; i < fbPages.length(); i++)
                            fbPageArrayList.add(new FbPage(fbPages.getJSONObject(i)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    binding.rcFbPages.setLayoutManager(new LinearLayoutManager(this));
                    binding.rcFbPages.setAdapter(new FbPagesAdapter(fbPageArrayList,this));
                    binding.rcFbPages.setHasFixedSize(true);

                    Snackbar.make(binding.fab, "Pages List loaded ", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                });

        request.executeAsync();

    }

    @Override
    public void onClickFbPage(FbPage page) {
        Intent fbPageDetailsIntent = new Intent(this,FbPageDetailsActivity.class);
        fbPageDetailsIntent.putExtra("page",page);
        this.startActivity(fbPageDetailsIntent);
    }
}