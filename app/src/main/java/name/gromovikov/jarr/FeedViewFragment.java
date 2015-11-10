package name.gromovikov.jarr;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */

public class FeedViewFragment extends Fragment {
    //public static final String FEEDURL = "http://ladybyron.net/feed/rss/";
    public static final String FEEDURL = "http://4pda.ru/feed/rss/";
    public static final int VIEW_MODE_BROWSER = 0;
    public static final int VIEW_MODE_WEBVIEW = 1;
    public static final int VIEW_MODE_PARSED = 2;
    public static final int POST_VIEW_MODE = VIEW_MODE_BROWSER;

    public FeedViewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View feedView = inflater.inflate(R.layout.fragment_feed_view, container, false);
        GetRSSDataTask task = new GetRSSDataTask(getContext(),feedView);

        // Start download RSS task
        task.execute(FEEDURL);

        return feedView;

    }

    private class GetRSSDataTask extends AsyncTask<String, Void, List<RssRecord> > {

        private Context context;
        private View rootView;

        public GetRSSDataTask(Context context, View rootView) {
            super();
            this.context=context;
            this.rootView=rootView;

        }

        @Override
        protected List<RssRecord> doInBackground(String... urls) {


            try {
                // Create RSS reader
                RssLoader rssLoader = new RssLoader(urls[0]);

                // Parse RSS, get items
                return rssLoader.getItems();

            } catch (Exception e) {
                Log.e("JARR", e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(final List<RssRecord> result) {

            // Get a ListView from main view
            ListView feedListView = (ListView) rootView.findViewById(R.id.listView);

            // Create a list adapter
            ArrayAdapter<RssRecord> adapter = new ArrayAdapter<RssRecord>(context,android.R.layout.simple_list_item_1, result);
            // Set list adapter for the ListView
            feedListView.setAdapter(adapter);

            // Set list view item click listener
            feedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (POST_VIEW_MODE == VIEW_MODE_WEBVIEW){

                    }
                    else if (POST_VIEW_MODE == VIEW_MODE_PARSED){

                    }
                    else {
                        //open link in a browser by default
                        Intent i = new Intent();
                        i.setAction(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(result.get(position).getLink()));
                        startActivity(i);
                    }

                }
            }
            );
        }
    }
}
