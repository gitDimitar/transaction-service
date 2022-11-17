package com.example.transactionservice.controller;

import com.example.transactionservice.entity.TransactionInfo;
import com.example.transactionservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.IOException;
import java.util.Arrays;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/transaction")
@Validated
public class TransactionApiImpl {

    @Autowired
    TransactionService transactionService;

    @PostMapping
    public ResponseEntity<String> submitTransactions(@Valid @RequestBody TransactionInfo[] transactions)
    {
        try {
            transactionService.submitTransactions(Arrays.asList(transactions));
        }
        catch (IOException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping
    public ResponseEntity<TransactionInfo> retrieveTransaction(@RequestParam @NotNull @Pattern(regexp = "([1-9]|[12][0-9]|3[01])-([1-9]|1[012])-((18|19|20|)[0-9][0-9])") String date,
                                                               @RequestParam @NotNull @Pattern(regexp = "credit|debit") String type)
    {
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.retrieveTransaction(date, type));
    }
}
