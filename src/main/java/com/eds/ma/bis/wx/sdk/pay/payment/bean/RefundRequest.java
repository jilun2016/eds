package com.eds.ma.bis.wx.sdk.pay.payment.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @borball on 5/17/2016.
 */
public class RefundRequest {

    @JsonProperty("transaction_id")
    private String transactionId;

    @JsonProperty("out_trade_no")
    private String tradeNumber;

    @JsonProperty("out_refund_no")
    private String refundNumber;

    @JsonProperty("total_fee")
    private int totalFee;

    @JsonProperty("refund_fee")
    private int refundFee;

    @JsonProperty("refund_fee_type")
    private String refundFeeType;

    @JsonProperty("op_user_id")
    private String operatorId;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTradeNumber() {
        return tradeNumber;
    }

    public void setTradeNumber(String tradeNumber) {
        this.tradeNumber = tradeNumber;
    }

    public String getRefundNumber() {
        return refundNumber;
    }

    public void setRefundNumber(String refundNumber) {
        this.refundNumber = refundNumber;
    }

    public int getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(int totalFee) {
        this.totalFee = totalFee;
    }

    public int getRefundFee() {
        return refundFee;
    }

    public void setRefundFee(int refundFee) {
        this.refundFee = refundFee;
    }

    public String getRefundFeeType() {
        return refundFeeType;
    }

    public void setRefundFeeType(String refundFeeType) {
        this.refundFeeType = refundFeeType;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RefundRequest{");
        sb.append("transactionId='").append(transactionId).append('\'');
        sb.append(", tradeNumber='").append(tradeNumber).append('\'');
        sb.append(", refundNumber='").append(refundNumber).append('\'');
        sb.append(", totalFee=").append(totalFee);
        sb.append(", refundFee=").append(refundFee);
        sb.append(", refundFeeType='").append(refundFeeType).append('\'');
        sb.append(", operatorId='").append(operatorId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
