package name.gromovikov.jarr;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by pja on 12.11.2015.
 * fetches html page by a given link and pulls out (almost) all text from a div class=content
 * Based on https://github.com/survivingwithandroid/Surviving-with-android/tree/master/AndroidJSoup
 */


public class PostParser extends AsyncTask<String, Void, String> {

    public static final String POST_TEXT_ERROR = "No post text \r\n";
    private OnPostParsedListener onPostParsedListener;

    public PostParser (OnPostParsedListener onPostParsedListener){
        this.onPostParsedListener = onPostParsedListener;
    }

    public interface OnPostParsedListener{
        void onPostParsed(String s);
    }



    @Override
    protected String doInBackground(String... strings) {
         return parsePost(strings[0]);


    }


    public static String parsePost(String link) {
        StringBuilder buffer = new StringBuilder();
        try {
            Log.d("JARR", "Connecting to [" + link + "]");
            Document doc  = Jsoup.connect(link).get();
            Log.d("JARR", "Connected to ["+ link +"]");
            // Get document (HTML page) title
            String title = doc.title();
            Log.d("JARR", "Title ["+title+"]");
            buffer.append(title+"\r\n\r\n");
            Elements contentElements = doc.select("div.content p, div.content h2 , div.content li");

            for (Element elem : contentElements) {

               buffer.append(elem.text() + "\r\n");
            }


        }
        catch(Throwable t) {

            buffer.append (POST_TEXT_ERROR);
            Log.e ("PPARS", t.toString());
        }

        return buffer.toString();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        onPostParsedListener.onPostParsed(s);

    }
}
