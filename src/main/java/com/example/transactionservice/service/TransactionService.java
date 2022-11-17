package com.example.transactionservice.service;

import com.example.transactionservice.entity.TransactionInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransactionService {

    private static final String CSV_FILE_NAME = "transactions.csv";
    private static final String EVENT_LOG_FILE = "eventLogFile.txt";
    private File csvFile = new File(CSV_FILE_NAME);

    /* In this case, we will not have headers in the CSV file for simplicity */
    public void submitTransactions(List<TransactionInfo> transactionList) throws IOException
    {
        /*
        1. File exists
            a) read from file
            b) add transactions to the ordered list of file transactions
            c) while adding, check if incoming transactions already appear in file transactions, if yes:
                i) add amounts
                ii)log event into a log-file
            d) Order the whole list(which is mostly ordered)
            e) write to file

         */

        Map<String, Double> transactionMap = new HashMap<>();

        for(TransactionInfo tran: transactionList) {
            String key = getTransactionKey(tran);
            transactionMap.put(key, transactionMap.getOrDefault(key, 0.0) + tran.getAmount());
        }

        List<TransactionInfo> transactionInfos = new ArrayList<>();
        Reader in = new FileReader(csvFile);
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(in);

        for (CSVRecord record : records) {
            String columnOne = record.get(0);
            String columnTwo = record.get(1);
            double columnThree = Double.parseDouble(record.get(2));
            TransactionInfo cur = new TransactionInfo(columnOne, columnTwo, columnThree);
            String key = getTransactionKey(cur);
            if (transactionMap.containsKey(key)) {
                cur.setAmount(cur.getAmount() + transactionMap.get(key));
                transactionMap.remove(key);
                writeToEventLog(cur);
            }
            transactionInfos.add(cur);
        }
        in.close();
        transactionMap.forEach((k, v) -> transactionInfos.add(new TransactionInfo(k.substring(0,k.lastIndexOf('-')),k.substring(k.lastIndexOf('-')+1), v)));

        Collections.sort(transactionInfos, Comparator.comparing(TransactionInfo::getTransactionDate));

        CSVPrinter printer = new CSVPrinter(new FileWriter(csvFile), CSVFormat.DEFAULT);

        transactionInfos.forEach(t -> {
            try {
                printer.printRecord(t.getTransactionDate(), t.getType(), t.getAmount());
            }
            catch (IOException e) {
                log.error(e.getMessage());
            }
        });
        printer.close();
    }

    private String getTransactionKey(TransactionInfo transaction) {
        return transaction.getTransactionDate() + "-" + transaction.getType();
    }


    public TransactionInfo retrieveTransaction(String date, String type)
    {
        /*
        Read from the file and search from the results.
        We can improve the performance of this function vastly by adding a caching layer which will hold added transactions from submitTransactions() and store them in the cache.
        We can then retrieve them very easily from in-memory
         */
        Iterable<CSVRecord> records = null;
        try {
            Reader in = new FileReader(csvFile);
            records = CSVFormat.DEFAULT.parse(in);
        } catch ( IOException e) {
            log.error(e.getMessage());
        }

        for (CSVRecord record : records) {
            String columnOne = record.get(0);
            String columnTwo = record.get(1);
            if(columnOne.equals(date) && columnTwo.equals(type)) {
                double columnThree = Double.parseDouble(record.get(2));
                return new TransactionInfo(columnOne, columnTwo, columnThree);
            }
        }
        return null;
    }

    private void writeToEventLog(TransactionInfo duplicate) throws IOException
    {
        FileWriter fileWriter = new FileWriter(EVENT_LOG_FILE, true);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.println(duplicate.getType() + " transaction for on " + duplicate.getTransactionDate()
                        + " already exists, total amount " + duplicate.getAmount());
        printWriter.close();
        fileWriter.close();
    }


}
