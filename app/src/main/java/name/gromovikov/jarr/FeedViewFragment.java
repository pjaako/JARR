package name.gromovikov.jarr;

import android.content.Context;
import android.database.Cursor;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */

public class FeedViewFragment extends Fragment {
    //public static final String FEEDURL = "http://ladybyron.net/feed/rss/";
    public static final String LOG_TAG = "FEEDFRAG";
    public static final String FEEDURL = "http://4pda.ru/feed/rss/";
    private OnPostSelectedListener onPostSelectedListener;
    private SimpleCursorAdapter feedAdapter;
    private Cursor feedCursor;
    private NewsDb newsDb;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView feedListView;
    public interface OnPostSelectedListener {
        void onPostSelected(long id);
    }

    public FeedViewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    //TODO - Deprecation issues - onAttach-23 gets Context - might work now but needs fixing - pja
    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            onPostSelectedListener = (OnPostSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View feedView = inflater.inflate(R.layout.fragment_feed_view, container, false);
        newsDb = new NewsDb(getActivity());
        feedCursor = newsDb.getMetas();
        feedAdapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_1,
                feedCursor,
                new String[]{NewsDb.Entry.COLUMN_NAME_TITLE},
                new int[]{android.R.id.text1},
                0
        );
        swipeRefreshLayout =
                (SwipeRefreshLayout) feedView.findViewById(R.id.swiperefresh);
        feedListView =
                (ListView) feedView.findViewById(R.id.listView);
        feedListView.setAdapter(feedAdapter);


        // Set list view item click listener
        feedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(LOG_TAG, String.format("selected id = %d", id));
                onPostSelectedListener.onPostSelected(id);

            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(LOG_TAG, "user swiped to refresh");
                loadNews();
            }
        });

        if (newsDb.isEmpty()){
            loadNews();
        }

        return feedView;
    }

    private void loadNews(){
        Log.i (LOG_TAG, "loadNews");
        if (!swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(true);
        FeedParser feedParser = new FeedParser(new FeedParser.OnFeedParsedListener() {
            @Override
            public void onFeedParsed(List<NewsEntry> feed) {
                newsDb.addFromFeed(feed);
                //force feed view to display new metas
                feedAdapter.swapCursor(newsDb.getMetas());
                //start loading full texts to make them available offline
                loadNewPostTexts();

            }
        });

        feedParser.execute(FEEDURL);

    }

    private void loadNewPostTexts(){
        Log.i (LOG_TAG, "loadNewPostTexts");
        //get a list of post text to load
        Cursor cursor = newsDb.findTextlessNews();
        Log.i (LOG_TAG, "texts to load: " + cursor.getCount());
        //load them one-by-one
        while (cursor.moveToNext())
            loadPostText(cursor.getString(cursor.getColumnIndex(NewsDb.Entry.COLUMN_NAME_LINK)));
        cursor.close();
        Log.i(LOG_TAG, "loadNewPostTexts complete");
        swipeRefreshLayout.setRefreshing(false);


    }

    private void loadPostText(final String link){
        Log.i (LOG_TAG, "loadPostText "+ link);

        //and it is also better to avoid a bunch of async parse tasks firing almost at one moment
        //newsDb.addTextForLink(PostParser.parsePost(link), link);

        PostParser postParser = new PostParser(new PostParser.OnPostParsedListener() {
            @Override
            public void onPostParsed(String text) {
                newsDb.addTextForLink (text, link);
            }
        });

        postParser.execute(link);


    }



}



