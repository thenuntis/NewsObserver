package com.jack.newsobserver;

public class StoriesDigest {

    private String storyTitle;
    private String storyLink;
    private String storyAuthor;
    private String storyPubdate;
    private String imgUrl;


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

    public String getStoryPubdate() {
        return storyPubdate;
    }
    public void setStoryPubdate(String storyPubdate) {
        this.storyPubdate = storyPubdate;
    }

    public String getImgUrl() {
        return imgUrl;
    }
    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @Override
    public String toString() {
        return "StoriesDigest [storyTitle=" + storyTitle + ", storyLink=" + storyLink + ", storyAuthor="
                + storyAuthor + ", storyPubdate=" + storyPubdate + ", imgUrl=" + imgUrl + "]";
    }
}