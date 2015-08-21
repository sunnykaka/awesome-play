package common.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lidujun on 15-6-9.
 */
public class FileUtils {
    /**
     * 下载远程文件并保存到本地
     * @param remoteFilePath 远程文件路径
     * @param localFilePath 本地文件路径
     * 抛出异常说明文件下载出错
     */
    public static void downloadRemotFile(String remoteFilePath, String localFilePath) throws Exception {
        URL urlfile = null;
        HttpURLConnection httpUrl = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        File f = new File(localFilePath);
        if(f.exists()) {
            f.delete();
        }
        try {
            urlfile = new URL(remoteFilePath);
            httpUrl = (HttpURLConnection)urlfile.openConnection();
            httpUrl.connect();
            bis = new BufferedInputStream(httpUrl.getInputStream());
            bos = new BufferedOutputStream(new FileOutputStream(f));
            int len = 2048;
            byte[] b = new byte[len];
            while ((len = bis.read(b)) != -1) {
                bos.write(b, 0, len);
            }
            bos.flush();
            httpUrl.disconnect();
        } finally {
            try {
                bis.close();
            } catch (IOException e) {
                play.Logger.error("关闭bis流失败",e);
            }
            try {
                bos.close();
            } catch (IOException e) {
                play.Logger.error("关闭bos流失败", e);
            }
        }
    }

    /**
     * 按指定编码按行读取txt文件，并将文件内容按照行保存在map中
     * 建议在文件比较少时使用此方法，此方法现在只用来读取汇率文件 lidujun
     * @param localFilePath
     * @param encoding
     *  抛出异常说明解析文件出错
     */
    public static Map<Integer,String> readTxtFileByLine(String localFilePath, String encoding) throws Exception {
        Map<Integer,String> retMap = new HashMap<Integer,String>();
        File file = new File(localFilePath);
        InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);//考虑到编码格式
        BufferedReader bufferedReader = new BufferedReader(read);
        String lineTxt = null;
        int count = 0;
        while((lineTxt = bufferedReader.readLine()) != null){
            retMap.put(++count, lineTxt);
        }
        read.close();
        return retMap;
    }
}
