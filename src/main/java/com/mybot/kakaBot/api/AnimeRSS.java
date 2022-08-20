package com.mybot.kakaBot.api;

import com.alibaba.fastjson2.JSONObject;
import com.aliyuncs.reader.XmlReader;
import okhttp3.*;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.XMLFormatter;

/**
 * @Author xun
 * @create 2022/8/1 22:23
 */
@Component
public class AnimeRSS {

/*    public static void getAnimeData() throws IOException {

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet("https://bangumi.moe/rss/latest");

        CloseableHttpResponse response = httpClient.execute(httpGet);
        System.out.println(response);
    }*/

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        String URL = "https://bangumi.moe/rss/latest";
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(URL);
        doc.getDocumentElement().normalize();
        int title = doc.getElementsByTagName("title").getLength();
        System.out.println(title);
        System.out.println(doc.getElementsByTagName("title").item(1).getTextContent());
    }
}
