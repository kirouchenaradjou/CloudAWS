package com.csye6255.web.application.fall2018.utilities;

import java.io.*;
import java.net.URL;

public class AttachmentUtility {
    public static boolean devDownloadImage(String sourceUrl) throws FileNotFoundException, IOException {

        URL url = new URL(sourceUrl);
        String fileName = url.getFile();
        String basePath = "/home/menitakoonani/csye6225/app/csye6225-fall2018/webapp/src/main/java/com/csye6255/web/application/fall2018/images/";
        String destName = basePath + fileName.substring(fileName.lastIndexOf("/") + 1);

        InputStream is = url.openStream();
        OutputStream os = new FileOutputStream(destName);

        byte[] b = new byte[2048];
        int length;

        while ((length = is.read(b)) != -1) {
            os.write(b, 0, length);
        }

        is.close();
        os.close();

        return true;
    }
}
