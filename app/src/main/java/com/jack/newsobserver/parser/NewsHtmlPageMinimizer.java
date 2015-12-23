package com.jack.newsobserver.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class NewsHtmlPageMinimizer {
    private static final String TAG_MARGIN_STYLE = "style=\"margin: 0 2%;\"";

    public static String getMinimizedHtml(String url) throws IOException {
        Document htmlDocument = Jsoup.connect(url).get();
        String htmlHeader = getDocumentData(htmlDocument, "head");
        String newsHeader = getDocumentData(htmlDocument, "div.colfull");
        String newsSubHeader = getFilteredSubHeader(getDocumentData(htmlDocument, "div.story-leadmedia"));
        String newsBody = "<div "+TAG_MARGIN_STYLE+">"+getDocumentData(htmlDocument, "div.story-content")+"</div>";
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
        Element subheaderImage = doc.select("img").first();
        Element subheaderCaption = doc.select("p.figure-caption").first();
        if (null != subheaderImage) {
            String imgAlt = subheaderImage.attr("alt");
            String imgSrc = subheaderImage.attr("src");
            filteredSubHeaderImageString="<img alt=\""+imgAlt+"\" width=\"100%\" src=\""+imgSrc+"\">";
        }
        if (null != subheaderCaption){
            filteredSubHeaderCaptionString = String.valueOf(subheaderCaption);
        }
        if (null == subheaderImage & null == subheaderCaption){
            return s;
        }else {
            return "<div "+TAG_MARGIN_STYLE+">"+filteredSubHeaderImageString+" "+filteredSubHeaderCaptionString+"</div>";
        }
    }
}
