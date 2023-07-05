package com.customer.transaction.controller.View;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PagedData<T> {
    private long totalElements;
    private int totalPages;
    private int numberOfElements;
    private List<T> content;
}
