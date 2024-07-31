package com.dss.e_garage;

public class ModelService {
    String OrderId,Date,GID,Amount,Status,UserId,LatLong,BillUrl,Vno;
    public ModelService(){

    }
    public ModelService(String orderId, String date, String gid, String amount, String orderStatus, String userId, String latlong, String billurl, String vno) {
        OrderId = orderId;
        Date = date;
        GID = gid;
        Amount = amount;
        Status = orderStatus;
        UserId = userId;
        LatLong=latlong;
        BillUrl=billurl;
        Vno=vno;

    }

    public String getGID() {
        return GID;
    }

    public String getBillUrl() {
        return BillUrl;
    }

    public String getLatLong() {
        return LatLong;
    }

    public String getOrderId() {
        return OrderId;
    }


    public String getDate() {
        return Date;
    }


    public String getAmount() {
        return Amount;
    }

    public String getStatus() {
        return Status;
    }

    public String getUserId() {
        return UserId;
    }

    public void setOrderId(String orderId) {
        OrderId = orderId;
    }

    public void setDate(String date) {
        Date = date;
    }

    public void setGID(String GID) {
        this.GID = GID;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public void setLatLong(String latLong) {
        LatLong = latLong;
    }

    public void setBillUrl(String billUrl) {
        BillUrl = billUrl;
    }

    public String getVno() {
        return Vno;
    }

    public void setVno(String vno) {
        Vno = vno;
    }
}
