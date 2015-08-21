package common.utils.file;

import java.io.File;
import java.util.Optional;

/**
 * @auth amos
 * 15-4-9.
 */
public interface FileUpload {


    Optional<String> upload(File file, FileContentType type);

}
