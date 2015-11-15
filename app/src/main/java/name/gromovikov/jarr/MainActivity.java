package name.gromovikov.jarr;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity
        implements FeedViewFragment.OnPostSelectedListener {

    private FeedViewFragment feedViewFragment;
    private PostWebViewFragment postWebViewFragment;
    private PostTextViewFragment posTextViewFragment;
    private NewsDb newsDb;

    //those should be put in settings in a normal project
    public static final String FEEDURL = "http://4pda.ru/feed/rss/";
    public static final String LOGTAG = "MAINACT";
    public static final int VIEW_MODE_BROWSER = 0;
    public static final int VIEW_MODE_RENDERED = 1;
    public static final int VIEW_MODE_PARSED = 2;
    private int postViewMode = VIEW_MODE_PARSED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        newsDb = new NewsDb(this);
        // we probably will return to this later - pja
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        Log.d(LOGTAG, "OnCreate");



        //check and load new post if post base is empty..
        //if (newsDb.isEmpty()) loadNews();
        //..OR the user has swiped down to re-check

        //if (savedInstanceState == null) {
            Log.d(LOGTAG, "Initial fragment population");
            feedViewFragment = new FeedViewFragment();
            feedViewFragment.setArguments(getIntent().getExtras());
            getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment, feedViewFragment);
            fragmentTransaction.commit();
        //}


    }





    public void onPostSelected(long id) {
        if (postViewMode == VIEW_MODE_PARSED || postViewMode == VIEW_MODE_RENDERED) {
            //TODO parse and add text to database if it hasn't been cached yet
            Bundle bundle = new Bundle();
            Fragment fragmentToDisplay;

            if (postViewMode == VIEW_MODE_PARSED ){
                //get parsed text from database if it exists
                String text = newsDb.getTextById(id);
                bundle.putString("text", text);
                fragmentToDisplay = new PostTextViewFragment();
            }

            else {
                //get parsed text from database if it exists
                String link = newsDb.getLinkById(id);
                bundle.putString("url", link);
                fragmentToDisplay = new PostWebViewFragment();
            }

            fragmentToDisplay.setArguments(bundle);
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            //set the post in special fragment if available
            if (findViewById(R.id.post_fragment) != null){
                fragmentTransaction.replace(R.id.post_fragment, fragmentToDisplay);
            }
            //or insteadof the feed if no special place is found (stash feed to put back on "back" press)
            else {
                fragmentTransaction.replace(R.id.fragment, fragmentToDisplay);
                fragmentTransaction.addToBackStack(null);
            }
            // Commit the transaction
            fragmentTransaction.commit();

        } else {
            String link = newsDb.getLinkById(id);
            //open link in a browser by default
            Intent i = new Intent();
            i.setAction(Intent.ACTION_VIEW);
            i.setData(Uri.parse(link));
            startActivity(i);
        }



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
