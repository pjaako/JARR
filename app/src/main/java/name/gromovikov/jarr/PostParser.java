package name.gromovikov.jarr;

import android.os.AsyncTask;
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

    private OnPostParsedListener onPostParsedListener;

    public PostParser (OnPostParsedListener onPostParsedListener){
        this.onPostParsedListener = onPostParsedListener;
    }

    public interface OnPostParsedListener{
        void onPostParsed(String s);
    }

    @Override
    protected String doInBackground(String... strings) {
        StringBuffer buffer = new StringBuffer();
        try {
            Log.d("JARR", "Connecting to ["+strings[0]+"]");
            Document doc  = Jsoup.connect(strings[0]).get();
            Log.d("JARR", "Connected to ["+strings[0]+"]");
            // Get document (HTML page) title
            String title = doc.title();
            Log.d("JARR", "Title ["+title+"]");
            buffer.append("["+title+"]");
            //String postText = doc.select(".content_box").text();
            //buffer.append("Post text: " + postText + "\r\n");
            Elements contentElements = doc.select("div.content p, div.content h2 , div.content li");

            for (Element elem : contentElements) {

               buffer.append(elem.text() + "\r\n");
            }


        }
        catch(Throwable t) {
            buffer.append ("No post text \r\n");
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
