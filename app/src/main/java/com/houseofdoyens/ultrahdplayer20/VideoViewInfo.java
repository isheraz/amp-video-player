package com.houseofdoyens.ultrahdplayer20;

class VideoViewInfo {

    String id;
    String title;
    String filePath;
    String bucketName;
    String date;
    String duration;
    String resolution;
    String size;

    public VideoViewInfo(String id, String title, String filePath, String bucketName,
                         String date, String duration, String resolution, String size) {
        this.id = id;
        this.title = title;
        this.filePath = filePath;
        this.bucketName = bucketName;
        this.date = date;
        this.duration = duration;
        this.resolution = resolution;
        this.size = size;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}