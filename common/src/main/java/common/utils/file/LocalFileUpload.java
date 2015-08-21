package common.utils.file;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

/**
 * 上传到服务器本地
 *
 * @auth amos
 * 15-4-9.
 */
public class LocalFileUpload implements FileUpload {


    private static final String DEFAULT_UPLOAD_ROOT = "/var/wwww/";
    /**
     * 网站的静态资源部分:js,css,图片等
     */
    private static final String DEFAULT_ASSERT_ROOT = DEFAULT_UPLOAD_ROOT + "/asserts";


    private static final String DEFAULT_PIC_ROOT = DEFAULT_UPLOAD_ROOT + "/images";

    /**
     * 默认目录结构 /home/admin/{year}/{month}/{day}
     */
    private static final String DEFAULT_PIC_DIRECTORY = DEFAULT_PIC_ROOT + "/%s/%s/%s/";




    /**
     * 上传到本地目录之前先判断是否需要创建文件目录
     *
     * @param type
     */
    private File createStoreDirectory(FileContentType type) {
        File file = null;
        /**
         * 如果是网站的内容部分,包括设计师,商品等,则存放目录为
         * /home/admin/{year}/{month}/{day}
         */
        if (type.equals(FileContentType.Content)) {
            Calendar c = Calendar.getInstance();

            int month = c.get(Calendar.MONTH) + 1;
            int day = c.get(Calendar.DATE);

            String yearStr = c.get(Calendar.YEAR) + "";
            String monthStr = month < 10 ? "0" + month : "" + month;
            String dayStr = day < 10 ? "0" + day : "" + day;

            file = new File(String.format(DEFAULT_PIC_DIRECTORY, yearStr, monthStr, dayStr));
        } else {
            file = new File(DEFAULT_ASSERT_ROOT);
        }

        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }


    public Optional<String> upload(File file, FileContentType type) {
        File directory = createStoreDirectory(type);
        String originalFileName = file.getName();
        String fileType = originalFileName.substring(originalFileName.lastIndexOf("."));
        String newFileName = UUID.randomUUID().toString().replace("-", "") + fileType;
        File newFile = new File(directory.getPath() + "/" + newFileName);
        try {
            if (!newFile.exists()) {
                file.createNewFile();
            }
            FileCopyUtils.copy(file, newFile);

            String filePath  = newFile.getPath();
            String pathForSave =null;
            if(type.equals(FileContentType.Assert)){
                pathForSave = filePath.substring(DEFAULT_ASSERT_ROOT.length());
            }
            if(type.equals(FileContentType.Content)){
                pathForSave = filePath.substring(DEFAULT_PIC_ROOT.length());
            }
            return Optional.of(pathForSave);
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

}
