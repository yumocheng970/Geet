package geet;

import java.io.*;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.*;

public class Utils {                                        //一些工具函数
    public static HashMap getlastindex() {                              //commit时获取上次commit的index里的Hashmap并返回
        String path = System.getProperty("user.dir");           //获取当前工作区路径
        String path2 = path + "\\work";
        String path3 = path2 + "\\.git";
        String path4= path3 + "\\objects";
        HashMap<String,String> lastindex = null;
        try {
            File f = new File(path3 + File.separator + "HEAD");         //从头文件里找到上次的commit文件
            if (!f.exists()) {
                System.out.println("头文件不存在");
            } else {
                Scanner getLid = new Scanner(f);
                if(getLid.hasNext()){                       //若head文件不为空，继续找
                    String Lid = getLid.nextLine();       //读出head文件里的上次commit id
                    String treeid = gettree(Lid);                                   //通过上次commit id找到上次的tree文件
                    lastindex = getindex(treeid);                                   //读取tree文件里的暂存区hashmap
                }else{
                    lastindex = null;
                }
            }
        }catch (FileNotFoundException f){
            System.out.println("文件未找到"+ f.getMessage());
        }
        return lastindex;
    }

    public static String gettree(String lastID) {                  //通过commit id找到对应tree文件id并返回
        String path = System.getProperty("user.dir");           //获取当前工作区路径
        String path2 = path + "\\work";
        String path3 = path2 + "\\.git";
        String path4 = path3 + "\\objects";
        String treeid = null;
        try {
            File file1 = new File(path4 + File.separator + lastID);         //找到对应的commit文件
            if(file1.exists()) {
                FileInputStream f1ips = new FileInputStream(file1);
                ObjectInputStream objf1ips = new ObjectInputStream(f1ips);                             //读取
                Commit info = (Commit) objf1ips.readObject();
                treeid = info.getId();                               //读出其中的tree id
                System.out.println("tree " + treeid);
            }else{
                System.out.println("commit文件未找到");
            }

        } catch (FileNotFoundException e) {
            System.out.println("文件未找到" + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO错误" + e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            System.out.println("超时" + e.getMessage());
        }
        return treeid;
    }

    public static HashMap getindex(String treeid){                 //读取tree文件，把里面的文件名和hash值条目以Hashmap的形式返回
        String path = System.getProperty("user.dir");           //获取当前工作区路径
        String path2 = path + "\\work";
        String path3 = path2 + "\\.git";
        String path4= path3 + "\\objects";
        HashMap<String,String> map = new HashMap<>();
        try{
            File ftree1 = new File(path4 + File.separator + treeid);        //创建对应的tree文件对象
            if(ftree1.exists()) {
                System.out.println("存在");
                FileInputStream treeips = new FileInputStream(ftree1);
                ObjectInputStream objintree = new ObjectInputStream(treeips);
                Tree treecontent = (Tree) objintree.readObject();                    //反序列化读出其中的tree对象
                map = treecontent.getMap();
                System.out.println("index内容为: " + map);
            }else {
                System.out.println("tree文件不存在");
            }
        } catch (FileNotFoundException e) {
            System.out.println("文件未找到" + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO错误" + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("超时" + e.getMessage());
        }
        return map;                                                       //返回Hashmap
    }

    public static void deletefolder(String folder){ //覆盖工作区用的
        File file = new File(folder);
        deleteDirectoryLegacyIO(file);
    }

    public static void deleteDirectoryLegacyIO(File file){
        File[] list = file.listFiles(); //无法做到list多层文件夹数据
        if (list != null) {
            for (File temp : list) { //先去递归删除子文件夹及子文件
                deleteDirectoryLegacyIO(temp); //递归调用删除
            }
        }
    }
    public static String getcurrenttime(){     //commit操作时返回当前日期和时间的字符串
        long currentTime = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年-MM月dd日-HH:mm:ss");
        Date date = new Date(currentTime);
        String time =  formatter.format(date);
        String t1,t2,t;
        t1= time.replace("年",".");
        t2 = t1.replace("月",".");
        t = t2.replace("日",".");
        return t;
    }

    public static String SHA1(String S) {               //字符串转换成SHA1值
        if(S.length()==0||S == null) {                    //为空返回
            return null;
        }
        char[] HD={'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
        try {
            MessageDigest mdTemp=MessageDigest.getInstance("SHA1");
            mdTemp.update(S.getBytes("UTF-8"));
            byte[] kkt=mdTemp.digest();
            char[] bvs=new char[kkt.length*2];
            int k=0;
            for(int i=0;i<kkt.length;i++) {
                byte byte0=kkt[i];
                bvs[k++]=HD[byte0>>>4&0xf];
                bvs[k++]=HD[byte0&0xf];
            }
            return new String(bvs);
        }catch(NoSuchAlgorithmException e) {
            return null;
        }catch(UnsupportedEncodingException e) {
            return null;
        }
    }

    public static HashMap readindex (){                          //从暂存区反序列化出hashmap
        String path = System.getProperty("user.dir");           //获取当前工作区路径
        String path2 = path + "\\work";
        String path3 = path2 + "\\.git";
        HashMap index = null;
        try {
            File findex = new File(path3 + File.separator + "index");
            ObjectInputStream objindex = new ObjectInputStream(new FileInputStream(findex));
            index = (HashMap) objindex.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("文件未找到" + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO错误" + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("类调用错误" + e.getMessage());
        }
        return index;
    }
    public static void writeindex(HashMap map){           //把hashmap序列化进暂存区
        String path = System.getProperty("user.dir");           //获取当前工作区路径
        String path2 = path + "\\work";
        String path3 = path2 + "\\.git";
        try{
            File findex = new File(path3 + File.separator + "index");
            ObjectOutputStream objindex = new ObjectOutputStream(new FileOutputStream(findex));
            objindex.writeObject(map);
            objindex.flush();
            objindex.close();
        }catch (FileNotFoundException e) {
            System.out.println("文件未找到" + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO错误" + e.getMessage());
        }
    }

    public static void writeblob(String name,String info){              //创建blob文件
        String path = System.getProperty("user.dir");           //获取当前工作区路径
        String path2 = path + "\\work";
        String path3 = path2 + "\\.git";
        String path4= path3 + "\\objects";
        try{
            ObjectOutputStream wblob = new ObjectOutputStream(new FileOutputStream(new File(path4 + File.separator + name)));
            Blob b = new Blob(info);
            wblob.writeObject(b);
            wblob.flush();
            wblob.close();
        } catch (FileNotFoundException e) {
            System.out.println("文件未找到" + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO错误" + e.getMessage());
        }
    }

    public static void delfile(String path){
        File f = new File(path);
        f.delete();
    }

 }