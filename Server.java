package geet;

import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.util.Scanner;

import geet.zip;

public class Server{
    public static void main(String[] args){
        ServerSocket socket1;
        try {
            socket1 = new ServerSocket(8888);
            Socket socket2 = socket1.accept();
            System.out.println("开始监听，端口号: 8888");
            System.out.println("本机IP地址：127.0.0.1");
            System.out.println("远程仓库路径：D:\\java_code\\random\\src\\ServerFile\\" );
            InputStream inps = socket2.getInputStream();           //输入输出流
            OutputStream outps = socket2.getOutputStream();
            //先接收命令类型
            DataInputStream readcommand = new DataInputStream(inps);
            String command = readcommand.readUTF();
            //如果是push就执行push操作
            if (command.equals("push")) {
                System.out.println("接收到push命令");
                push(inps, outps);
            } else if (command.equals("pull")) {
                System.out.println("接收到pull命令");
                pull(inps, outps);
            } else {
                System.out.println("请求错误");
            }
        }catch (BindException e){
            System.out.println("端口已被占用 " + e.getMessage());
        }catch (IOException e) {
            System.out.println("IO错误 " + e.getMessage());
        }
    }

    static void push(InputStream inps,OutputStream outps){
        try {
            //读取文件内容
            ByteArrayOutputStream baops = new ByteArrayOutputStream();
            byte b1[] = new byte[1024];
            int readlen;
            while ((readlen = inps.read(b1,0,b1.length)) != -1) {           //用一个字节数组循环读入输入流
                baops.write(b1, 0, readlen);
            }
            byte[] b2 = baops.toByteArray();
            //写文件
            File serverf = new File("D:\\java_code\\random\\src\\geet\\ServerFile" + File.separator + "savefile.zip");
            FileOutputStream optserverf = new FileOutputStream(serverf);
            optserverf.write(b2);
            optserverf.flush();
            System.out.println("已成功接收文件存入仓库");
            //解压到远程仓库
            zip.jieya("D:\\java_code\\random\\src\\geet\\ServerFile\\savefile.zip","D:\\java_code\\random\\src\\geet\\ServerFile\\work\\");
            System.out.println("已经解压");
            //执行reset克隆一个工作区
            File freset = new File("D:\\java_code\\random\\src\\geet\\ServerFile\\work\\.git\\HEAD");
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
            File ftd = new File("D:\\java_code\\random\\src\\geet\\ServerFile\\work\\save.zip");
            if(!ftd.exists()){
                System.out.println("文件不存在");
            }
            System.out.println("文件存在，删除");
            ftd.delete();
            serverf.delete();
            optserverf.close();
        }catch (IOException e) {
            System.out.println("IO错误" + e.getMessage());
            throw new RuntimeException(e);
        }catch (RuntimeException e){
            System.out.println("超时" + e.getMessage());
        }
    }

    static void pull(InputStream inps,OutputStream outps){
        try{
            //再读取文件内容
            File f = new File("D:\\java_code\\random\\src\\geet\\ServerFile" + File.separator + "savefile.zip");
            if (f.exists()) {
                System.out.println("远程仓库找到所需文件");
                FileInputStream fip = new FileInputStream(f);

                byte b2[] = new byte[1024];
                int readlen;
                while ((readlen = fip.read(b2,0,b2.length)) != -1) {           //用一个字节数组循环读入输入流
                    outps.write(b2, 0, readlen);                  //把读到的内容发送给客户端
                }
                outps.flush();
                outps.close();
                System.out.println("远程仓库已传回");
            }else {
                System.out.println("远程仓库未找到备份");
            }
        }catch (FileNotFoundException a){
            System.out.println("文件未找到" + a.getMessage());
        }catch (IOException e){
            System.out.println("IO错误" + e.getMessage());
        }
    }
}