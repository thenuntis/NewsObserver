package com.jack.newsobserver.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class NewsHtmlPageMinimizer {
    private static final String TAG_MARGIN_STYLE = " style=\"margin: 0 2%;\" ";
    private static final String TAG_WIDTH_STYLE = " width=\"100%\" ";
    private static final String TAG_HEAD = "head" ;
    private static final String TAG_COLFULL = "div.colfull";
    private static final String TAG_STORY_LEADMEDIA = "div.story-leadmedia";
    private static final String TAG_PHOTOGALERY="sclt-photogallery";
    private static final String TAG_STORY_CONTENT = "div.story-content";
    private static final String TAG_FIGURE_CAPTION = "p.figure-caption";
    private static final String TAG_DIV_OPEN_WITH_MARGIN = "<div "+TAG_MARGIN_STYLE+">";
    private static final String TAG_DIV_CLOSE = "</div>";
    private static final String TAG_ALT = "alt";
    private static final String TAG_SRC = "src";
    private static final String TAG_IMG = "img";



    public static String getMinimizedHtml(String url) throws IOException {
        Document htmlDocument = Jsoup.connect(url).get();
        String htmlHeader = getDocumentData(htmlDocument, TAG_HEAD);
        String newsHeader = getDocumentData(htmlDocument, TAG_COLFULL);
        String newsSubHeader = getFilteredSubHeader(getDocumentData(htmlDocument, TAG_STORY_LEADMEDIA));
        String newsBody = TAG_DIV_OPEN_WITH_MARGIN+getDocumentData(htmlDocument, TAG_STORY_CONTENT)+TAG_DIV_CLOSE;
        Document newsHtmlPage = Jsoup.parse(htmlHeader+newsHeader+newsSubHeader+newsBody);
        return String.valueOf(newsHtmlPage);
    }


    private static String getDocumentData(Document doc,String tag) {
        Element mainElement = doc.select(tag).first();
        return String.valueOf(mainElement);
    }
    private static String getFilteredSubHeader(String s){
        String filteredSubHeaderImageString = "";
        String filteredSubHeaderCaptionString = "";
        Document doc = Jsoup.parse(s);
        Element subheaderImage = doc.select(TAG_IMG).first();
        Element subheaderCaption = doc.select(TAG_FIGURE_CAPTION).first();
        if (null != subheaderImage) {
            String imgAlt = subheaderImage.attr(TAG_ALT);
            String imgSrc = subheaderImage.attr(TAG_SRC);
            filteredSubHeaderImageString="<"+TAG_IMG+" "+TAG_ALT+"=\""+imgAlt+"\""+TAG_WIDTH_STYLE+TAG_SRC+"=\""+imgSrc+"\">";
        }
        if (null != subheaderCaption){
            filteredSubHeaderCaptionString = String.valueOf(subheaderCaption);
        }
        if (null == subheaderImage & null == subheaderCaption){
            return s;

        }else {
            return TAG_DIV_OPEN_WITH_MARGIN+filteredSubHeaderImageString+" "+filteredSubHeaderCaptionString+TAG_DIV_CLOSE;
        }
    }
}
