package geet;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class zip {                  //网上down的
    public static void main(String[] args) throws IOException {
        //解决中文乱码
        //压缩 参数改成你自己的源文件路径和压缩后的文件路径
        //yasuo("C:\file\", "C:\file.zip");
        //解压 参数改成你自己的源文件路径和解压后的文件路径
        //jieya("C:\\file.zip", "C:\\file\\");
    }

    public static void jieya(String zipPath, String path) throws IOException, FileNotFoundException {
        //创建解压后的文件夹
        File pt=new File(path.substring(0,path.length()-1));
        if(!pt.exists()) {
            pt.mkdirs();
        }
        //try(resource)来保证InputStream正确关闭
        try(ZipInputStream zip=new ZipInputStream(new FileInputStream(zipPath))){
            //ZipEntry表示一个压缩文件或目录
            ZipEntry entry;
            while((entry=zip.getNextEntry())!=null) {
                String name=entry.getName();
                //压缩文件
                if(!(entry.getName().contains(File.separator))) {
                    FileOutputStream file= new FileOutputStream( path+ name);
                    int n=0;
                    while((n=zip.read())!=-1) {
                        file.write(n);
                    }
                }else {
                    //目录
                    int index=name.lastIndexOf("\\");
                            File file= new File(path+ name.substring(0,index));
                    if(!file.exists()) {
                        file.mkdirs();
                    }
                    //如果不是空目录
                    if(index!=name.length()-1) {
                        FileOutputStream f= new FileOutputStream( path+ name);
                        int n=0;
                        while((n=zip.read())!=-1) {
                            f.write(n);
                        }
                    }
                }
            }
            zip.closeEntry();
        }
    }

    public static void yasuo(String path, String zipPath) throws IOException, FileNotFoundException {
        File zp=new File(zipPath);
        if(!zp.exists()) {
            zp.createNewFile();
        }

        try(ZipOutputStream zip=new ZipOutputStream(new FileOutputStream(zp))) {
            File files= new File(path);
            File[] f=files.listFiles();
            for (File file : f) {
                zipAll(zip, file,file.getName());
            }

        }
    }

    public static void zipAll(ZipOutputStream zip, File files,String name) throws IOException, FileNotFoundException {
        if(files.isDirectory()) {
            File[] files2=files.listFiles();
            if(files2.length==0||files2==null) {
                zip.putNextEntry(new ZipEntry(name+File.separator));
            }else{
                for (File file2 : files2) {
                    if(file2.isFile()) {
                        zip.putNextEntry(new ZipEntry(name+File.separator+file2.getName()));
                        int n;
                        FileInputStream input=new FileInputStream(file2);
                        while((n=input.read())!=-1) {
                            zip.write(n);
                        }
                    }
                    else {
                        zipAll(zip,file2,name+File.separator+file2.getName());
                    }
                }
            }
        }else {
            zip.putNextEntry(new ZipEntry(name));
            int n;
            FileInputStream input=new FileInputStream(files);
            while ((n=input.read())!=-1) {
                zip.write(n);
            }
        }
    }
}