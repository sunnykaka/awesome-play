package common.exceptions;


/**
 * User: liubin
 * Date: 14-2-7
 */
public class AppException extends Exception {

    //类似Http状态码
    private ErrorCode errorCode = ErrorCode.InternalError;

    public AppException() {
        super();
    }

    public AppException(String message) {
        super(message);
    }

    /**
     * @param errorCode 状态码, 在app项目里，这个字段会在错误信息里返回给客户端，shop项目这个字段暂时无作用。
     * @param message
     */
    public AppException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public AppException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppException(Throwable cause) {
        super(cause);
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
