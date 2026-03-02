package itqGroupTestApp.core.entity;

import lombok.Getter;

@Getter
public enum ErrorCode {
    DOCUMENT_NOT_FOUND("NOT_FOUND", "Не найдено"),
    INVALID_OPERATION("INVALID_OPERATION", "Конфликт: недопустимая операция"),
    VALIDATION_ERROR("VALIDATION_ERROR", "Ошибка валидации");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
