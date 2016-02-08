package com.jack.newsobserver.parser;

import com.jack.newsobserver.util.Constants;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class NewsHtmlPageMinimizer {
    private static final String TAG_MARGIN_STYLE = " style=\"margin: 0 2%;\" ";
    private static final String TAG_WIDTH_STYLE = " width=\"100%\" ";
    private static final String TAG_HEAD = "head" ;
    private static final String TAG_STORY_HEADLINE = "div.story-headline";
    private static final String TAG_EPISODE_HEADLINE = "h1.episode-headline";
    private static final String TAG_STORY_LEADMEDIA = "div.story-leadmedia";
    private static final String TAG_EPISODE_LEADMEDIA = "div.episode-leadmedia";
    private static final String TAG_PHOTOGALERY="sclt-photogallery";
    private static final String TAG_STORY_CONTENT = "div.story-content";
    private static final String TAG_EPISODE_CONTENT = "div.episode-content";
    private static final String TAG_FIGURE_CAPTION = "p.figure-caption";
    private static final String TAG_DIV_OPEN_WITH_MARGIN = "<div "+TAG_MARGIN_STYLE+">";
    private static final String TAG_DIV_CLOSE = "</div>";
    private static final String TAG_ALT = "alt";
    private static final String TAG_SRC = "src";
    private static final String TAG_IMG = "img";


    public static String getMinimizedHtml(String url) throws IOException {
        Document htmlDocument = Jsoup.connect(url).userAgent(Constants.PARSING_BROWSER).get();
        String htmlHeader = getDocumentData(htmlDocument, TAG_HEAD).toString();

        Element newsHeader = getDocumentData(htmlDocument, TAG_STORY_HEADLINE);
        if (null == newsHeader){
            newsHeader = getDocumentData(htmlDocument, TAG_EPISODE_HEADLINE);
        }

        Element newsSubHeader = getDocumentData(htmlDocument, TAG_STORY_LEADMEDIA);
        if (null == newsSubHeader){
            newsSubHeader = getDocumentData(htmlDocument, TAG_EPISODE_LEADMEDIA);
        }
        String newsClearedSubHeader = getFilteredSubHeader(newsSubHeader);

        Element newsBodyContent = getDocumentData(htmlDocument, TAG_STORY_CONTENT);
        if (null == newsBodyContent){
            newsBodyContent = getDocumentData(htmlDocument, TAG_EPISODE_CONTENT);
        }

        String newsBody = TAG_DIV_OPEN_WITH_MARGIN+String.valueOf(newsBodyContent)+TAG_DIV_CLOSE;
        Document newsHtmlPage = Jsoup.parse(htmlHeader+String.valueOf(newsHeader)+newsClearedSubHeader+newsBody);
        return newsHtmlPage.toString();
    }


    private static Element getDocumentData(Document doc,String tag) {
        return doc.select(tag).first();
    }

    private static String getFilteredSubHeader(Element subHeader){
        String filteredSubHeaderImageString = "";
        String filteredSubHeaderCaptionString = "";
        Document doc = Jsoup.parse(String.valueOf(subHeader));
        Element subheaderImage = doc.select(TAG_IMG).first();
        Element subheaderCaption = doc.select(TAG_FIGURE_CAPTION).first();
        if (null != subheaderImage) {
            String imgAlt = subheaderImage.attr(TAG_ALT);
            String imgSrc = subheaderImage.attr(TAG_SRC);
            filteredSubHeaderImageString="<"+TAG_IMG+" "+TAG_ALT+"=\""+imgAlt+"\""+TAG_WIDTH_STYLE+TAG_SRC+"=\""+imgSrc+"\">";
        }
        if (null != subheaderCaption){
            filteredSubHeaderCaptionString = subheaderCaption.toString();
        }
        if (null == subheaderImage & null == subheaderCaption){
            return String.valueOf(subHeader);
        }else {
            return TAG_DIV_OPEN_WITH_MARGIN+filteredSubHeaderImageString+" "+filteredSubHeaderCaptionString+TAG_DIV_CLOSE;
        }
    }
}
