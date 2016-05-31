package uk.ac.ncl.cs.csc8101.weblogcoursework;

/**
 * Created by don on 13/02/15.
 */
public class UrlHourCounter {

    private String url;
    private Integer hour;
    private Long counter;

    public UrlHourCounter(String url, Integer hour, Long counter) {
        this.url = url;
        this.hour = hour;
        this.counter = counter;
    }
    public UrlHourCounter incrementCounter() {
        this.counter++;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public Integer getHour() {
        return hour;
    }

    public Long getCounter() {
        return counter;
    }
}
