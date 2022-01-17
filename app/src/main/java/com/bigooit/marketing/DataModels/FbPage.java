package com.bigooit.marketing.DataModels;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FbPage {
    private String token;
    private String category ;
    private JSONArray categoryList ;
    private String name ;
    private String id ;
    private JSONArray tasks ;

    public FbPage() {}

    public FbPage(JSONObject page) {
        try {
            this.token = page.getString("access_token");
            this.category = page.getString("category");
            this.categoryList = page.getJSONArray("category_list");
            this.name = page.getString("name");
            this.id = page.getString("id");
            this.tasks = page.getJSONArray("tasks");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

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

}

