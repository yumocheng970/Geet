package geet;

import java.io.*;

public class Blob implements Serializable{

    private static final long serialVersionUID=1L;
    private  String type;

    private  String length;

    private String content;

    public String getContent() {
        return content;
    }

    public Blob(String inform) {
        this.type = "Blob ";
        this.length = inform.length() + " ";
        this.content = inform;
    }

    @Override
    public String toString() {
        return  type + length + content ;
    }
}