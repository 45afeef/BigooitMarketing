package com.bigooit.marketing.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bigooit.marketing.DataModels.FbPage;
import com.bigooit.marketing.R;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FbPageDetailsActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 101;

    private AccessToken accessToken;
    private StorageReference storage;
    private FirebaseFirestore db;


    private DocumentReference pageDbRef;
    private String userId;
    private String userDocRef;
    private FbPage page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fb_page_details);

        accessToken = AccessToken.getCurrentAccessToken();
        storage = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();
        userId = accessToken.getUserId();
        userDocRef = "dummyUser/"+accessToken.getUserId();
        page = getIntent().getParcelableExtra("page");
        pageDbRef = db.document(userDocRef).collection("FbPage").document(page.getId());

        setTitle(page.getName());

        TextView fbNameView = findViewById(R.id.fb_page_name);
        fbNameView.setText("Id : "+page.getId()+" Category is "+page.getCategory());

        FloatingActionButton fabPost = findViewById(R.id.fab_post);
        fabPost.setOnClickListener(v -> chooseImage());

        /**
         * Facebook GraphRequests starts from here
         */
        // get the page's instagram business account
        GraphRequest request = GraphRequest.newGraphPathRequest(
                AccessToken.getCurrentAccessToken(),
                "/"+page.getId(),
                response -> {
                    try {
                        page.setInstagramBusinessAccountId(Objects.requireNonNull(response.getJsonObject()).getJSONObject("instagram_business_account").getLong("id"));
                        Toast.makeText(this, "Instagram Business Account Id is "+page.getInstagramBusinessAccountId(), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "instagram_business_account");
        request.setParameters(parameters);
        request.executeAsync();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            uploadToFirebase(imageUri);
        }
    }

    public void createIgContainer(AccessToken accessToken,String igBusinessId,String imageUrl,String caption){

        Map<String,String> postObject = new HashMap<>();

        postObject.put("image_url",imageUrl);
        postObject.put("caption",caption);

        GraphRequest request = GraphRequest.newPostRequest(
                accessToken,
                "/"+igBusinessId+"/media",
                new JSONObject(postObject),
                response -> {
                    // Insert your code here
                });

        request.executeAsync();
    }

    public void publishTheContainer(AccessToken accessToken,String creationId){
        try {
            GraphRequest request = GraphRequest.newPostRequest(
                    accessToken,
                    "/17841451338977754/media_publish",
                    new JSONObject("{\"creation_id\":\""+creationId+"\"}"),
                    response -> {
                        // Insert your code here
                        Toast.makeText(this,"Yeah we have posted it"+response.toString(),Toast.LENGTH_LONG).show();
                    });
            request.executeAsync();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void chooseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    public void uploadToFacebook(Uri imageUri,String caption,Bundle params){
        GraphRequest.newPostRequest(
                AccessToken.getCurrentAccessToken(),
                "/app/uploads",null,
                sessionResponse -> {
                    try {
                        String uploadId = sessionResponse.getJsonObject().getString("id");
                        Toast.makeText(this, "Upload id is "+ uploadId, Toast.LENGTH_SHORT).show();

                        GraphRequest.newUploadPhotoRequest(
                                accessToken,
                                uploadId,
                                imageUri,
                                caption,
                                params,
                                graphResponse -> {
                                    Log.d("Whatisthis",graphResponse.toString());
                                    Toast.makeText(this, "Created the upload session", Toast.LENGTH_SHORT).show();
                                    Log.d("nothi","yes");
                                }
                        ).executeAsync();
                    } catch (JSONException | FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
        ).executeAsync();
    }

    private void uploadToFirebase(Uri imageUri) {
        StorageReference uploadRef = storage.child("ContentGallery").child(userId).child(System.currentTimeMillis()+".png");
        uploadRef.putFile(imageUri)
                .addOnSuccessListener(t -> {
                    // Add the download url to user gallery
                    uploadRef.getDownloadUrl().addOnSuccessListener(uri -> {

                        Map<String,Object> galleryContent = new HashMap<>();
                        galleryContent.put("url",uri.toString());
                        galleryContent.put("created", Timestamp.now());
                        galleryContent.put("modified", Timestamp.now());

                        pageDbRef.collection("Gallery").add(galleryContent)
                                .addOnCompleteListener(task -> {
                                    Log.d("Update",task.isSuccessful() + " compedted " + task.toString());
                                    Log.d("Update"," compedted");
                                });

//                        Toast.makeText(this, "Uri is "+ uri, Toast.LENGTH_SHORT).show();
//
//                        // Create a new user with a first and last name
//                        Map<String, Object> content = new HashMap<>();
//                        content.put("imageUrl",uri.toString());
//                        content.put("caption","Static Caption");
//                        content.put("accessToken", accessToken.getToken());
//
//                        Log.d("ContentObject",content.toString());
//                        Log.d("ContentObject",content.toString());
//                        // Add a new document with a generated ID
//                        db.collection(userDocRef+"/ContentPool")
//                                .add(content)
//                                .addOnSuccessListener(docRef -> Toast.makeText(this, "Content Uploaded to ContentPool", Toast.LENGTH_LONG).show())
//                                .addOnFailureListener(e -> Toast.makeText(this, "Some error occurred", Toast.LENGTH_LONG).show());
                    });
                });

    }

}