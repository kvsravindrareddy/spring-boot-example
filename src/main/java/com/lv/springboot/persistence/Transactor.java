package com.lv.springboot.persistence;

import com.google.common.base.Throwables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Consumer;
import java.util.function.Function;

@Service
public class Transactor {

    private final TransactionTemplate transactionTemplate;
    private final PlatformTransactionManager transactionManager;

    @Autowired
    public Transactor(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    public <T> T call(Function<TransactionStatus, T> function) {
        try {
            return transactionTemplate.execute(function::apply);
        } catch (TransactionException e) {
            throw e.getCause() != null ? Throwables.propagate(e.getCause()) : e;
        }
    }

    public void run(Consumer<TransactionStatus> function) {
        call(status -> {
            function.accept(status);
            return null;
        });
    }

    public void runAndRollback(Consumer<TransactionStatus> function) {
        final TransactionStatus status = transactionManager.getTransaction(transactionTemplate);
        try {
            final TransactionCallback action = txStatus -> {
                function.accept(txStatus);
                return null;
            };
            action.doInTransaction(status);
        } finally {
            transactionManager.rollback(status);
        }
    }
}
