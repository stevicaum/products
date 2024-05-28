package org.show.exception;

public class ResourceNotFoundException extends RuntimeException {
  public static final String PRODUCT = "Product cannot be found for ID: {}";;
  private static final long serialVersionUID = -1266176142238147750L;

  public ResourceNotFoundException(final String message) {
    super(message);
  }
}
