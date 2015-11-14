package name.gromovikov.jarr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */

public class FeedViewFragment extends Fragment {
    //public static final String FEEDURL = "http://ladybyron.net/feed/rss/";
    public static final String FEEDURL = "http://4pda.ru/feed/rss/";
    public static final int VIEW_MODE_BROWSER = 0;
    public static final int VIEW_MODE_RENDERED = 1;
    public static final int VIEW_MODE_PARSED = 2;
    private int postViewMode = VIEW_MODE_PARSED;
    private OnPostSelectedListener onPostSelectedListener;
    private SimpleCursorAdapter feedAdapter;
    private Cursor feedCursor;
    private NewsDb newsDb;

    public interface OnPostSelectedListener {
        void onPostSelected(String url);
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
    public void onAttach(Activity activity) {
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
        ListView feedListView = (ListView) feedView.findViewById(R.id.listView);
        feedListView.setAdapter(feedAdapter);

        // Set list view item click listener
        feedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("JARR", String.format("selected id = %d", id));
                String link = "somelink";
                if (postViewMode == VIEW_MODE_RENDERED) {
                    onPostSelectedListener.onPostSelected(link);
                } else if (postViewMode == VIEW_MODE_PARSED) {
                    (new PostParser(new PostParser.OnPostParsedListener() {
                        @Override
                        public void onPostParsed(String s) {
                            Log.d("JARR", s);
                        }
                    }

                    )).execute(link);
                } else {
                    //open link in a browser by default
                    Intent i = new Intent();
                    i.setAction(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(link));
                    startActivity(i);
                }

            }
        });


        return feedView;

    }
}



