package com.tier5.redeemar.RedeemarConsumerApp.pojo;

import java.io.Serializable;

public class BrandVideo implements Serializable {

        private String videoProvider, videoId, videothumb, videoUrl;

    public BrandVideo() {

    }

    public String getVideoProvider() {
        return videoProvider;
    }

    public void setVideoProvider(String videoProvider) {
        this.videoProvider = videoProvider;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getVideothumb() {
        return videothumb;
    }

    public void setVideothumb(String videothumb) {
        this.videothumb = videothumb;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
