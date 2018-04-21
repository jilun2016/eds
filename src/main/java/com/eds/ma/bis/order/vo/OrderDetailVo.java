package com.eds.ma.bis.order.vo;import java.math.BigDecimal;import java.util.Date;/** * 用户订单详情vo * @Author gaoyan * @Date: 2018/4/14 0014 */public class OrderDetailVo {    /**     * 订单id     */    private Long orderId;    /**     * 订单code     */    private String orderCode;    /**     * 订单状态     * {@link com.eds.ma.bis.device.OrderStatusEnum}     */    private String orderStatus;    /**     * 订单状态描述     */    private String orderStatusDesc;    /**     * 租借时间     */    private Date rentStartTime;    /**     * 租借时间     */    private Date rentEndTime;    /**     * 租借商家id     */    private Long rentSpId;    /**     * 租借商家名称     */    private String rentSpName;    /**     * 租借设备精度     */    private BigDecimal rentSpLng;    /**     * 租借设备纬度     */    private BigDecimal rentSpLat;    /**     * 租借商家id     */    private Long returnSpId;    /**     * 租借商家名称     */    private String returnSpName;    /**     * 租借设备精度     */    private BigDecimal returnSpLng;    /**     * 租借设备纬度     */    private BigDecimal returnSpLat;    /**     * 设备id     */    private Long deviceId;    /**     * 用户id     */    private Long userId;    /**     * 租借实收金额(租借中状态:预计费用,已结束状态:真实收费)     */    private BigDecimal totalFee;    /**     * 余额支付金额     */    private BigDecimal balance;    public Long getOrderId() {        return orderId;    }    public void setOrderId(Long orderId) {        this.orderId = orderId;    }    public String getOrderCode() {        return orderCode;    }    public void setOrderCode(String orderCode) {        this.orderCode = orderCode;    }    public String getOrderStatus() {        return orderStatus;    }    public void setOrderStatus(String orderStatus) {        this.orderStatus = orderStatus;    }    public String getOrderStatusDesc() {        return orderStatusDesc;    }    public void setOrderStatusDesc(String orderStatusDesc) {        this.orderStatusDesc = orderStatusDesc;    }    public Long getDeviceId() {        return deviceId;    }    public void setDeviceId(Long deviceId) {        this.deviceId = deviceId;    }    public Long getUserId() {        return userId;    }    public void setUserId(Long userId) {        this.userId = userId;    }    public BigDecimal getTotalFee() {        return totalFee;    }    public void setTotalFee(BigDecimal totalFee) {        this.totalFee = totalFee;    }    public Date getRentStartTime() {        return rentStartTime;    }    public void setRentStartTime(Date rentStartTime) {        this.rentStartTime = rentStartTime;    }    public Date getRentEndTime() {        return rentEndTime;    }    public void setRentEndTime(Date rentEndTime) {        this.rentEndTime = rentEndTime;    }    public Long getRentSpId() {        return rentSpId;    }    public void setRentSpId(Long rentSpId) {        this.rentSpId = rentSpId;    }    public String getRentSpName() {        return rentSpName;    }    public void setRentSpName(String rentSpName) {        this.rentSpName = rentSpName;    }    public BigDecimal getRentSpLng() {        return rentSpLng;    }    public void setRentSpLng(BigDecimal rentSpLng) {        this.rentSpLng = rentSpLng;    }    public BigDecimal getRentSpLat() {        return rentSpLat;    }    public void setRentSpLat(BigDecimal rentSpLat) {        this.rentSpLat = rentSpLat;    }    public Long getReturnSpId() {        return returnSpId;    }    public void setReturnSpId(Long returnSpId) {        this.returnSpId = returnSpId;    }    public String getReturnSpName() {        return returnSpName;    }    public void setReturnSpName(String returnSpName) {        this.returnSpName = returnSpName;    }    public BigDecimal getReturnSpLng() {        return returnSpLng;    }    public void setReturnSpLng(BigDecimal returnSpLng) {        this.returnSpLng = returnSpLng;    }    public BigDecimal getReturnSpLat() {        return returnSpLat;    }    public void setReturnSpLat(BigDecimal returnSpLat) {        this.returnSpLat = returnSpLat;    }    public BigDecimal getBalance() {        return balance;    }    public void setBalance(BigDecimal balance) {        this.balance = balance;    }}