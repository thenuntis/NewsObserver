package com.jack.newsobserver.parser;

import com.jack.newsobserver.models.NewsList;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class NewsListFromXmlParser {

    static final String KEY_ITEM = "item";
    static final String KEY_TITLE = "title";
    static final String KEY_LINK = "link";
    static final String KEY_AUTHOR = "author";
    static final String KEY_PUBDATE = "pubdate";
    static final String KEY_DESCRIPTION = "description";
    private static boolean isTitleFromItem = false;
    private static boolean isLinkFromItem = false;
    private static boolean isDescriptionFromItem = false;

    public List<NewsList> getNewsList(String link, long id) {
        List<NewsList> newsList = new ArrayList<>();
        NewsList curNewsList = null;
        String curText = "";
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new InputStreamReader(getUrlData(link)));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = xpp.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagname.equalsIgnoreCase(KEY_ITEM)) {
                            isTitleFromItem = true;
                            isLinkFromItem = true;
                            isDescriptionFromItem = true;
                            curNewsList = new NewsList();
                            curNewsList.setParentTopicId(id);
                        }
                        break;
                    case XmlPullParser.TEXT:
                        curText = xpp.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if (tagname.equalsIgnoreCase(KEY_ITEM)) {
                            isTitleFromItem = false;
                            isLinkFromItem = false;
                            isDescriptionFromItem = false;
                            newsList.add(curNewsList);
                        } else if (tagname.equalsIgnoreCase(KEY_TITLE) && isTitleFromItem) {
                            curNewsList.setStoryTitle(curText);
                        } else if (tagname.equalsIgnoreCase(KEY_LINK) && isLinkFromItem) {
                            curNewsList.setStoryLink(curText);
                        } else if (tagname.equalsIgnoreCase(KEY_AUTHOR)) {
                            curNewsList.setStoryAuthor(curText);
                        } else if (tagname.equalsIgnoreCase(KEY_PUBDATE)) {
                            curNewsList.setStoryPubdate(curText);
                        } else if (tagname.equalsIgnoreCase(KEY_DESCRIPTION) && isDescriptionFromItem) {
                            int startPos = curText.indexOf("http:");
                            int finishPos = curText.indexOf("'",startPos) ;
                            curText = curText.substring(startPos,finishPos);
                            curNewsList.setImgUrl(curText);
                        }
                        break;
                    default:
                        break;
                }
                eventType = xpp.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newsList;
    }
    private InputStream getUrlData(String url) throws URISyntaxException, IOException {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet method = new HttpGet(new URI(url));
        HttpResponse res = client.execute(method);
        return  res.getEntity().getContent();
    }
}
