package com.example.coversong.view;

public class WriteInfo {
    private String title;
    private String contents;
    private String publisher;

    public WriteInfo() { }

    public WriteInfo(String title, String contents, String publisher) {
        this.title=title;
        this.contents=contents;
        this.publisher = publisher;
    }
    public String getTitle() { return this.title;}
    public void getTitle(String title) {this.title=title;}
    public String getContents() { return this.contents;}
    public void setContents(String publisher) { this.contents = contents;}
    public String getPublisher() { return this.publisher;}
    public void setPublisher(String publisher) { this.publisher = publisher;}
    @Override
    public String toString() {
        return "Post{" + "title=" + title + "/" + "contents=" + contents + "/" + "publisher=" + publisher +" }";
    }
}