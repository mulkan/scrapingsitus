/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JSOUP;

import java.io.File;
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
    private ArrayList <String> VISITED_URL; //sebagai pencatat URL yang pernah dikunjungi
    private String URL; //alamat utama yang akan di crawler
    private String NAMA_HOST; //untuk parsing dari sebuah alamat website
                            //misalkan https://softscient.com/index.php/contoh post
                            //maka MAIN_HOST nya adalah https://softscients.com
    private int COUNT_INDEX = 1; //untuk menghitung jumlah URL yang dikunjungi
    private Connection conn = null;
    private PreparedStatement pstmt;
    public CrawlerRekursif(File file,String url){
        try {
            String db = "jdbc:sqlite:"+file.getPath();            
            conn = DriverManager.getConnection(db);
            String sql = "CREATE TABLE IF NOT EXISTS link (\n"
                    + "	id integer PRIMARY KEY,\n"
                    + "	name text NOT NULL, \n"
                    + "	error text \n"
                    + ");";
            Statement stmt = conn.createStatement();            
            stmt.execute(sql);
            String insert = "INSERT INTO link (name,error) VALUES(?,?)";
            pstmt = conn.prepareStatement(insert);        
            VISITED_URL = new ArrayList();
            this.URL=url;
            NAMA_HOST = new URL(url).getHost();
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
        
        File file = new File("D:/cdc.db");
        
        CrawlerRekursif m = new CrawlerRekursif(file,"https://cdc.uns.ac.id/");
        
    }
    private void scrap(String f){       
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
                      !lnk.contains("=mobile") & //abaikan versi mobile
                      !lnk.contains("?amp") & //abaikan versi AMP
                      !lnk.contains(".xls") &
                      !lnk.contains(".txt") &
                      !lnk.contains(".docx") &
                      !lnk.contains(".rar") &
                      !lnk.contains(".gif") &
                      !lnk.contains(".zip") &
                      !lnk.contains("tag") & //ini untuk wordpress, abaikan tag
                      !lnk.contains("page") & //ini untuk wordpress, abaikan page
                      !lnk.contains(".csv") &
                      !lnk.contains("category")){ //ini untuk wordpress, abaikan page
                  String host = new URL(lnk).getHost();
                  //jika link tersebut milik HOST, lanjutkan saja               
                  if(host.equals(NAMA_HOST) & !lnk.equals(URL)){ 
                      local_url.add(lnk);
                      if(!VISITED_URL.contains(lnk)){ //masukan link jika belum pernah dikunjungi    
                          pstmt.setString(1,lnk); //tambahkan ke db
                          pstmt.setString(2,""); //tambahkan ke db
                          pstmt.executeUpdate();
                          System.out.println("\t"+COUNT_INDEX+" : "+lnk);
                          COUNT_INDEX++;                          
                          VISITED_URL.add(lnk); //tambahkan ke visited_url
                          scrap(lnk); //lakukan scraping lagi           
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