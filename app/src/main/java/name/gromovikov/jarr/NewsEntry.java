package name.gromovikov.jarr;

/**
 * Created by pja on 09.11.2015.
 * based on https://github.com/itcuties/Android-AsyncTask-RSS-Reader
 */
public class NewsEntry {

    // item title
    private String title;
    // item link
    private String link;

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    // item brief
    private String brief;
    // full entry text
    private String text;

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
