package com.lv.springboot.persistence;

import com.google.common.base.Throwables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Consumer;
import java.util.function.Function;

@Service
public class Transactor {

    private final TransactionTemplate transactionTemplate;

    @Autowired
    public Transactor(PlatformTransactionManager txManager) {
        this.transactionTemplate = new TransactionTemplate(txManager);
    }

    /**
     * Callback run within a transaction, if an exception is throw it will propagate
     */
    public <T> T call(Function<TransactionStatus, T> function) {
        try {
            return transactionTemplate.execute(status -> function.apply(status));
        } catch (TransactionException e) {
            throw e.getCause() != null ? Throwables.propagate(e.getCause()) : e;
        }
    }

    /**
     * Callback run within a transaction, if an exception is throw it will propagate
     */
    public void run(Consumer<TransactionStatus> function) {
        call(status -> {
            function.accept(status);
            return null;
        });
    }

    public void runAndRollback(Consumer<TransactionStatus> function) {
        call(status -> {
            status.setRollbackOnly();
            function.accept(status);
            return null;
        });
    }
}
