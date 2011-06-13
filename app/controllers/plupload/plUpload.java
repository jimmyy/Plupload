/**
 * 
 * Rewriting of the upload.php file in java
 *
 * PlayFramework version 1.2.1
 *
 * @package    controllers.upload
 * @author     Jérémie Robert <mystheme@free.fr>
 * @see        plupload
 * 
 */
package controllers.plupload;

import java.io.File;
import java.util.*;
import play.*;
import play.mvc.*;
import play.mvc.Http.Response;

public class Plupload extends Controller {

    static String targetDir = Play.configuration.getProperty("plupload.uploadDirectory", "../uploads/");

    public static void index() {
        render();
    }

    public static void dump() {
        int count = 0;
        Map<String, String[]> map = params.all();
        render(map);
    }

    public static String upload(String name, File file) {
        Response response = Response.current();
        response.setHeader("Server", "Web Server");
        response.setHeader("Expires", "Mon, 26 Jul 1997 05:00:00 GMT");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        int chunk = (params.get("chunk") != null) ? Integer.valueOf(params.get("chunk")) : 0;
        String chunks = params.get("chunks");
        String filename = (params.get("name") != null) ? params.get("name") : "";

        System.out.println("chunk : " + chunk);
        System.out.println("chunks : " + chunks);
        System.out.println("filename : " + filename);

        // Clean the fileName for security reasons
        filename = filename.replaceAll("/[^\\w\\._]+/", "");

        // Make sure the fileName is unique but only if chunking is disabled
        File f = new File(targetDir.concat(System.getProperty("file.separator")).concat(filename));
        if (chunk < 2 && f.exists()) {
            int ext = filename.lastIndexOf(".");
            String fileName_a = filename.substring(0, ext);
            String fileName_b = filename.substring(ext);
            File test = new File(targetDir.concat(System.getProperty("file.separator")).concat(fileName_a).concat("_").concat(fileName_b));
            int count = 1;
            while (test.exists()) {
                count++;
            }
            filename = fileName_a + "_" + count + fileName_b;
            System.out.println("filename : " + filename);
        }
        // Create target dir
        File t = new File(targetDir);
        if (!t.exists()) {
            t.mkdir();
        }

        // Look for the content type header
        String contentType = response.getHeader("Content-Type");
        System.out.println("contentType : " + contentType);
        // Handle non multipart uploads older WebKit versions didn't support multipart in HTML5
        if (contentType != null && contentType.contains("multipart") != false) {
            // Open temp file
            File out = new File(targetDir.concat(System.getProperty("file.separator")).concat(filename));
            if (out != null) {
                // Read binary input stream and append it to temp file
                // $in = fopen($_FILES['file']['tmp_name'], "rb");
                if (file != null) {
                    System.out.println("rename " + file.getAbsolutePath() + " to " + out.getAbsolutePath());
                    file.renameTo(out);
                } else {
                    System.out.println("{\"jsonrpc\" : \"2.0\", \"error\" : {\"code\": 101, \"message\": \"Failed to open input stream.\"}, \"id\" : \"id\"}");
                    return "{\"jsonrpc\" : \"2.0\", \"error\" : {\"code\": 102, \"message\": \"Failed to open output stream.\"}, \"id\" : \"id\"}";
                }

            } else {
                System.out.println("{\"jsonrpc\" : \"2.0\", \"error\" : {\"code\": 102, \"message\": \"Failed to open output stream.\"}, \"id\" : \"id\"}");
                return "{\"jsonrpc\" : \"2.0\", \"error\" : {\"code\": 102, \"message\": \"Failed to open output stream.\"}, \"id\" : \"id\"}";
            }

        } else {
            // Open temp file
            File out = new File(targetDir.concat(System.getProperty("file.separator")).concat(filename));
            // $out = fopen($targetDir.DIRECTORY_SEPARATOR.$fileName, $chunk == 0 ? "wb" : "ab");
            if (out != null) {
                if (file != null) {
                    System.out.println("rename " + file.getAbsolutePath() + " to " + out.getAbsolutePath());
                    file.renameTo(out);
                } else {
                    System.out.println("{\"jsonrpc\" : \"2.0\", \"error\" : {\"code\": 101, \"message\": \"Failed to open input stream.\"}, \"id\" : \"id\"}");
                    return "{\"jsonrpc\" : \"2.0\", \"error\" : {\"code\": 101, \"message\": \"Failed to open input stream.\"}, \"id\" : \"id\"}";
                }

            } else {
                System.out.println("{\"jsonrpc\" : \"2.0\", \"error\" : {\"code\": 102, \"message\": \"Failed to open output stream\"}, \"id\" : \"id\"}");
                return "{\"jsonrpc\" : \"2.0\", \"error\" : {\"code\": 102, \"message\": \"Failed to open output stream\"}, \"id\" : \"id\"}";
            }
        }

        // Return JSON-RPC response
        System.out.println("{\"jsonrpc\" : \"2.0\", \"result\" : null, \"id\" : \"id\"}");
        return "{\"jsonrpc\" : \"2.0\", \"result\" : null, \"id\" : \"id\"}";
    }
}