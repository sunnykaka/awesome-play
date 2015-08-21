package common.exceptions;



/**
 * ERP项目业务异常
 * User: liubin
 * Date: 14-1-10
 */
public class AppBusinessException extends RuntimeException {

    //类似Http状态码
    private ErrorCode errorCode = ErrorCode.InternalError;

    public AppBusinessException() {
        super();
    }

    public AppBusinessException(String message) {
        super(message);
    }

    /**
     * @param errorCode 状态码, 在app项目里，这个字段会在错误信息里返回给客户端，shop项目这个字段暂时无作用。
     * @param message
     */
    public AppBusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public AppBusinessException(ErrorCode errorCode) {
        this(errorCode, errorCode.message);
    }

    public AppBusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppBusinessException(Throwable cause) {
        super(cause);
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
