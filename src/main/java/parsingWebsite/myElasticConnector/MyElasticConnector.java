package parsingWebsite.myElasticConnector;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import parsingWebsite.ParsingMain;
import parsingWebsite.fileReader.MyFileReader;
import parsingWebsite.urlHandlers.URLHandlers;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyElasticConnector {

    public void createIndex(String indexName) {
        MyFileReader fileReader = new MyFileReader();

        /**
         * Establishing connection to Elastic Cluster
         */
        RestHighLevelClient client = null;
        CreateIndexRequest request = null;
        client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")));

        /**
         * Creating index and determine mapping
         */
        ClassLoader classLoader = getClass().getClassLoader();
        String mappingFilePath = classLoader.getResource("elasticMap.json").getFile();

        request = new CreateIndexRequest(indexName);
        String mappingFile = fileReader.readFromFile(mappingFilePath);
        request.mapping("article", mappingFile, XContentType.JSON);

        try {
            client.indices().create(request);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Index created.");
    }

    public void uploadingDocuments(String url, String indexName) {
        ParsingMain parser = new ParsingMain();
        URLHandlers urlHandlers = new URLHandlers();

        /**
         * Estabilishing connection to Elastic Cluster
         */
        RestHighLevelClient client = null;
        client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")));

        IndexRequest request = new IndexRequest(indexName, "article");

        /**
         * Parsing the "incoming" URL to be able to get its elements
         */
        Document parsedURL = null;
        try {
            parsedURL = urlHandlers.parseURLs(url);
        } catch (IOException io) {
            io.printStackTrace();
        }

        /**
         * Extracting the name of the author and date published from a DataNode
         */
        Elements scriptTags = new Elements();
        try {
            scriptTags = urlHandlers.parseURLs(url).getElementsByTag("script");
        } catch (IOException io) {
            io.printStackTrace();
        }

        DataNode authorNode = new DataNode("");
        for (Element element : scriptTags) {
            for (DataNode node : element.dataNodes()) {
                if (node.getWholeData().contains("author")) {
                    authorNode = node;
                }
            }
        }

        String authorNodeData = authorNode.getWholeData();
        Matcher mDate = Pattern.compile("\\d{4}-\\d{2}-\\d{2}").matcher(authorNodeData);
        Matcher mAuthor = Pattern.compile("\"[A-ZÁÉÓŐÚŰÍ][a-záéóőúűí]+( [A-ZÁÉÓŐÚŰÍ][a-záéóőúűí]+)+\"").matcher(authorNodeData);
        Date datePublished = null;
        String author = null;
        if (mDate.find()) {
            System.out.println(authorNodeData.substring(mDate.start(), mDate.end()));
            try {
                datePublished = new SimpleDateFormat("yyyy-MM-dd").parse(authorNodeData.substring(mDate.start(), mDate.end()));
            } catch (ParseException pe) {
                pe.printStackTrace();
            }
        } else {
            System.out.println("Something went wrong with the regular expression or no date can be found.");
        }
        if (mAuthor.find()) {
            System.out.println(authorNodeData.substring(mAuthor.start(), mAuthor.end()));
            author = authorNodeData.substring(mAuthor.start(), mAuthor.end());
        } else {
            author = "Unknown author";
            System.out.println("Something went wrong with the regular expression or the author is unknown.");
        }

        /**
         * Creating map
         */
        HashMap<String, Object> outerMap = new HashMap<>();
        outerMap.put("date_published", datePublished);
        outerMap.put("author", author);
        outerMap.put("title", parsedURL.title());
        outerMap.put("url", url);
        outerMap.put("content", parsedURL.body().getElementsByClass("cikk-torzs").text());

        try {
            client.index(request.source(outerMap));
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Upload complete.");
    }
}
