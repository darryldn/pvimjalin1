/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.dto;

/**
 *
 * @author darryl.sulistyan
 */
public class PvWsCassette {
    private String cassetteExchanged       ;
    private String cassetteId              ;
    private String cassetteType            ;
    private String currency                ;
    private String denomination            ;
    private String deposited               ;
    private String dispensed               ;
    private String filling                 ;
    private String physicalPositionName    ;
    private String rejected                ;
    private String remainingTotal          ;
    private String start                   ;
    private String status                  ;

    public String getCassetteExchanged() {
        return cassetteExchanged;
    }

    public void setCassetteExchanged(String cassetteExchanged) {
        this.cassetteExchanged = cassetteExchanged;
    }

    public String getCassetteId() {
        return cassetteId;
    }

    public void setCassetteId(String cassetteId) {
        this.cassetteId = cassetteId;
    }

    public String getCassetteType() {
        return cassetteType;
    }

    public void setCassetteType(String cassetteType) {
        this.cassetteType = cassetteType;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDenomination() {
        return denomination;
    }

    public void setDenomination(String denomination) {
        this.denomination = denomination;
    }

    public String getDeposited() {
        return deposited;
    }

    public void setDeposited(String deposited) {
        this.deposited = deposited;
    }

    public String getDispensed() {
        return dispensed;
    }

    public void setDispensed(String dispensed) {
        this.dispensed = dispensed;
    }

    public String getFilling() {
        return filling;
    }

    public void setFilling(String filling) {
        this.filling = filling;
    }

    public String getPhysicalPositionName() {
        return physicalPositionName;
    }

    public void setPhysicalPositionName(String physicalPositionName) {
        this.physicalPositionName = physicalPositionName;
    }

    public String getRejected() {
        return rejected;
    }

    public void setRejected(String rejected) {
        this.rejected = rejected;
    }

    public String getRemainingTotal() {
        return remainingTotal;
    }

    public void setRemainingTotal(String remainingTotal) {
        this.remainingTotal = remainingTotal;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    

}
