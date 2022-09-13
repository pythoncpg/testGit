package com.xiaomi.myapplication.Bean;

import java.util.ArrayList;
import java.util.List;

public class PhotoUrl {
    private List<String> title;
    private List<String> url ;

    public void addTitle(String name){

        if (title==null) title = new ArrayList<>();
            title.add(name);
    }

    public void addUrl(String photoUrl){
        if (url==null) url = new ArrayList<>();
        url.add(photoUrl);
    }

    public List<String> getTitle() {
        return title;
    }

    public void setTitle(List<String> title) {
        this.title = title;
    }

    public List<String> getUrl() {
        return url;
    }

    public void setUrl(List<String> url) {
        this.url = url;
    }
}
