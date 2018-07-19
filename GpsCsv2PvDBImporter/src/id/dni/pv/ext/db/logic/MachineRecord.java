/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pv.ext.db.logic;

/**
 *
 * @author darryl.sulistyan
 */
public class MachineRecord {
    private String bank;
    private String id;
    private String location;
    private String address;
    private String latitude;
    private String longitude;
    private String updateDate;

    @Override
    public String toString() {
        return "MachineRecord{" + "bank=" + bank + ", id=" + id + ", location=" + location + ", address=" + address + ", latitude=" + latitude + ", longitude=" + longitude + ", updateDate=" + updateDate + '}';
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }
    
    
}
