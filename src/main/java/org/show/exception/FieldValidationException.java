package org.show.exception;

public class FieldValidationException extends RuntimeException {
  private static final String MESSAGE = "Validation failed for field: %s";
  private static final long serialVersionUID = 1756656737235473931L;

  public FieldValidationException(String field) {
    super(String.format(MESSAGE, field));
  }
}
