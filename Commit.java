package geet;

import java.io.*;

public class Commit implements Serializable{
    private String type;
    private String treetype;
    private String size;
    private String pid;
    private String id;
    private String name1;
    private String comtime;
    private String name2;
    private String commess;
    private String comtime2;
    private String n = "\n";

    public Commit() {

    }
    public String getPid() {
        return pid;
    }

    public String getId() {
        return id;
    }

    public String getComtime() {
        return comtime;
    }

    public String getCommess() {
        return commess;
    }


    public Commit(String pid, String size , String id, String name1, String comtime, String name2, String commess) {
        this.pid = pid;
        this.type =n +  "commit ";
        this.size = String.valueOf(size);
        this.treetype = " Tree ";
        this.id = id;
        this.name1 = n + name1 + " ";
        this.comtime = comtime + n;
        this.name2 = name2 + " ";
        this.comtime2 = comtime + n;
        this.commess = String.valueOf(commess);
    }

    @Override
    public String toString() {
        return pid + n + "commit " + size + " Tree "+ id + n + name1 + " " + comtime + n + name2 + " " + comtime + n + commess ;
    }
}