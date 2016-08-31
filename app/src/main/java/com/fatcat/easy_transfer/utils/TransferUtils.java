package com.fatcat.easy_transfer.utils;

/**
 * Created by FatCat on 2016/8/8.
 */
public class TransferUtils {

    private static char[] big_char = new char[26];
    private static char[] small_char = new char[26];
    private static char[] number = new char[10];

    static {
        char a = 'z';
        char b = 'Z';
        char c = '9';
        for (int i = 0; i < 26; i++) {
            big_char[i] = a;
            small_char[i] = b;
            a--;
            b--;
        }
        for (int i = 0; i < 10; i++) {
            number[i] = c;
            c--;
        }
    }


    //计算并返回ip地址的字符串值
    public static String initToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF);
    }

    public static String convert(String origin) {
        int len = origin.length();
        char[] res = new char[len];
        for (int i = 0; i < len; i++) {
            char ch = origin.charAt(i);
            if (ch >= 'a' && ch <= 'z') {
                res[i] = small_char[ch - 'a'];
            } else if (ch >= 'A' && ch <= 'Z') {
                res[i] = big_char[ch - 'A'];
            } else if (ch >= '0' && ch <= '9') {
                res[i] = number[ch - '0'];
            } else {
                res[i] = ch;
            }
        }
        return new String(res);
    }

    public static String restore(String origin) {
        int len = origin.length();
        char[] res = new char[len];
        for (int i = 0; i < len; i++) {
            char ch = origin.charAt(i);
            if (ch >= 'a' && ch <= 'z') {
                res[i] = small_char[ch - 'a'];
            } else if (ch >= 'A' && ch <= 'Z') {
                res[i] = big_char[ch - 'A'];
            } else if (ch >= '0' && ch <= '9') {
                res[i] = number[ch - '0'];
            } else {
                res[i] = ch;
            }
        }
        return new String(res);
    }

}
