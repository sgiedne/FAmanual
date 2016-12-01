package xyz.thefearpoet.famanual.searchgoogle;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;


public class SearchGoogle {

    String google = "https://www.google.com/search?q=";
    String charset = "UTF-8";


    public SearchGoogle(){

    }

    public String searchFor(String queryString){
        String result = "";
        try {

            URLConnection connection =new URL(google + URLEncoder.encode(queryString, charset)).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.connect();

            BufferedReader r  = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                sb.append(line);
            }
            //System.out.println(sb.toString());

            result = findFirstTitle(sb.toString());
            if (result == null || result.isEmpty()){
                result = "I don't know";
            }

        }
        catch(MalformedURLException urlEx){
            result = "Url error exception" + urlEx ;
        }
        catch(UnsupportedEncodingException encEx)
        {
            result = "Encoding error exception";
        }
        catch (IOException ioEx){
            result = "IO error exception";
        }
        catch (Exception e){
            result = "General error exception";
        }
        return result;

    }

    private String findFirstTitle(String html){

        String res = "";
        Document doc = Jsoup.parse(html);

        Elements links = doc.select("div[class=_Jig]");

        for (Element link: links){
            Elements bodies = link.select("span");
            res =  bodies.text();
            break;
        }
        return res;
    }



    public static void main(String[] args) throws Exception {

        String searchTerm = "";
        if (args.length > 0) {
            searchTerm = args[0];
        }
        SearchGoogle searchClient = new SearchGoogle();
        System.out.println("Searching answer for: " +  searchTerm + "\n");
        String response = searchClient.searchFor(searchTerm);
        System.out.println(response);

    }

}
