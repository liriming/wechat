package com.iosre.pphb.util;

/**
 * Created by liriming on 2018/3/15.
 */

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.util.List;

public class FileUtils {

    // 输出TXT
    public static void writeToTxt(HttpServletResponse response, List<String> list) {
        response.setContentType("text/plain");// 一下两行关键的设置
        response.addHeader("Content-Disposition",
                "attachment;filename=" + list.size()  +".txt");// filename指定默认的名字
        BufferedOutputStream buff = null;
        StringBuffer write = new StringBuffer();
        String enter = "\r\n";
        ServletOutputStream outSTr = null;
        try {
            outSTr = response.getOutputStream();// 建立
            buff = new BufferedOutputStream(outSTr);
            for (int i = 0; i < list.size(); i++) {
                write.append(list.get(i));
                write.append(enter);
            }
            buff.write(write.toString().getBytes("UTF-8"));
            buff.flush();
            buff.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                buff.close();
                outSTr.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
