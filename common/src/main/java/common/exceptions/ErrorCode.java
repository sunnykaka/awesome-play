package common.exceptions;

import common.models.utils.ViewEnum;

/**
 * Created by liubin on 15-8-4.
 */
public enum ErrorCode implements ViewEnum {

    InvalidRefreshToken(400, "无效的RefreshToken"),
    BadRequest(400, "请求的参数个数或格式不符合要求"),
    InvalidArgument(400, "请求的参数不正确"),

    MissAccessToken(401, "请求缺少AccessToken"),
    InvalidAccessToken(401, "无效或已过期的AccessToken"),
    Unauthorized(401, "无权访问"),

    Forbidden(403, "禁止访问"),

    NotFound(404, "请求的地址不正确"),

    UsernameExist(409, "用户名已存在"),
    PhoneExist(409, "手机已存在"),
    EmailExist(409, "邮箱已存在"),
    Conflict(409, "请求的资源发生冲突"),

    InternalError(500, "服务器内部错误");

    public int status;

    public String message;

    ErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public String getName() {
        return this.toString();
    }

    @Override
    public String getValue() {
        return message;
    }


}
