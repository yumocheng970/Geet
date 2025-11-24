package geet;

import java.io.*;

public class test{
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String p = "min";
        String len = "1154";
        String tree = "tree";
        String id = "2200022780";
        String firstname = "Kal";
        String time = Utils.getcurrenttime();
        String lastname = "El";
        String message = "LEAVE NOW";
        Commit commit = new Commit(p,len,id,firstname,time,lastname,message);
        ObjectOutputStream oop = new ObjectOutputStream(new FileOutputStream(new File("D:\\java_code\\random\\src\\geet\\aaa")));
        oop.writeObject(commit);
        oop.flush();
        oop.close();


        ObjectInputStream oip = new ObjectInputStream(new FileInputStream(new File("D:\\java_code\\random\\src\\geet\\aaa")));
        Commit cc = (Commit) oip.readObject();
        System.out.println(cc.getCommess());
        System.out.print(cc.getComtime());
        System.out.print(cc.getPid());
        System.out.print(cc.getId());
    }
}