package geet;

import java.io.File;

public class geet {
    public static void main(String[] args){
        if (args.length == 0) {
            System.out.print("重新输入命令");
        } else {
            if (args[0].equals("init")) {
                System.out.println("init执行中");
                Operation.Init();
            } else if (args[0].equals("add")) {
                if (args[1].equals(".")) {
                    System.out.println("add全部文件");
                    Operation.addall();
                } else {
                    System.out.println("add执行中");
                    Operation.Addadoc(args[1]);
                }
            } else if (args[0].equals("commit")) {
                if(args[1].equals("-m")) {
                    System.out.println("commit执行中");
                    Operation.Commit(args[2]);
                }else{
                    System.out.println("commit命令格式错误，请检查格式重新输入");
                }
            } else if (args[0].equals("reset")) {
                System.out.println("reset执行中");
                if (args[1].equals("soft")) {
                    Operation.resetsoftly(args[2]);
                } else if (args[1].equals("hard")) {
                    Operation.resethardly(args[2]);
                } else {
                    if (args[1].equals("mixed")) {
                        Operation.resetmixed(args[2]);
                    } else {
                        Operation.resetmixed(args[1]);
                    }
                }
            } else if (args[0].equals("rm")) {
                if (args[1].equals("--cached")) {
                    System.out.println("rm-cached执行中");
                    Operation.rm1(args[2]);
                } else {
                    Operation.rm2(args[1]);
                    System.out.println("文件删除");
                }
            } else if (args[0].equals("log")) {
                System.out.println("log执行中");
                Operation.log();
            } else if (args[0].equals("push")) {
                System.out.println("push一下");
                Client.pushpush();
                String path = System.getProperty("user.dir");
                Utils.delfile(path + File.separator + "work" + File.separator + "save.zip");
            } else if (args[0].equals("pull")) {
                System.out.println("pull一下");
                Client.pullpull();
                String path = System.getProperty("user.dir");
                Utils.delfile(path + File.separator + "work" + File.separator + "save.zip");
            } else {
                System.out.println("命令错误，重新输入");
            }
        }
    }
}
