package com.jack.newsobserver.models;

public class NewsTopic {
    private long topicId;
    private long topicCategoryId;
    private String topicName;
    private String topicLink;

    public long getTopicId() {
        return topicId;
    }

    public void setTopicId(long topicId) {
        this.topicId = topicId;
    }

    public long getTopicCategory() {
        return topicCategoryId;
    }

    public void setTopicCategory(long topicCategory) {
        this.topicCategoryId = topicCategory;
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
