package com.gujjubites;

/**
 * Created by admin on 9/12/2017.
 */

public class addNews  {

    String description;
    String img_url;
    String tag;
    String  title;
    String user;

    public addNews() {
    }

    public addNews(String description, String img_url, String tag, String title, String User)
    {
        this.title = title;
        this.description = description;
        this.img_url = img_url;
        this.tag = tag;
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getUser() {
        return user;
    }
}
