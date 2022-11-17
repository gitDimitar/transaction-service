package com.example.transactionservice.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@SuppressWarnings("JpaMissingIdInspection")
public class TransactionInfo {

    @JsonProperty("date")
    @NotNull
    @Pattern(regexp = "([1-9]|[12][0-9]|3[01])-([1-9]|1[012])-((18|19|20|)[0-9][0-9])") /* The regex can be tweaked for much stricter validations */
    private String transactionDate;

    @NotNull
    @Pattern(regexp = "credit|debit") /* The regex can be removed and the field can be an Enum , as we only take 2 values */
    private String type;

    private double amount;

    public TransactionInfo(String transactionDate, String type, double amount)
    {
        this.transactionDate = transactionDate;
        this.type = type;
        this.amount = amount;
    }

    public TransactionInfo()
    {

    }

    public String getTransactionDate()
    {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate)
    {
        this.transactionDate = transactionDate;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public double getAmount()
    {
        return amount;
    }

    public void setAmount(double amount)
    {
        this.amount = amount;
    }
}