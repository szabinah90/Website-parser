package parsingWebsite.myElasticConnector;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.jsoup.nodes.Document;
import parsingWebsite.ParsingMain;
import parsingWebsite.fileReader.MyFileReader;
import parsingWebsite.urlHandlers.Extractors;
import parsingWebsite.urlHandlers.URLHandlers;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

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
        Extractors extractors = new Extractors(parsedURL.getElementsByTag("meta"));
        String author = extractors.getAuthor();
        Date datePublished = extractors.getDatePublished();


        /**
         * Creating map
         */
        HashMap<String, Object> outerMap = new HashMap<>();
        outerMap.put("date_published", datePublished);
        if (author.equals("")) {
            author = "Unknown author";
        }
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
