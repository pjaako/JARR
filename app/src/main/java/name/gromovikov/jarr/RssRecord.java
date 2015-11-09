package name.gromovikov.jarr;

/**
 * Created by pja on 09.11.2015.
 * based on https://github.com/itcuties/Android-AsyncTask-RSS-Reader
 */
public class RssRecord {

    // item title
    private String title;
    // item link
    private String link;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return title;
    }

}
