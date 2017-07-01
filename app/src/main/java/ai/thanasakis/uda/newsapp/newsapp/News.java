package ai.thanasakis.uda.newsapp.newsapp;

import java.io.Serializable;

/**
 * Created by programbench on 6/27/2017.
 */

public class News implements Serializable {

    private String mSectionName;
    private String mPublicationDate;
    private String mWebTitle;
    private String mWebUrl;


    public News(String title, String section, String date, String url) {
        mWebTitle = title;
        mSectionName = section;
        mPublicationDate = date;
        mWebUrl = url;
    }
    public News(News clone) {
        mWebTitle = clone.getTitle();
        mSectionName = clone.getSection();
        mPublicationDate = clone.getDate();
        mWebUrl = clone.getUrl();
    }

    public String getTitle() {
        return mWebTitle;
    }

    public String getSection() {
        return mSectionName;
    }

    public String getDate() {
        return mPublicationDate;
    }

    public String getUrl() {
        return mWebUrl;
    }

    public void setmSectionName(String mSectionName) {
        this.mSectionName = mSectionName;
    }

    public void setmPublicationDate(String mPublicationDate) {
        this.mPublicationDate = mPublicationDate;
    }

    public void setmWebTitle(String mWebTitle) {
        this.mWebTitle = mWebTitle;
    }

    public void setmWebUrl(String mWebUrl) {
        this.mWebUrl = mWebUrl;
    }

    @Override
    public String toString() {
        return "News{" +
                "mWebTitle='" + mWebTitle + '\'' +
                ", mSectionName=" + mSectionName +
                ", mWebUrl='" + mWebUrl + '\'' +
                ", mPublicationDate=" + mPublicationDate +
                '}';
    }
}