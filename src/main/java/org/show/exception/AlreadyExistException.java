package org.show.exception;

public class AlreadyExistException extends RuntimeException {
  private static final String MESSAGE = "Entity with %s = %s already exists";
  private static final long serialVersionUID = -5471672059734581923L;

  public AlreadyExistException(String field, String name) {
    super(String.format(MESSAGE, field, name));
  }
}
