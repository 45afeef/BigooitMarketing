package com.bigooit.marketing.DataModels;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FbPage implements Parcelable{
    private String token;
    private String category ;
    private String id ;
    private String name ;
    private long instagramBusinessAccountId;
    private JSONArray tasks ;
    private JSONArray categoryList ;

    public FbPage() {}

    public FbPage(JSONObject page) {
        try {
            this.token = page.getString("access_token");
            this.category = page.getString("category");
            this.name = page.getString("name");
            this.id = page.getString("id");
            this.categoryList = page.getJSONArray("category_list");
            this.tasks = page.getJSONArray("tasks");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected FbPage(Parcel in) {
        token = in.readString();
        category = in.readString();
        id = in.readString();
        name = in.readString();
        instagramBusinessAccountId = in.readLong();
    }

    public static final Creator<FbPage> CREATOR = new Creator<FbPage>() {
        @Override
        public FbPage createFromParcel(Parcel in) {
            return new FbPage(in);
        }

        @Override
        public FbPage[] newArray(int size) {
            return new FbPage[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getInstagramBusinessAccountId() {
        return instagramBusinessAccountId;
    }

    public void setInstagramBusinessAccountId(long instagramBusinessAccountId) {
        this.instagramBusinessAccountId = instagramBusinessAccountId;
    }

    public JSONArray getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(JSONArray categoryList) {
        this.categoryList = categoryList;
    }

    public JSONArray getTasks() {
        return tasks;
    }

    public void setTasks(JSONArray tasks) {
        this.tasks = tasks;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(token);
        dest.writeString(category);
        dest.writeString(id);
        dest.writeString(name);
        dest.writeLong(instagramBusinessAccountId);
    }
}

