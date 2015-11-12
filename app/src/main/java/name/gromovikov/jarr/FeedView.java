package name.gromovikov.jarr;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

public class FeedView extends AppCompatActivity
        implements FeedViewFragment.OnPostSelectedListener {

    private FeedViewFragment feedViewFragment;

    //those should be put in settings in a normal project
    public static final String FEEDURL = "http://4pda.ru/feed/rss/";
    public static final String LOGTAG = "JARR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_view);
        /* we probably will return to this later - pja
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        //check and load new post if post base is empty..
        loadNewPosts();
        //..OR the user has swiped down to re-check

/*        if (savedInstanceState == null) {

            feedViewFragment = new FeedViewFragment();
            feedViewFragment.setArguments(getIntent().getExtras());
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.fragment, feedViewFragment);
            fragmentTransaction.commit();
        }*/

    }

    private void loadNewPosts(){
        FeedParser feedParser = new FeedParser(new FeedParser.OnFeedParsedListener() {
            @Override
            public void onFeedParsed(List<NewsEntry> feed) {
                for (NewsEntry feedEntry : feed){
                    Log.d (LOGTAG, feedEntry.getTitle());
                }
            }
        });

        feedParser.execute(FEEDURL);

    }

    public void onPostSelected(String url) {


        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        //fragmentTransaction.addToBackStack(null);
        PostViewFragment postViewFragment = new PostViewFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        postViewFragment.setArguments(bundle);
        //set the post in special fragment if available
        if (findViewById(R.id.post_fragment) != null){
            fragmentTransaction.replace(R.id.post_fragment, postViewFragment);
        }
        //or insteadof the feed if no special place is found (stash feed to put back on "back" press)
        else {
            fragmentTransaction.replace(R.id.fragment, postViewFragment);
            fragmentTransaction.addToBackStack(null);
        }
        // Commit the transaction
        fragmentTransaction.commit();

    }

    //return to previous (list) fragment on "back" key
    //TODO - have I to do this explicitly or is it supposed to work by itself? (it doesn't) - pja
    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0 ){
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_feed_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
