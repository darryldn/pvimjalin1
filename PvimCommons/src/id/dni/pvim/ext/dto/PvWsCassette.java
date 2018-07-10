/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.dto;

import java.util.Objects;

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

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(this.cassetteExchanged);
        hash = 23 * hash + Objects.hashCode(this.cassetteId);
        hash = 23 * hash + Objects.hashCode(this.cassetteType);
        hash = 23 * hash + Objects.hashCode(this.currency);
        hash = 23 * hash + Objects.hashCode(this.denomination);
        hash = 23 * hash + Objects.hashCode(this.deposited);
        hash = 23 * hash + Objects.hashCode(this.dispensed);
        hash = 23 * hash + Objects.hashCode(this.filling);
        hash = 23 * hash + Objects.hashCode(this.physicalPositionName);
        hash = 23 * hash + Objects.hashCode(this.rejected);
        hash = 23 * hash + Objects.hashCode(this.remainingTotal);
        hash = 23 * hash + Objects.hashCode(this.start);
        hash = 23 * hash + Objects.hashCode(this.status);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PvWsCassette other = (PvWsCassette) obj;
        if (!Objects.equals(this.cassetteExchanged, other.cassetteExchanged)) {
            return false;
        }
        if (!Objects.equals(this.cassetteId, other.cassetteId)) {
            return false;
        }
        if (!Objects.equals(this.cassetteType, other.cassetteType)) {
            return false;
        }
        if (!Objects.equals(this.currency, other.currency)) {
            return false;
        }
        if (!Objects.equals(this.denomination, other.denomination)) {
            return false;
        }
        if (!Objects.equals(this.deposited, other.deposited)) {
            return false;
        }
        if (!Objects.equals(this.dispensed, other.dispensed)) {
            return false;
        }
        if (!Objects.equals(this.filling, other.filling)) {
            return false;
        }
        if (!Objects.equals(this.physicalPositionName, other.physicalPositionName)) {
            return false;
        }
        if (!Objects.equals(this.rejected, other.rejected)) {
            return false;
        }
        if (!Objects.equals(this.remainingTotal, other.remainingTotal)) {
            return false;
        }
        if (!Objects.equals(this.start, other.start)) {
            return false;
        }
        if (!Objects.equals(this.status, other.status)) {
            return false;
        }
        return true;
    }
    
    

}
