package parsingWebsite.urlHandlers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLHandlers {
    public Document parseURLs(String url) throws IOException {
        Document doc = Jsoup.connect(url)
                .data("query", "Java")
                .get();
        return doc;
    }


    public ArrayList<String> extractURL(String urlList) {
        String[] listSplitBySpace = urlList.split(" ");

        ArrayList<String> hrefs = new ArrayList<>();
        for (String element : listSplitBySpace) {
            Matcher m = Pattern.compile("href=\"(.*?)\"").matcher(element);
            if (m.find()) {
                hrefs.add(element);
            }
        }

        ArrayList<String> urls = new ArrayList<>();
        for (String href : hrefs) {
            String[] hrefSplitByEq = href.split("=");
            hrefSplitByEq[1] = hrefSplitByEq[1].substring(1, hrefSplitByEq[1].length()-1);
            urls.add(hrefSplitByEq[1]);
        }

        return urls;
    }
}
