package parsingWebsite.urlHandlers;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Extractors {

    private Elements metaTags;

    public Extractors(Elements metaTags) {
        this.metaTags = metaTags;
    }

    public String getAuthor() {
        String author = "";

        for (Element metaTag : metaTags) {
            if (metaTag.hasAttr("name") && metaTag.attr("name").equals("author")) {
                author = metaTag.attr("content");
            }
        }

        return author;
    }

    public Date getDatePublished() {
        Date datePublished = new Date();

        for (Element metaTag : metaTags) {
            if (metaTag.hasAttr("property") && metaTag.attr("property").equals("article:published_time")) {
                try {
                    datePublished = new SimpleDateFormat("yyyy-MM-dd").parse(metaTag.attr("content"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        return datePublished;
    }
}
