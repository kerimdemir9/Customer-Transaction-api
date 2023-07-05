package com.customer.transaction.Data.service;

import com.customer.transaction.Data.model.Customer;
import com.customer.transaction.Data.model.Transaction;
import com.customer.transaction.Data.repository.TransactionRepository;
import com.customer.transaction.Data.util.GenericPagedModel;
import com.customer.transaction.util.SortDirection;
import lombok.val;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TransactionService {
    final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction findById(Integer id) {
        return getTransaction(id);
    }

    public GenericPagedModel<Transaction> findAll(int page, int size, String sortBy, SortDirection sortDirection) {
        try {
            val result = sortDirection.equals(SortDirection.Ascending)
                    ? transactionRepository.findAll(PageRequest.of(page, size, Sort.by(sortBy).ascending()))
                    : transactionRepository.findAll(PageRequest.of(page, size, Sort.by(sortBy).descending()));
            if (result.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No data");
            }

            return GenericPagedModel.<Transaction>builder()
                    .totalElements(result.getTotalElements())
                    .numberOfElements(result.getNumberOfElements())
                    .totalPages(result.getTotalPages())
                    .content(result.getContent())
                    .build();
        } catch (final DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionUtils.getStackTrace(ex));
        }
    }

    private Transaction getTransaction(Integer id) {
        try {
            val result = transactionRepository.findById(id);
            if(result.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "transactionId:".concat(id.toString()));
            }
            return result.get();
        }
        catch (final DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionUtils.getStackTrace(ex));
        }
    }

}
