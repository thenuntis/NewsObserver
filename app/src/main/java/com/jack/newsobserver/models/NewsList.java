package com.jack.newsobserver.models;

import java.util.Date;

public class NewsList {

    private long storyId;
    private long parentTopicId;
    private String storyTitle;
    private String storyLink;
    private String storyAuthor;
    private Date storyPubdate;
    private String imgUrl;
    private Date newsLastWatched;



    public long getStoryId() {
        return storyId;
    }

    public void setStoryId(long storyId) {
        this.storyId = storyId;
    }

    public long getParentTopicId() {
        return parentTopicId;
    }

    public void setParentTopicId(long parentTopicId) {
        this.parentTopicId = parentTopicId;
    }

    public String getStoryTitle() {
        return storyTitle;
    }
    public void setStoryTitle(String storyTitle) {
        this.storyTitle = storyTitle;
    }

    public String getStoryLink() {
        return storyLink;
    }
    public void setStoryLink(String storyLink) {
        this.storyLink = storyLink;
    }

    public String getStoryAuthor() {
        return storyAuthor;
    }
    public void setStoryAuthor(String storyAuthor) {
        this.storyAuthor = storyAuthor;
    }

    public Date getStoryPubdate() {
        return storyPubdate;
    }
    public void setStoryPubdate(Date storyPubdate) {
        this.storyPubdate = storyPubdate;
    }

    public String getImgUrl() {
        return imgUrl;
    }
    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Date getNewsLastWatched() {
        return newsLastWatched;
    }
    public void setNewsLastWatched(Date newsLastWatched) {
        this.newsLastWatched = newsLastWatched;
    }
}