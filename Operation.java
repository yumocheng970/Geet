package geet;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.*;

public class Operation{             //操作合集 包括init，add，commit，reset，log和rm
    static void Init() {
        try {
            String path = System.getProperty("user.dir");    //获取路径
            String path2 = path + "\\work";
            File folder = new File(path2 + File.separator + ".git");  //创建.git文件夹对象
            if (folder.exists())
                System.out.println(".git文件夹已存在");           //存在便报错
            else {
                if(folder.mkdir()){//不存在则创建.git文件夹
                    System.out.println(".git文件夹创建成功");
                }
            }
            String path3 = path2 + "\\.git";
            if(new File(path3 + File.separator + "objects").mkdirs()) {       // .git文件夹下创建objects文件夹
                System.out.println("objects文件夹创建成功");
            }
            HashMap<String, String> hashnullMap = new HashMap<>();       //在index中维护一个hashmap对象,目前为空
            Utils.writeindex(hashnullMap);                                      //创建空index文件//将空hashmap对象序列化进index
            FileOutputStream hops = new FileOutputStream(path3 + "\\HEAD");          //head output stream 创建空head文件
            hops.flush();
            hops.close();      //关流
        } catch (IOException ex) {
            System.out.println("读入输出错误" + ex.getMessage());
        }
    }
    static void Addadoc(String name){                             //add单个文件
        String path = System.getProperty("user.dir");           //获取当前工作区路径
        String path2 = path + "\\work";
        String path3 = path2 + "\\.git";
        String path4= path3 + "\\objects";
        try {
            File fff = new File(path2 + File.separator+ name);
            if(fff.exists()) {                                                 //判断要add的文件是否存在，存在就进行add操作，不存在就去114行判断index里存不存在该文件条目
                FileInputStream fips = new FileInputStream(fff);                //输入流读取要add的文件内容
                ByteArrayOutputStream baops = new ByteArrayOutputStream();
                byte[] b1 = new byte[1024];
                int readlen;
                while ((readlen = fips.read(b1)) != -1) {           //用一个字节数组循环读入输入流
                    baops.write(b1, 0, readlen);
                }
                byte[] b2 = baops.toByteArray();
                String contxt = new String(b2);                 //转化成字符串
                String filename = Utils.SHA1(contxt);             //获得文件名
                Utils.writeblob(filename,contxt);                   //创建blob文件并写入内容

                HashMap<String, String> hashblobMap = new HashMap<>();       //将工作区文件名和内容的哈希值存入hashmap
                hashblobMap.put(name, filename);
                System.out.println("暂存区:"+ "\n" + hashblobMap);
                Utils.writeindex(hashblobMap);                   //最后再把hashmap序列化进暂存区
            }else{                                                                         //命令行里add的文件不存在工作区时，就查看index里是否有对应条目，有就删除，没有就不变
                System.out.println("要add的文件 " + name +  " 不存在");
                HashMap hm = Utils.readindex();                     //从index里反序列化出Hashmap
                System.out.println("原暂存区:"+ "\n" + hm);
                if(hm.containsKey(name)) {                                            //若该文件条目存在便将其移出hashmap
                    hm.remove(name);
                    System.out.println("暂存区中存在此条目，即将删除");
                    System.out.println("现暂存区:" + "\n" + hm);
                }
                Utils.writeindex(hm);                 //最后再把hashmap序列化回去
            }
        }catch (FileNotFoundException a){
            System.out.println("文件未找到" + a.getMessage());
        }catch (IOException ex){
            System.out.println("IO错误" + ex.getMessage());
        } catch (RuntimeException e ){
            System.out.println( "超时 "+ e.getMessage());
        }
    }
    public static void addall() {                       //add .操作，把工作区的全部文件都add
        String path = System.getProperty("user.dir");           //获取当前工作区路径
        String path2 = path + "\\work";
        String path3 = path2 + "\\.git";
        String path4= path3 + "\\objects";
        HashMap<String,String> multimap = new HashMap<String,String>();   //hashmap
        try {
            File fwork = new File(path2);                                //遍历路径下所有文件
            File[] flist = fwork.listFiles();
            for (File eachfile : flist) {                           //对每一个文件
                if(eachfile.isFile()) {
                    String fn = eachfile.getName();                     //获取名字
                    File nf = new File(path2 + File.separator + fn);
                    FileInputStream flip = new FileInputStream(nf);
                    String filecon = "";
                    int readingnumber = flip.read();
                    while (readingnumber != -1) {                        //读取文件内容
                        filecon += (char) readingnumber;
                        readingnumber = flip.read();
                    }
                    flip.close();
                    String hofc = Utils.SHA1(filecon);                         //hash of file content 文件内容对应的hash值
                    multimap.put(fn, hofc);                              //file name和hash值匹配成键值对存入hashmap
                    Utils.writeblob(hofc,filecon);                                  //创建并写入blob文件
                }
            }
            System.out.println("暂存区:" + "\n" +multimap);
            //blob文件创建完成
            //----------
            //将维护的hashmap对象写入index文件
            Utils.writeindex(multimap);
        }catch (FileNotFoundException a){
            System.out.println("文件未找到" + a.getMessage());
        }catch (IOException b){
            System.out.println("读入错误" + b.getMessage());
        }
    }

    static void Commit(String commitinfo) {          //提交操作
        String path = System.getProperty("user.dir");           //获取当前工作区路径
        String path2 = path + "\\work";
        String path3 = path2 + "\\.git";
        String path4= path3 + "\\objects";

        HashMap readmap = Utils.readindex();        //反序列化index对象，读进一个hashmap中
        try {
            //Tree的构建
            Set<String> set=readmap.keySet();                       //获取hashmap的key和value
            String[] keys= set.toArray(new String[set.size()]);
            Arrays.sort(keys);
            String content="";
            for(String key:keys){
                content+=key + " ";
                content+=readmap.get(key) + " ";  //获取hashmap的内容
            }
            long length = content.length();//tree内容的长度
            String size = String.valueOf(length);
            String treename = Utils.SHA1(content);                   //获取tree文件的文件名
            Tree ter =new Tree(size,readmap);       //构建tree对象
            File ftree = new File(path4 + File.separator + treename);  //构造tree文件
            FileOutputStream treeoutput = new FileOutputStream(ftree);
            ObjectOutputStream treeobjop = new ObjectOutputStream(treeoutput);              //对象io流 tree object output
            treeobjop.writeObject(ter);                    // 序列化进tree文件中
            treeobjop.flush();
            treeobjop.close();   // 关流
            //tree部分完成
            //----------
            //commit文件部分
            File fhead = new File(path3 + File.separator + "HEAD");      //找到head文件
            Scanner rhead = new Scanner(fhead);
            String parentLT;                                                //读出上次的commit id
            if(rhead.hasNext()){
                parentLT = rhead.nextLine();
            }else{
                parentLT = " ";
            }
            String conIdTT = treename;                                                  //本次commit的id即根树的hash值
            String commitauthor = "author Kal ";                                       //commit信息
            String committor = "committer El ";                                       //设定author和committer
            String now = Utils.getcurrenttime();                                        //commit时的准确时间

            String commitcontent = "Tree " + treename + "\n" + commitauthor + committor + now + commitinfo + now;   //commit文件的内容

            String comcontsize = String.valueOf(commitcontent.length());                  //计算即将创建的commit文件的长度
            String commitname = Utils.SHA1(commitcontent);                                  //获取commit文件名，即内容的hash值
            Commit comm = new Commit(parentLT,comcontsize,treename,commitauthor,now,committor,commitinfo);       //创建commit对象
            File fcom = new File(path4 + File.separator + commitname);                   //创建commit文件对象file commit
            FileOutputStream fcomops = new FileOutputStream(fcom);                     //file commit output stream
            ObjectOutputStream objcom = new ObjectOutputStream(fcomops);               //文件输出流和对象输出流
            objcom.writeObject(comm);                                      //commit对象写入commit文件
            objcom.flush();
            objcom.close();
            fcomops.flush();
            fcomops.close();

            //commit文件部分完成
            //-------------
            //打印commit变动情况

            HashMap<String,String> lastindex = Utils.getlastindex();                //获取上次的暂存区
            if(lastindex != null) {                                             //如果不是第一次commit，之前的暂存区，不为空，就进行比较
                int add = 0;                                                             //准备计数
                int modify = 0;
                int delete = 0;
                Set<String> lastset = lastindex.keySet();       //获取两个hashmap的key，各自生成一个文件名数组
                Set<String> thisset = readmap.keySet();
                String[] lkeys = lastset.toArray(new String[lastset.size()]);
                String[] tkeys = thisset.toArray(new String[thisset.size()]);
                Arrays.sort(lkeys);
                Arrays.sort(tkeys);
                for (int i = 0; i < lkeys.length; i++) {                           //循环判断比较两个文件名数组
                    for (int j = 0; j < tkeys.length; j++) {
                        if (lkeys[i].equals(tkeys[j])) {                      //对于两个数组里都有的文件
                            if (!lastindex.get(lkeys[i]).equals(readmap.get(tkeys[j]))) {            //比较文件内容的Hash值
                                modify = modify + 1;                                                    //文件名相同但内容不同，说明该文件修改过
                            }
                        }

                    }
                    if (!readmap.containsKey(lkeys[i])) {                                     //上次有的文件这次没有，表明该文件被删除了
                        delete = delete + 1;
                    }
                }


                for (int k = 0; k < tkeys.length; k++){
                    if (!lastindex.containsKey(tkeys[k])) {                               //本次有的文件上次没有，表明该文件是新增的
                        add = add + 1;
                    }
                }

                System.out.println("本次相比上次commit，添加了" + add + "个文件，删除了" + delete + "个文件，修改了" + modify + "个文件");       //打印出变动情况
            }else{                                                          //如果是第一次commit，直接数出本次提交的文件数
                Set<String> thisset = readmap.keySet();
                String[] tkeys = thisset.toArray(new String[thisset.size()]);
                System.out.println("本次是第一次commit，添加了" + tkeys.length + "个文件");
            }
            //变动情况打印完成
            //HEAD部分
            FileOutputStream wHEAD = new FileOutputStream(fhead);                   //把本次commit id写入head文件
            wHEAD.write(commitname.getBytes());
            wHEAD.flush();
            wHEAD.close();
            //HEAD写入完成
        } catch (FileNotFoundException ex) {
            System.out.println("文件未找到" + ex.getMessage());
        } catch (IOException e) {
            System.out.println("IO错误" + e.getMessage());
        }
    }

    public static void rm1(String todel)  {                          //rm --cache操作，即只删除暂存区条目
        String path = System.getProperty("user.dir");           //获取当前工作区路径
        String path2 = path + "\\work";
        String path3 = path2 + "\\.git";
        String path4= path3 + "\\objects";
        HashMap rmhm = Utils.readindex();           //从index文件里反序列化出维护的hashmap //remove hashmap
        try {
            System.out.println("原暂存区:" + "\n" + rmhm);
            if (!rmhm.containsKey(todel)){                           //判断暂存区中是否有对应条目
                System.out.println("暂存区中不存在该条目");
            }else{
                rmhm.remove(todel);                                     //删除hashmap中传入相应的键值对
                System.out.println("暂存区中存在该条目，已删除");
            }
            System.out.println("暂存区:" + "\n" + rmhm);
            Utils.writeindex(rmhm);                                         //再重新序列化回index中
        } catch (RuntimeException e){
            System.out.println("超时" + e.getMessage());
        }
    }

    public static void rm2(String docutodel){                    //在cached的基础上删除工作区文件---rm
        String path = System.getProperty("user.dir");           //获取当前工作区路径
        String path2 = path + "\\work";
        String path3 = path2 + "\\.git";
        String path4= path3 + "\\objects";
        try {
            File f = new File(path2 + File.separator + docutodel);
            if (f.exists()){                    //判断该文件是否存在
                rm1(docutodel);                //暂存区删除条目
                f.delete();                   //删除工作区文件
                System.out.println("删除工作区文件: " + docutodel);
            }else {
                System.out.println("工作区不存在要删除的文件");
                rm1(docutodel);
            }
        } catch (RuntimeException rte){
            System.out.println("超时 " + rte.getMessage());
        }
    }

    public static void log(){                                   //获取历次commit的信息
        String path = System.getProperty("user.dir");           //获取当前工作区路径
        String path2 = path + "\\work";
        String path3 = path2 + "\\.git";
        String path4= path3 + "\\objects";
        try {
            File fhead = new File(path3 + File.separator + "HEAD");   // file head
            Scanner shead = new Scanner(fhead);      //scan head
            String parentLT;                //读取HEAD文件里的上次提交的commit id
            String idcomlt;                 //id commit last time
            if(shead.hasNext()){                      //非首次提交时head里不为空
                parentLT = shead.next();                //读出HEAD里的上次commit id
                do{
                    FileInputStream readcomm  = new FileInputStream(path4 + File.separator + parentLT );   //找到上次的commit文件
                    ObjectInputStream objreadcomm = new ObjectInputStream(readcomm);
                    Commit rcomm = (Commit)objreadcomm.readObject();            //反序列化读出commit文件里的内容//找出里面需要的信息
                    idcomlt = rcomm.getPid();                                   //id committed last time
                    String idcomtt = parentLT;                                  //id committed this time
                    String tcomm = rcomm.getComtime();                          //time of committing
                    String messcomm = rcomm.getCommess();                       //message of committing
                    //打印出需要的信息
                    System.out.println("commit信息按时间倒叙：");
                    System.out.println("commit id last time : " + idcomlt);
                    System.out.println("commit id: " + idcomtt + "\n"+ "committing time: " + tcomm+ "\n" + "committing message: " + messcomm);
                    parentLT = idcomlt;
                    System.out.println(" ");
                }while(!idcomlt.equals(" "));                    //循环去找更前一次的commit文件直至没有更早的commit id
                System.out.println("已全部打印完成");
            }else{                                          //若head为空，说明是首次提交，之前没有记录
                parentLT = " ";
                System.out.println("HEAD文件为空，无更早提交记录");
            }
        }catch (FileNotFoundException e){
            System.out.println("文件未找到" + e.getMessage());
        }catch (IOException s){
            System.out.println("文件读入或输出错误" + s.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }catch (RuntimeException e){
            System.out.println("运行超时" + e.getMessage());
        }
    }

    public static void resetsoftly(String resetid){             //只把HEAD文件重置
        String path = System.getProperty("user.dir");           //获取当前工作区路径
        String path2 = path + "\\work";
        String path3 = path2 + "\\.git";
        String path4= path3 + "\\objects";
        try {
            File ftores = new File(path4 + File.separator + resetid);       //file to reset 确认是否有对应的commit文件
            if (!ftores.exists()) {                             //若不存在则提示
                System.out.println("仓库中不存在此commit记录");
            } else {
                File fhead = new File(path3 + File.separator + "HEAD");     //file of head 存在则写入HEAD文件里
                FileOutputStream fophead = new FileOutputStream(fhead);                 //file output of head
                fophead.write(resetid.getBytes());
                fophead.flush();
                fophead.close();
            }
        }catch (FileNotFoundException e){
            System.out.println("文件未找到" + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }catch (RuntimeException e){
            System.out.println("超时" + e.getMessage());
        }
    }

    public static void resethardly(String resetinfo) {          //复原HEAD文件，暂存区和工作区
        String path = System.getProperty("user.dir");           //获取当前工作区路径
        String path2 = path + "\\work";
        String path3 = path2 + "\\.git";
        String path4= path3 + "\\objects";
        resetsoftly(resetinfo);                           //在soft
        resetmixed(resetinfo);          //和mixed的基础上// 复原工作区文件
        try {
            HashMap readmap = Utils.readindex();                     //从暂存区反序列化出hashmap
            Set<String> set = readmap.keySet();                       //获取hashmap的key和value
            String[] keys = set.toArray(new String[set.size()]);
            Arrays.sort(keys);
            String name = "";
            String hash = "";
            //把暂存区里的文件复现回工作区
            for(String key:keys){                                           //对于hashmap里的每一对文件名和hash值
                name = key;
                hash = (String) readmap.get(key);
                File fblob = new File(path4 + File.separator + hash);   //先找到hash值对应的blob文件 file blob
                ObjectInputStream objblob = new ObjectInputStream(new FileInputStream(fblob));  //object blob
                Blob blobcontent = (Blob) objblob.readObject();                //反序列化读出blob文件里的内容
                String content = blobcontent.getContent();                              //找到工作区文件内容
                objblob.close();

                File f = new File(path2 + File.separator + name);           //以hashmap的key为文件名在工作区中创建文件
                FileOutputStream wfile = new FileOutputStream(f);               //write file
                wfile.write(content.getBytes());                                //将从blob文件里读出的文件内写入工作区的文件
                wfile.flush();
                wfile.close();
            }
            //再把不在暂存区的hashmap里的文件删除
            File fwork = new File(path2);                                //遍历路径下所有文件
            File[] flist = fwork.listFiles();
            for (File eachfile : flist) {                           //对每一个文件
                if (eachfile.isFile()) {
                    String fn = eachfile.getName();                     //获取名字 file name
                    if(!readmap.containsKey(fn)){                       //如果文件名没有存在要reset的index里
                        File ftodel = new File(path2 + File.separator + fn);        //file to delete
                        ftodel.delete();                                   //删除文件
                    }
                }
            }
            System.out.println("已重置工作区文件与暂存区一致");
            //工作区成功重置
        }catch (FileNotFoundException e){
            System.out.println("文件未找到" + e.getMessage());
        }catch (IOException e){
            System.out.println("IO错误" + e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }catch (RuntimeException e){
            System.out.println("超时 " + e.getMessage());
        }
    }

    public static void resetmixed(String resetinfo){            //只复原HEAD文件和暂存区
        String path = System.getProperty("user.dir");           //获取当前工作区路径
        String path2 = path + "\\work";
        String path3 = path2 + "\\.git";
        String path4= path3 + "\\objects";
        resetsoftly(resetinfo);//在soft的基础上
        String treeid = Utils.gettree(resetinfo);         //从commit文件里读出tree的id
        HashMap<String,String> resetmap = Utils.getindex(treeid);    //从tree id里读出Hashmap
        try {
            System.out.println("新的index内容: " + resetmap);
            //把生成的hashmap序列化进index
            Utils.writeindex(resetmap);                    //把得到的hashmap序列化进index文件 //object index
        } catch (RuntimeException e){
            System.out.println("超时" + e.getMessage());
        }
    }
}