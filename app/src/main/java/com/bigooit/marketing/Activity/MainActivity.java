package com.bigooit.marketing.Activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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
import com.facebook.login.LoginResult;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

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
                    public void onSuccess(LoginResult loginResult) {loadFbPages();}
                    @Override
                    public void onCancel() {Snackbar.make(binding.loginButton, "Login cancelled", Snackbar.LENGTH_LONG).show();}
                    @Override
                    public void onError(FacebookException exception) {Snackbar.make(binding.loginButton, "Error on login", Snackbar.LENGTH_LONG).show();}
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

    private void loadFbPages() {
        // get the user's pages
        GraphRequest request = GraphRequest.newGraphPathRequest(
                accessToken,
                "/me/accounts",
                response -> {
                    ArrayList<FbPage> fbPageArrayList = new ArrayList<>();

                    try {
                        JSONArray fbPages = response.getJsonObject().getJSONArray("data");
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

        //"id": "17914372193136603"
    }

    @Override
    public void onClickFbPage(FbPage page) {
        Toast.makeText(this, page.getName(), Toast.LENGTH_SHORT).show();
    }
}