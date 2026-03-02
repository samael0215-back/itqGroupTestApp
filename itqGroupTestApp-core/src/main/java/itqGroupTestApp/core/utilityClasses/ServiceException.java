package itqGroupTestApp.core.utilityClasses;

import itqGroupTestApp.core.entity.ErrorCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceException extends RuntimeException {

    private final String code;

    public ServiceException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public ServiceException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.code = errorCode.getCode();
    }
}
