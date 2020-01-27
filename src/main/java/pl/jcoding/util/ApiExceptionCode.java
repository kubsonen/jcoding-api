package pl.jcoding.util;

import lombok.Getter;

@Getter
public enum ApiExceptionCode {
    UNHANDLED_ERROR(0, ""),
    CANNOT_GET_MIME_TYPE(1, ""),
    NULL_OR_EMPTY_MIME_TYPE(2, ""),
    MIME_IS_NOT_AN_IMAGE(3, ""),
    UNHANDLED_MIME_TYPE(4, ""),
    NOT_FOUND_PIECES_IN_PHOTO(5, ""),
    PASSWORD_MISMATCH(6, ""),
    USER_IN_NOT_MOTO_FOTO_MEMBER(7, ""),
    USER_IN_NOT_AUTHENTICATED(8, ""),
    USER_WITH_INPUT_NICKNAME_NOT_FOUND(9, ""),
    ;

    private int errorCode;
    private String errorDescription;

    ApiExceptionCode(int errorCode, String errorDescription) {
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
    }

}
