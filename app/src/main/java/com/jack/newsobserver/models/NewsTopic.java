package com.jack.newsobserver.models;

public class NewsTopic {
    private long topicId;
    private NewsCategory topicCategory;
    private String topicName;
    private String topicLink;

    public long getTopicId() {
        return topicId;
    }

    public void setTopicId(long topicId) {
        this.topicId = topicId;
    }

    public NewsCategory getTopicCategory() {
        return topicCategory;
    }

    public void setTopicCategory(NewsCategory topicCategory) {
        this.topicCategory = topicCategory;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getTopicLink() {
        return topicLink;
    }

    public void setTopicLink(String topicLink) {
        this.topicLink = topicLink;
    }
}
