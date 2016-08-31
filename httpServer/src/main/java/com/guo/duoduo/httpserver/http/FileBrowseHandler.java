package com.guo.duoduo.httpserver.http;


import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.guo.duoduo.httpserver.utils.Constant;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;


/**
 * Created by Guo.Duo duo on 2015/9/5.
 */
public class FileBrowseHandler implements HttpRequestHandler {
    private static final String tag = FileBrowseHandler.class.getSimpleName();

    private String webRoot;
    private Context context;

    public FileBrowseHandler(String webRoot, Context context) {
        this.webRoot = webRoot;
        this.context = context;
    }

    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse,
                       HttpContext httpContext) throws HttpException, IOException {

        //String target = URLDecoder.decode(httpRequest.getRequestLine().getUri(),
                //Constant.ENCODING);

        //Log.d(tag, "http request target = " + target);

        //   target == /

        String path = null;
        try {
            path = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0).sourceDir;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //Log.i("123", "-----------" + path);

        final File file = new File(path);

        HttpEntity entity = new StringEntity("", Constant.ENCODING);

        String contentType = "text/html;charset=" + Constant.ENCODING;


        Log.d(tag, " file is real file");
        String mime = null;


        mime = Constant.MIME_DEFAULT_BINARY;

        long fileLength = file.length();
        httpRequest.addHeader("Content-Length", "" + fileLength);
        httpResponse.setHeader("Content-Type", mime);
        httpResponse.addHeader("Content-Description", "File Transfer");
        httpResponse.addHeader("Content-Disposition", "attachment;filename="
                + encodeFilename(file));
        httpResponse.setHeader("Content-Transfer-Encoding", "binary");

        entity = new EntityTemplate(new ContentProducer() {
            @Override
            public void writeTo(OutputStream outStream) throws IOException {
                write(file, outStream);
            }
        });

        httpResponse.setEntity(entity);
    }


    private void write(File inputFile, OutputStream outStream) throws IOException {
        FileInputStream fis = new FileInputStream(inputFile);
        try {
            int count;
            byte[] buffer = new byte[Constant.BUFFER_LENGTH];
            while ((count = fis.read(buffer)) != -1) {
                outStream.write(buffer, 0, count);
            }
            outStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            fis.close();
            outStream.close();
        }
    }

    private String encodeFilename(File file) throws IOException {
        String filename = URLEncoder.encode(getFilename(file), Constant.ENCODING);
        return filename.replace("+", "%20");
    }

    private String getFilename(File file) {
        return file.isFile() ? file.getName() : file.getName() + ".zip";
    }
}
