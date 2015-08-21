package common.utils.file;

import java.io.File;
import java.util.Optional;

/**
 * 七牛云盘上传
 * @auth amos
 * 15-4-9.
 */
public class QiNiuFileUpload implements FileUpload {

    @Override
    public Optional<String> upload(File file, FileContentType type) {
        //TODO  七牛上传
        return null;
    }
}
