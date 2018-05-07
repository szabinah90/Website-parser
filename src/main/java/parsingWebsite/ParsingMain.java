package parsingWebsite;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import parsingWebsite.fileWriter.MyFileWriter;
import parsingWebsite.myElasticConnector.MyElasticConnector;
import parsingWebsite.urlHandlers.URLHandlers;

import java.io.IOException;
import java.util.ArrayList;

public class ParsingMain {

    public static void main(String[] args) throws IOException {
        MyFileWriter writer = new MyFileWriter();
        URLHandlers urlHandlers = new URLHandlers();

        String urlToParse = "https://index.hu/24ora/?word=1&pepe=1&tol=1999-01-01&ig=2018-05-07&s=keyword";
        String DOMclassName = "datum cikk-date-label";

        /**
         * Parsing desired URL
         */
        Document indexParsed = urlHandlers.parseURLs(urlToParse);

        /**
         * Extracting the relevant part from the HTML code by element class and converting to string
         */
        Elements results = indexParsed.body().getElementsByClass(DOMclassName);
        String resultsString = results.toString();

        ArrayList<String> urls = urlHandlers.extractURL(resultsString);

        /**
         * OPTIONAL: Writing the content of relevant URLs into files
         */
        /*
        for (int k = 0; k < urls.size(); k++) {
            Document temp = urlHandlers.parseURLs(urls.get(k));
            writer.writeToFile("./articles/article" + (k+1) + ".txt", temp.title());
            writer.writeToFile("./articles/article" + (k+1) + ".txt", temp.body().getElementsByClass("cikk-torzs").text());
        }

        /**
         * Creating ElasticSearch cluster connection and uploading the content of relevant URLs
         */


        MyElasticConnector myElasticConnector = new MyElasticConnector();
        myElasticConnector.createIndex("ES_index");
        for (String url : urls) {
            myElasticConnector.uploadingDocuments(url, "ES_index");
        }
        System.exit(0);
    }
}
