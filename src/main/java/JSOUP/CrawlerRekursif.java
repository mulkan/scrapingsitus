/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JSOUP;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
/**
 *
 * @author User
 */
public class CrawlerRekursif {
    ArrayList <String> VISITED_URL;
    String MAIN_URL;
    String MAIN_HOST;
    int COUNT_INDEX = 1;
    Connection conn = null;
    PreparedStatement pstmt;
    public CrawlerRekursif(String url){
        try {
            String db = "jdbc:sqlite:D:/softcsients.db";
            // create a connection to the database
            conn = DriverManager.getConnection(db);            
            System.out.println("Connection to SQLite has been established.");
            // SQL statement for creating a new table
            String sql = "CREATE TABLE IF NOT EXISTS link (\n"
                    + "	id integer PRIMARY KEY,\n"
                    + "	name text NOT NULL, \n"
                    + "	error text \n"
                    + ");";
            Statement stmt = conn.createStatement();
            // create a new table
            stmt.execute(sql);
            String insert = "INSERT INTO link (name,error) VALUES(?,?)";
            pstmt = conn.prepareStatement(insert);
        
            VISITED_URL = new ArrayList();
            this.MAIN_URL=url;
            MAIN_HOST = new URL(url).getHost();
            Thread tr = new Thread(new Runnable(){
                @Override
                public void run() {
                    scrap(url);
                }
            }){                
            };
            tr.start();
        } catch (MalformedURLException ex) {
            Logger.getLogger(CrawlerRekursif.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException e){
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        //https://softscients.com/
        //http://www.detik.com/
        //https://idschool.net/
        //https://www.reyneraea.com/
        CrawlerRekursif m = new CrawlerRekursif("https://softscients.com/");
        
    }
    public void scrap(String f){       
        try {
            Document doc = Jsoup.connect(f).userAgent("Mozilla").get();
            Elements links = doc.select("a[href]");
            if(links.isEmpty()){
                return;
            }
            ArrayList <String> local_url = new ArrayList();
            for (Element link : links) {
              String lnk = link.attr("href");
              //abaikan link yang mengandung image
              //comment, header, atau link kosong
              if(!local_url.contains(lnk) & 
                      !lnk.equals("") & 
                      !lnk.contains("#") & 
                      !lnk.contains(".png") & 
                      !lnk.contains(".jpg") &
                      !lnk.contains(".jpeg") &
                      !lnk.contains(".bmp") &
                      !lnk.contains(".xlsx") &
                      !lnk.contains(".xls") &
                      !lnk.contains(".txt") &
                      !lnk.contains(".docx") &
                      !lnk.contains(".rar") &
                      !lnk.contains(".gif") &
                      !lnk.contains(".zip") &
                      !lnk.contains("tag") &
                      !lnk.contains("page") &
                      !lnk.contains(".csv") &
                      !lnk.contains("category")){
                  String host = new URL(lnk).getHost();
                  //jika link tersebut milik HOST, lanjutkan saja                  
                  if(host.equals(MAIN_HOST) & !lnk.equals(MAIN_URL)){ 
                      local_url.add(lnk);
                      if(!VISITED_URL.contains(lnk)){ //masukan link jika belum pernah dikunjungi    
                          pstmt.setString(1,lnk); //tambahkan ke db
                          pstmt.setString(2,""); //tambahkan ke db
                          pstmt.executeUpdate();
                          System.out.println("\t"+COUNT_INDEX+" : "+lnk);
                          COUNT_INDEX++;                          
                          VISITED_URL.add(lnk);
                          scrap(lnk);                          
                      }
                  }
              }
              Thread.sleep(10);
            }                
        } catch (IOException e) {
            
        } catch (InterruptedException ex) {
            Logger.getLogger(CrawlerRekursif.class.getName()).log(Level.SEVERE, null, ex);
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}