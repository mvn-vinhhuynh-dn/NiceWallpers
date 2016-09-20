package com.mobiapp.nicewallpapers.api;

import com.mobiapp.nicewallpapers.model.CategoriesRequest;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class XMLParsingDOM {


    private List<CategoriesRequest> datas;

    public void parsingDOM(final OnXMLParsingDOMListener listener) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                /** Create a new textview array to display the results */
                try {
                    URL url = new URL("https://dl.dropboxusercontent.com/s/uvlw6b3dtz6qcfu/wallpapersdata.xml");
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    Document doc = db.parse(new InputSource(url.openStream()));
                    doc.getDocumentElement().normalize();
                    NodeList nodeList = doc.getElementsByTagName("item");

                    /** Assign textview array lenght by arraylist size */
                    datas = new ArrayList<>();
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        Node node = nodeList.item(i);

                        Element fstElmnt = (Element) node;
                        NodeList nameList = fstElmnt.getElementsByTagName("name");
                        Element nameElement = (Element) nameList.item(0);
                        nameList = nameElement.getChildNodes();

                        NodeList coverList = fstElmnt.getElementsByTagName("id");
                        Element coverElement = (Element) coverList.item(0);
                        coverList = coverElement.getChildNodes();

                        datas.add(new CategoriesRequest(nameList.item(0).getNodeValue(),
                                coverList.item(0).getNodeValue()));
                    }
                    if (null != listener) {
                        listener.parseResult(datas);
                    }
                } catch (Exception e) {
                    System.out.println("XML Pasing Excpetion = " + e);
                }
            }
        });
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    public interface OnXMLParsingDOMListener {
        void parseResult(List<CategoriesRequest> datas);
    }
}
