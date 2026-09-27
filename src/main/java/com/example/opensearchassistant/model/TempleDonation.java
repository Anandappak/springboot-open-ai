package com.example.opensearchassistant.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "temple_donations")
public class TempleDonation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String donorName;
    private BigDecimal amount;
    private String purpose;
    private LocalDate donationDate;
    private String paymentMethod = "UPI";
    private String transactionReference;
    private String upiId = "templedonation@upi";
    private String qrCodeLabel = "Temple Donation QR";
    private String bankName = "State Bank of India";
    private String accountHolderName = "Village Temple Trust";
    private String accountNumber = "123456789012";
    private String ifscCode = "SBIN0001234";
    private String paymentStatus = "PAID";

    public TempleDonation() {
    }

    public TempleDonation(Long id, String donorName, BigDecimal amount, String purpose, LocalDate donationDate) {
        this(id, donorName, amount, purpose, donationDate, "UPI", null, "templedonation@upi",
                "Temple Donation QR", "State Bank of India", "Village Temple Trust",
                "123456789012", "SBIN0001234", "PAID");
    }

    public TempleDonation(Long id, String donorName, BigDecimal amount, String purpose, LocalDate donationDate,
                          String paymentMethod, String transactionReference, String upiId,
                          String qrCodeLabel, String bankName, String accountHolderName,
                          String accountNumber, String ifscCode, String paymentStatus) {
        this.id = id;
        this.donorName = donorName;
        this.amount = amount;
        this.purpose = purpose;
        this.donationDate = donationDate;
        this.paymentMethod = paymentMethod == null || paymentMethod.isBlank() ? "UPI" : paymentMethod;
        this.transactionReference = transactionReference;
        this.upiId = upiId == null || upiId.isBlank() ? "templedonation@upi" : upiId;
        this.qrCodeLabel = qrCodeLabel == null || qrCodeLabel.isBlank() ? "Temple Donation QR" : qrCodeLabel;
        this.bankName = bankName == null || bankName.isBlank() ? "State Bank of India" : bankName;
        this.accountHolderName = accountHolderName == null || accountHolderName.isBlank() ? "Village Temple Trust" : accountHolderName;
        this.accountNumber = accountNumber == null || accountNumber.isBlank() ? "123456789012" : accountNumber;
        this.ifscCode = ifscCode == null || ifscCode.isBlank() ? "SBIN0001234" : ifscCode;
        this.paymentStatus = paymentStatus == null || paymentStatus.isBlank() ? "PAID" : paymentStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDonorName() {
        return donorName;
    }

    public void setDonorName(String donorName) {
        this.donorName = donorName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public LocalDate getDonationDate() {
        return donationDate;
    }

    public void setDonationDate(LocalDate donationDate) {
        this.donationDate = donationDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod == null || paymentMethod.isBlank() ? "UPI" : paymentMethod;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public String getUpiId() {
        return upiId;
    }

    public void setUpiId(String upiId) {
        this.upiId = upiId == null || upiId.isBlank() ? "templedonation@upi" : upiId;
    }

    public String getQrCodeLabel() {
        return qrCodeLabel;
    }

    public void setQrCodeLabel(String qrCodeLabel) {
        this.qrCodeLabel = qrCodeLabel == null || qrCodeLabel.isBlank() ? "Temple Donation QR" : qrCodeLabel;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName == null || bankName.isBlank() ? "State Bank of India" : bankName;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName == null || accountHolderName.isBlank() ? "Village Temple Trust" : accountHolderName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber == null || accountNumber.isBlank() ? "123456789012" : accountNumber;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode == null || ifscCode.isBlank() ? "SBIN0001234" : ifscCode;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus == null || paymentStatus.isBlank() ? "PAID" : paymentStatus;
    }
}
