package name.gromovikov.jarr;

import org.xml.sax.InputSource;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by pja on 09.11.2015.
 * based on https://github.com/itcuties/Android-AsyncTask-RSS-Reader
 */
public class RssLoader {

    private String rssUrl;

    /**
     * Constructor
     *
     * @param url
     */
    public RssLoader(String url) {
        this.rssUrl = url;
    }

    /**
     * Get RSS items.
     *
     * @return
     */
    public List<NewsEntry> getItems() throws Exception {

        URL url = new URL (rssUrl);
        InputStream inputStream = url.openStream();
        //encoding autodetection failure caused parsing error - pja
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

}

