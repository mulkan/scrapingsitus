/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package JSOUP;

import java.io.IOException;
import java.util.ArrayList;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author User
 */
public class Crawler {
    public static void main(String[] args) {
        try {            
            Document doc = Jsoup.connect("https://softscients.com/").get();
            Elements links = doc.select("a[href]");
            for (Element link : links) {                
                System.out.println("nlink: " + link.attr("href"));                
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
