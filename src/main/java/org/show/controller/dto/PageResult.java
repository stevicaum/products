package org.show.controller.dto;

import java.util.List;

public class PageResult<T> {

  private long totalPages;
  private List<T> items;

  protected PageResult() {
  }

  public PageResult(long totalPages, List<T> items) {
    this.totalPages = totalPages;
    this.items = items;
  }

  public long getTotalPages() {
    return totalPages;
  }

  public List<T> getItems() {
    return items;
  }
}
