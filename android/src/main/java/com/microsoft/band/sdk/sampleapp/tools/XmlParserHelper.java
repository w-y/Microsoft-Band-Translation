package com.microsoft.band.sdk.sampleapp.tools;

import android.util.Log;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by wangyue on 15/10/6.
 */
public class XmlParserHelper {

    private static XmlParserHelper mInstance;

    private DocumentBuilderFactory docBuilderFactory;

    private XmlParserHelper() {
        this.docBuilderFactory = DocumentBuilderFactory.newInstance();
    }

    public static synchronized XmlParserHelper getInstance() {
        if (mInstance == null) {
            mInstance = new XmlParserHelper();
        }
        return mInstance;
    }

    public String parse(String xml) {

        DocumentBuilder docBuilder = null;

        try {
            docBuilder = docBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            Log.e("ERROR", "doc builder error", e);
        }

        if (docBuilder != null) {

            Document doc = null;

            try {
                doc = docBuilder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
            } catch (SAXException e) {
                Log.e("ERROR", "parse error", e);
            } catch (IOException e) {
                Log.e("ERROR", "parse error", e);
            }

            if (doc != null) {
                doc.getDocumentElement().normalize();
                return doc.getDocumentElement().getTextContent();
            }
        }

        return null;
    }
}
