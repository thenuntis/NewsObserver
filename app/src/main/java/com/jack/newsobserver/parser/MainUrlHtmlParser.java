package com.jack.newsobserver.parser;

import com.jack.newsobserver.models.NewsCategory;
import com.jack.newsobserver.models.NewsTopic;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainUrlHtmlParser {
    private static final String MAIN_HTML_ELEMENT = "table.feeds";
    private static final String MAIN_HTML_TITLE = "td.title";
    private static final String MAIN_HTML_CONTENT = "td.content";
    private static final String URL_CONTENT_TEXT = "a";
    private static final String URL_CONTENT_LINK = "href";

    public static List<NewsCategory> startParsingUrl(String url) throws IOException {
        Document htmlDocument = Jsoup.connect(url).get();
        return parseDocument(htmlDocument);
    }

    private static List<NewsCategory> parseDocument(Document htmlDocument) {
        Elements categories = htmlDocument.select(MAIN_HTML_ELEMENT);
        List<NewsCategory> categoriesList = new ArrayList<>();
        for (Element category : categories) {
            Element categoryElement = htmlDocument.select(MAIN_HTML_ELEMENT).get(categories.indexOf(category));
            NewsCategory categoryItem = getFilledCategory(categoryElement);
            categoriesList.add(categoryItem);
        }
        return categoriesList;
    }

    private static NewsCategory getFilledCategory(Element categoryElement) {
        NewsCategory category = new NewsCategory();
        Document bodyOfCategory = Jsoup.parse(String.valueOf(categoryElement));
        String categoryTitle = bodyOfCategory.select(MAIN_HTML_TITLE).first().text();
        category.setCategoryName(categoryTitle);
        Elements topics = bodyOfCategory.select(MAIN_HTML_CONTENT);
        ArrayList<NewsTopic> newTopicsList = getTopicsArrayList(topics);
        category.setCategoryTopics(newTopicsList);
        return category;
    }

    private static ArrayList<NewsTopic> getTopicsArrayList(Elements topics) {
        ArrayList<NewsTopic> newTopicsList = new ArrayList<>();
        for (Element topic : topics) {
            NewsTopic mTopic = new NewsTopic();
            Document bodyOfTopic = Jsoup.parse(String.valueOf(topic));
            Elements topicContent = bodyOfTopic.select(URL_CONTENT_TEXT);
            mTopic.setTopicName(topicContent.text());
            mTopic.setTopicLink(topicContent.attr(URL_CONTENT_LINK));
            newTopicsList.add(mTopic);
        }
        return newTopicsList;
    }
}
