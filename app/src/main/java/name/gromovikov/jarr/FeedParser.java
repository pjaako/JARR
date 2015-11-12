package name.gromovikov.jarr;


import android.os.AsyncTask;
import android.util.Log;
import org.xml.sax.InputSource;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by pja on 12.11.2015.
 */
public class FeedParser extends AsyncTask<String, Void, List<RssRecord> > {

    private OnFeedParsedListener onFeedParsedListener;

    public FeedParser (OnFeedParsedListener onFeedParsedListener){
        this.onFeedParsedListener = onFeedParsedListener;
    }

    public interface OnFeedParsedListener{
        void onFeedParsed(List<RssRecord> feed);
    }

    @Override
    protected List<RssRecord> doInBackground(String... urls) {

        try {
            URL  url = new URL(urls[0]);
            InputStream inputStream = url.openStream();
            //encoding autodetection failure caused parsing error - pja - we know 4PDA uses win1251
            //so lets apply BandAid
            //TODO -  encoding still needs to be autodetected, not hardcoded - pja
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "cp1251");


            // SAX parse RSS data
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            RssHandler rssHandler = new RssHandler();

            InputSource inputSource = new InputSource();
            inputSource.setCharacterStream(inputStreamReader);
            saxParser.parse(inputSource, rssHandler);

            return rssHandler.getItems();
        }
        catch (Exception e){
            Log.e ("JARR", e.toString());
            return null;
        }


    }

    @Override
    protected void onPostExecute(final List<RssRecord> result) {
        super.onPostExecute(result);
        onFeedParsedListener.onFeedParsed(result);
    }
}