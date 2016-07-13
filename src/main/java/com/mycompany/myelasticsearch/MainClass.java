/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.myelasticsearch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.tika.Tika;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.xml.sax.SAXException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;

/**
 *
 * @author AKHIL
 */
public class MainClass {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws UnknownHostException {
        // TODO code application logic here

        Tika tika = new Tika();

        String fileEntry = "C:\\Contract\\Contract1.pdf";
        String filetype = tika.detect(fileEntry);
        System.out.println("FileType " + filetype);
        BodyContentHandler handler = new BodyContentHandler(-1);

        Metadata metadata = new Metadata();

        FileInputStream inputstream = null;
        try {
            inputstream = new FileInputStream(fileEntry);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainClass.class.getName()).log(Level.SEVERE, null, ex);
        }
        ParseContext pcontext = new ParseContext();

        //parsing the document using PDF parser
        PDFParser pdfparser = new PDFParser();
        try {
            pdfparser.parse(inputstream, handler, metadata, pcontext);
        } catch (IOException ex) {
            Logger.getLogger(MainClass.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(MainClass.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TikaException ex) {
            Logger.getLogger(MainClass.class.getName()).log(Level.SEVERE, null, ex);
        }
        String docText = "";
        String outputArray[];
        String out[];
        //getting the content of the document
        docText = handler.toString().replaceAll("(/[^\\da-zA-Z.]/)", "");
        // outputArray = docText.split("Article|Section|Borrower|Agents");

        //int definedTermsStart = docText.indexOf("ARTICLE 1");
        int definedTermsEnd = docText.indexOf("SCHEDULES:");

        //int start = docText.indexOf('“', definedTermsStart);
        //int end = docText.indexOf('”', start);
        String toc = docText.substring(0, definedTermsEnd);
        String c = docText.substring(definedTermsEnd);
        System.out.println("Table of content" + toc);
        System.out.println("--------------------------------");
        System.out.println("content" + c);

        out = toc.split("Article|article|ARTICLE");
        int count = 0;
        String outputArrayString = "";
        int s = 0;
        StringBuffer tocOutput = new StringBuffer();

        for (String o : out) {
            if (count != 0) {
                s = Integer.parseInt(String.valueOf(o.charAt(1)));
                if (s == count) {
                    tocOutput.append(o);
                    tocOutput.append("JigarAnkitNeeraj");
                    System.out.println(s);
                }
            }
            outputArrayString += "Count" + count + o;
            count++;
            System.out.println();

        }
        System.out.println("---------------------------------------------------Content---------");
        count = 1;
        StringBuffer contentOutput = new StringBuffer();
        
        String splitContent[] = c.split("ARTICLE|Article");
        for (String o : splitContent) {
            char input = o.charAt(1);
            if (input >= '0' && input <= '9') {
                s = Integer.parseInt(String.valueOf(o.charAt(1)));
                if (s == count) {
                    //System.out.println(s);
                    contentOutput.append(" \n MyArticlesSeparated \n ");
                    System.out.println(s);
                    count++;
                }
                //outputArrayString += "Count" + count + o;
                contentOutput.append(o);
            }

        }
        
        String tableOfContent[];
        tableOfContent=tocOutput.toString().split("JigarAnkitNeeraj");
        
        String splitContectsAccordingToArticles[];
        splitContectsAccordingToArticles=contentOutput.toString().split("MyArticlesSeparated");
        try {
            FileWriter file = new FileWriter("TableOfIndex.txt");
            file.write(tocOutput.toString());
            file.flush();
            file.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        
        try {
            FileWriter file = new FileWriter("ContextsArticles.txt");
            file.write(contentOutput.toString());
            file.flush();
            file.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        
        
        System.out.println(outputArrayString);

        Client client = TransportClient.builder().build()
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.0.1"), 9200))
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.0.1"), 9200));

        try {
            DocumentReader.parseString(docText, client);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainClass.class.getName()).log(Level.SEVERE, null, ex);
        }

// on shutdown
        client.close();
    }

}
