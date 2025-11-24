package geet;

import java.io.*;
import java.util.HashMap;

public class Tree implements Serializable{
    private String type;
    private String length;
    private HashMap<String,String> map;
    public HashMap<String, String> getMap() {
        return map;
    }

    public Tree( String length, HashMap map) {
        this.type = "Tree ";
        this.length = length + "\n";
        this.map = map;
    }
    @Override
    public String toString() {
        return "Tree " + length +
                "\n" + "Blob " + '\n' + map ;
    }
}