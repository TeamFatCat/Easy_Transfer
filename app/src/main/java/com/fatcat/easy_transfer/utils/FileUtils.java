package com.fatcat.easy_transfer.utils;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fatcat.easy_transfer.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * @author fatcat
 * @version $Rev$
 * @time 2016/4/2222:01
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class FileUtils {


    public static int[] Icons = new int[]{
            R.mipmap.ic_folder, R.mipmap.ic_music,
            R.mipmap.ic_media, R.mipmap.ic_picture,
            R.mipmap.ic_apk, R.mipmap.ic_zip,
            R.mipmap.ic_unkown, R.mipmap.ic_excel,
            R.mipmap.ic_pdf, R.mipmap.ic_ppt,
            R.mipmap.ic_text, R.mipmap.ic_word
    };

    public static final int SEND_FILE = 0;
    public static final int OPEN_FILE = 1;
    public static final int CHANGE_FILE_NAME = 2;
    public static final int DELETE_FILE = 3;
    public static final int COPY_FILE = 4;
    public static final int FILE_INFO = 5;


    final public static int TYPE_FOLDER = 0;
    final public static int TYPE_MUSIC = 1;
    final public static int TYPE_MEDIA = 2;
    final public static int TYPE_PICTURE = 3;
    final public static int TYPE_APK = 4;
    final public static int TYPE_ZIP = 5;
    final public static int TYPE_UNKOWN = 6;
    final public static int TYPE_DOUCMENT_EXCEL = 7;
    final public static int TYPE_DOUCMENT_PDF = 8;
    final public static int TYPE_DOUCMENT_PPT = 9;
    final public static int TYPE_DOUCMENT_TEXT = 10;
    final public static int TYPE_DOUCMENT_WORD = 11;

    public static int count;
    public static Long size;


    /*
           常量:
       */
    public final static String[] MUSIC = {"wav", "mp3", "flac", "wma", "ape"};
    public final static String[] VIDEO = {"rm", "rmvb", "wmv", "avi", "mp4", "3gp", "mkv", "mov"};
    public final static String[] DOCUMENT = {"txt", "doc", "excel", "ppt", "pdf"};
    public final static String[] BITMAP = {"bmp", "gif", "jpg", "pic", "png", "tif"};
    public final static String APK = "apk";
    public final static String[] RAR = {"rar", "zip", "7z"};


    public static DecimalFormat decimalFormat = new DecimalFormat("0.00");
    public static DecimalFormat decimalFormat_b = new DecimalFormat("0.000");
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");


    public static String getFileName(File file) {
        return file.getName();
    }

    public static String getFileType(File file) {
        if (file.isDirectory())
            return "文件夹";
        String string = file.getName();
        int i;
        for (i = string.length() - 1; i != -1; i--)
            if (string.charAt(i) == '.')
                break;

        return string.substring(i + 1) + "格式";
    }

    public static String getFileSize(File file) {
        if (file.isFile()) {
            if (file.length() / (1024 * 1024) > 1024)
                return (decimalFormat.format(file.length() / 1024.0 / 1024.0 / 1024.0)) + " GB  " + "(" + file.length() + "字节)";
            else if (file.length() / 1024 > 1024)
                return (decimalFormat.format(file.length() / 1024.0 / 1024.0)) + " MB  " + "(" + file.length() + "字节)";
            else if (file.length() > 1024)
                return (decimalFormat.format(file.length() / 1024.0)) + " KB  " + "(" + file.length() + "字节)";
            else
                return file.length() + " B";
        } else {
            count = 0;
            size = 0l;
            dfs(file);

            if (count == 0)
                return "空文件夹";
            else if (size / (1024 * 1024) > 1024)
                return (decimalFormat.format(size / 1024.0 / 1024.0 / 1024.0)) + " GB  " + "(" + count + "个文件)";
            else if (size / 1024 > 1024)
                return (decimalFormat.format(size / 1024.0 / 1024.0)) + " MB  " + "(" + count + "个文件)";
            else if (size > 1024)
                return (decimalFormat.format(size / 1024.0)) + " KB  " + "(" + count + "个文件)";
            else
                return size + " B" + "(" + count + "个文件)";
        }
    }


    private static void dfs(File f) {
        if (f.isFile()) {
            count++;
            size += f.length();
        } else {
            File[] files = f.listFiles();
            for (File file : files)
                dfs(file);
        }
    }


    public static String getPermission(File file) {
        return "\n是否可读:\t\t\t\t\t" + (file.canRead() ? "是" : "否") + "\n\n是否可写:\t\t\t\t\t" + (file.canWrite() ? "是" : "否") + "\n\n是否隐藏:\t\t\t\t\t" + (file.isHidden() ? "是" : "否");
    }


    public static String getlastModified(File file) {
        Date data = new Date(file.lastModified());
        return simpleDateFormat.format(data);
    }


    public static File getFilePath() {
        return Environment.getExternalStorageDirectory();
    }


    /*
    @param files :传入一个文件数组..
    @return 返回已过滤的文件数组..
 */
    public static File[] filtration(File[] files) {

        boolean[] f = new boolean[files.length];
        int size = 0;

        for (int i = 0; i < files.length; i++) {
            if (!files[i].getName().startsWith(".") && !files[i].getName().startsWith("com.") && !files[i].getName().startsWith("cn.")) {
                f[i] = true;
                size++;
            }
        }

        int n = 0;
        File[] res = new File[size];
        for (int i = 0; i < files.length; i++) {
            if (f[i]) {
                res[n++] = files[i];
            }
        }
        return res;
    }


    /**
     * 递归删除文件和文件夹
     *
     * @param file 要删除的根目录
     */
    public static boolean deleteFile(File file) {
        if (file.isFile()) {
            return file.delete();
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                return file.delete();
            }
            for (File f : childFile) {
                deleteFile(f);
            }
            file.delete();
        }
        return true;
    }


    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }



    /**
     * 复制整个文件夹内容
     *
     * @param oldPath String 原文件路径 如：c:/fqf
     * @param newPath String 复制后路径 如：f:/fqf/ff
     * @return boolean
     */
    public static void copyFolder(String oldPath, String newPath) {

        try {
            (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹
            File a = new File(oldPath);
            String[] file = a.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }

                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + "/" +
                            (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if (temp.isDirectory()) {//如果是子文件夹
                    copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
                }
            }
        } catch (Exception e) {
            System.out.println("复制整个文件夹内容操作出错");
            e.printStackTrace();

        }

    }


    //返回该文件的类型的图标
    public static int getIcons(File file) {
        int type = TYPE_UNKOWN;

        if (file.isDirectory()) {
            return TYPE_FOLDER;
        }

        String fileName = file.getName();

        String fileNameEnd = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();

        if (fileNameEnd.equals(DOCUMENT[0])) {
            return TYPE_DOUCMENT_TEXT;
        }

        if (fileNameEnd.equals(DOCUMENT[1])) {
            return TYPE_DOUCMENT_WORD;
        }

        if (fileNameEnd.equals(DOCUMENT[2])) {
            return TYPE_DOUCMENT_EXCEL;
        }

        if (fileNameEnd.equals(DOCUMENT[3])) {
            return TYPE_DOUCMENT_PPT;
        }

        if (fileNameEnd.equals(DOCUMENT[4])) {
            return TYPE_DOUCMENT_PDF;
        }


        for (String string : MUSIC) {
            if (fileNameEnd.equals(string)) {
                return TYPE_MUSIC;
            }
        }

        for (String string : MUSIC) {
            if (fileNameEnd.equals(string)) {
                return TYPE_MUSIC;
            }
        }

        for (String string : VIDEO) {
            if (fileNameEnd.equals(string)) {
                return TYPE_MEDIA;
            }
        }

        for (String string : BITMAP) {
            if (fileNameEnd.equals(string)) {
                return TYPE_PICTURE;
            }
        }

        if (fileNameEnd.equals(APK)) {
            return TYPE_APK;
        }

        for (String string : RAR) {
            if (fileNameEnd.equals(string)) {
                return TYPE_ZIP;
            }
        }

        return type;
    }


    //获取文件mimetype
    public static String getMIMEType(File file) {
        String type = "";
        String name = file.getName();
        //文件扩展名
        String end = name.substring(name.lastIndexOf(".") + 1, name.length()).toLowerCase();
        if (end.equals("m4a") || end.equals("mp3") || end.equals("wav")) {
            type = "audio";
        } else if (end.equals("mp4") || end.equals("3gp")) {
            type = "video";
        } else if (end.equals("jpg") || end.equals("png") || end.equals("jpeg") || end.equals("bmp") || end.equals("gif")) {
            type = "image";
        } else {
            //如果无法直接打开，跳出列表由用户选择
            type = "*";
        }
        type += "/*";
        return type;
    }

    //打开文件
    public static void openFile(File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        String type = FileUtils.getMIMEType(file);
        intent.setDataAndType(Uri.fromFile(file), type);
        UIUtils.getContext().startActivity(intent);
    }


    public static void setDialogFileInfo(LinearLayout linearLayout, File file) {

        ImageView icon_file_info = (ImageView) linearLayout.findViewById(R.id.dialog_icon_file_info);
        TextView name_file_info = (TextView) linearLayout.findViewById(R.id.dialog_name_file_info);
        TextView type_file_info = (TextView) linearLayout.findViewById(R.id.dialog_type_file_info);
        TextView path_file_info = (TextView) linearLayout.findViewById(R.id.dialog_path_file_info);
        final TextView size_file_info = (TextView) linearLayout.findViewById(R.id.dialog_size_file_info);
        TextView updatetime_file_info = (TextView) linearLayout.findViewById(R.id.dialog_updatetime_file_info);
        TextView permission_file_info = (TextView) linearLayout.findViewById(R.id.dialog_premission_file_info);

        icon_file_info.setImageResource(FileUtils.Icons[FileUtils.getIcons(file)]);
        name_file_info.setText(FileUtils.getFileName(file));
        type_file_info.setText("类型:\t\t\t  \t\t\t" + FileUtils.getFileType(file));
        path_file_info.setText("路径:\t\t\t  \t\t\t" + file.getPath().substring(FileUtils.getFilePath().getPath().length()));


        size_file_info.setText("大小:\t\t\t  \t\t\t" + FileUtils.getFileSize(file));


        updatetime_file_info.setText("修改时间:\t\t\t" + FileUtils.getlastModified(file));
        permission_file_info.setText("权限:\n" + FileUtils.getPermission(file));
    }


    public static class FileComparer implements Comparator<File> {
        @Override
        public int compare(File lhs, File rhs) {

            int i = 0;
            char o1_i;
            char o2_i;

            String o1 = lhs.getName();
            String o2 = rhs.getName();

            //都是路径
            if ((lhs.isDirectory() && rhs.isDirectory()) || (lhs.isFile() && rhs.isFile())) {
                i = lhs.getName().compareToIgnoreCase(rhs.getName());
                return i;
            }

            //一个文件一个路径
            if (lhs.isDirectory()) {
                i = -1;
                return i;
            }
            if (rhs.isDirectory()) {
                i = 1;
                return i;
            }

            while (true) {

                if (o1.charAt(i) >= 65 && o1.charAt(i) <= 90)
                    o1_i = (char) (o1.charAt(i) + 32);
                else
                    o1_i = o1.charAt(i);


                if (o2.charAt(i) >= 65 && o2.charAt(i) <= 90)
                    o2_i = (char) (o2.charAt(i) + 32);
                else
                    o2_i = o2.charAt(i);

                if (o1_i == o2_i && i < o1.length() - 1 && i < o2.length() - 1) {
                    i++;
                } else
                    break;
            }

            return o1_i - o2_i;
        }
    }

}
