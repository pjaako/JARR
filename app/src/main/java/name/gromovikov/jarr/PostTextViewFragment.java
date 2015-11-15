package name.gromovikov.jarr;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class PostTextViewFragment extends Fragment {


    public PostTextViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_post_text_view, container, false);
        String text = getArguments().getString("text");
        TextView textView = (TextView) view.findViewById(R.id.post_text_view);
        textView.setText(text);
        return view;
    }


}
