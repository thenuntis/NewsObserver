package com.jack.newsobserver.parser;


import android.content.Context;

import com.jack.newsobserver.StoriesDigest;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class XmlNewsParser {

    static final String KEY_ITEM = "item";
    static final String KEY_TITLE = "title";
    static final String KEY_LINK = "link";
    static final String KEY_AUTHOR = "author";
    static final String KEY_PUBDATE = "pubdate";
    static final String KEY_DESCRIPTION = "description";
    static String mXmlFileName ;
    private static boolean isTitleFromItem = false;
    private static boolean isLinkFromItem = false;
    private static boolean isDescriptionFromItem = false;

    public static List<StoriesDigest> getTopStories(Context ctx, String s) {
        mXmlFileName=s;

        List<StoriesDigest> storiesDigest = new ArrayList<>();

        StoriesDigest curStoriesDigest = null;

        String curText = "";

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();

            FileInputStream fis = ctx.openFileInput(mXmlFileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

            xpp.setInput(reader);

            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {

                String tagname = xpp.getName();

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagname.equalsIgnoreCase(KEY_ITEM)) {
                            isTitleFromItem = true;
                            isLinkFromItem = true;
                            isDescriptionFromItem = true;
                            curStoriesDigest = new StoriesDigest();
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
                            storiesDigest.add(curStoriesDigest);
                        } else if (tagname.equalsIgnoreCase(KEY_TITLE) && isTitleFromItem) {
                            curStoriesDigest.setStoryTitle(curText);
                        } else if (tagname.equalsIgnoreCase(KEY_LINK) && isLinkFromItem) {
                            curStoriesDigest.setStoryLink(curText);
                        } else if (tagname.equalsIgnoreCase(KEY_AUTHOR)) {
                            curStoriesDigest.setStoryAuthor(curText);
                        } else if (tagname.equalsIgnoreCase(KEY_PUBDATE)) {
                            curStoriesDigest.setStoryPubdate(curText);
                        } else if (tagname.equalsIgnoreCase(KEY_DESCRIPTION) && isDescriptionFromItem) {
                            int startPos = curText.indexOf("http:");
                            int finishPos = curText.indexOf("'",startPos) ;
                            curText = curText.substring(startPos,finishPos);
                            curStoriesDigest.setImgUrl(curText);
                        }
                        break;
                    default:
                        break;
                }
                eventType = xpp.next();
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return storiesDigest;
    }
}