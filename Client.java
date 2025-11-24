package geet;

import java.net.Socket;
import java.io.*;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    public static void pushpush(){
        String path = System.getProperty("user.dir");           //获取当前工作区路径
        String path2 = path + "\\work";
        String path3 = path2 + "\\.git";
        String path4= path3 + "\\objects";
        try {
            Socket socket = new Socket("127.0.0.1", 8888);      // 指定要连接的服务器和接口
            OutputStream ops = socket.getOutputStream();                    //输入流和输出流
            InputStream ips = socket.getInputStream();
            //打印套接字
            System.out.println("连接127.0.0.1,端口8888 ");
            DataOutputStream dop = new DataOutputStream(ops);
            dop.writeUTF("push");
            //先告知服务器命令类型push
            dop.flush();
            //压缩工作区
            //仓库
            zip.yasuo(path3, path2 + File.separator + "save.zip");
            //传输文件
            File ftopush = new File(path2 + File.separator + "save.zip");
            if(ftopush.exists()) {
                //System.out.println("672");
                byte[] inb = new byte[1024];
                int len = 0;
                FileInputStream pushout = new FileInputStream(ftopush);
                //先读取文件再发送出去
                while ((len = pushout.read(inb, 0, inb.length)) != -1) {
                    ops.write(inb, 0, len);
                }
                pushout.close();
                FileInputStream f = new FileInputStream(ftopush);
                f.close();
                ftopush.delete(); //删除内容
                dop.close();
                ops.flush();
                ops.close();
            }else{
                System.out.println("文件不存在");
            }
        }catch (UnknownHostException e) {
            System.out.println("IP地址找不到 " + e.getMessage());
        }catch (FileNotFoundException ex){
            System.out.println("文件未找到" + ex.getMessage());
        }catch (IOException ex){
            System.out.println("IO错误" + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void pullpull(){
        String path = System.getProperty("user.dir");           //获取当前工作区路径
        String path2 = path + "\\work";
        String path3 = path2 + "\\.git";
        String path4= path3 + "\\objects";
        try {
            Socket socket = new Socket("127.0.0.1", 8888);
            // 指定要连接的服务器和接口
            OutputStream ops = socket.getOutputStream();                    //输入流和输出流
            InputStream ips = socket.getInputStream();
            //先告知服务器命令类型pull
            System.out.println("连接127.0.0.1,端口8888 ");
            DataOutputStream dop = new DataOutputStream(ops);
            dop.writeUTF("pull");
            dop.flush();
            Utils.deletefolder(path2);                                          //覆盖原工作区

            File fo = new File(path2);
            if(fo.mkdir()){
                System.out.println("覆盖工作区");
            }
            //输入流读取服务器发回的压缩文件
            ByteArrayOutputStream baops = new ByteArrayOutputStream();
            byte b1[] = new byte[1024];
            int readlen;
            while ((readlen = ips.read(b1,0,b1.length)) != -1) {           //用一个字节数组循环读入输入流
                baops.write(b1, 0, readlen);
            }
            byte[] b2 = baops.toByteArray();
            File f = new File(path2+ File.separator + "backupfile.zip");            //把压缩文件存入工作区
            FileOutputStream bfps = new FileOutputStream(f);
            bfps.write(b2);
            bfps.flush();
            //解压文件得到仓库.git文件夹
            File folder = new File(path2 + File.separator + ".git");
            folder.mkdirs();
            zip.jieya("D:\\java_code\\random\\src\\geet\\work\\backupfile.zip","D:\\java_code\\random\\src\\geet\\work\\.git\\");
            bfps.close();
            //读一下head里的id再执行reset hard复原工作区
            File freset = new File("D:\\java_code\\random\\src\\geet\\work\\.git\\HEAD");
            String id = null;
            if(freset.exists()) {
                Scanner in = new Scanner(freset);
                if(in.hasNext()) {
                    id = in.nextLine();
                }else{
                    System.out.println("头文件为空");
                }
                Operation.resethardly(id);
            }else{
                System.out.println("复原工作区失败");
            }
            dop.close();
        } catch (FileNotFoundException a){
            System.out.println("文件未找到" + a.getMessage());
        }catch (UnknownHostException e) {
            System.out.println("IP地址找不到 " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO错误 " + e.getMessage());
        }
    }
}