package org.example.exception;

/**
 * Исключение для обработки некорректного ответа от апи сервера
 */
public class APIException extends RuntimeException {
  public APIException(String message) {
    super(message);
  }
}
