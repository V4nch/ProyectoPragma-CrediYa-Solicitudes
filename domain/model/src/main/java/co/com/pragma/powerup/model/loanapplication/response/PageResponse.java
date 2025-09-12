package co.com.pragma.powerup.model.loanapplication.response;

import lombok.Value;

import java.util.List;


@Value
public class PageResponse<T> {
    int page;
    int size;
    long totalItems;
    int totalPages;
    List<T> items;
}
