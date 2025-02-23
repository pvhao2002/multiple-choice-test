package com.app.quizzservice.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.java.Log;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.logging.Level;

@Log
@UtilityClass
public class ImageUtils {
    public String getBase64FromImageUrl(String url) {
        try {
            var imageUrl = URI.create(url).toURL();
            var ucon = imageUrl.openConnection();
            var is = ucon.getInputStream();
            var baos = new ByteArrayOutputStream();
            var buffer = new byte[1024];
            int read;
            while ((read = is.read(buffer, 0, buffer.length)) != -1) {
                baos.write(buffer, 0, read);
            }
            baos.flush();
            return Base64Utils.encodeImage(baos.toByteArray());
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error while converting image to base64: {0}", e.getMessage());
        }
        return null;
    }
}
