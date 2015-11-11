package name.gromovikov.jarr;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewFragment;

/**
 * A placeholder fragment containing a simple view.
 */
public class PostViewFragment extends WebViewFragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View result= super.onCreateView(inflater, container, savedInstanceState);
        String url = getArguments().getString("url");
        getWebView().getSettings().setJavaScriptEnabled(true);
        // настройка масштабирования
        getWebView().getSettings().setSupportZoom(true);
        getWebView().getSettings().setBuiltInZoomControls(true);
        getWebView().loadUrl(url);

        return(result);

    }



}
