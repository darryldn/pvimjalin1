/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.ext.firebase.cloud.msg.json;

import java.util.List;

/**
 *{"multicast_id":8908551463645251729,"success":1,"failure":0,"canonical_ids":0,"results":[{"message_id":"0:1534738734400173%9e3a44b89e3a44b8"}]}
 * @author darryl.sulistyan
 */
public class FcmMessageDownstreamResponseJson {
    
    private long multicast_id;
    private int success;
    private int failure;
    private int canonical_ids;
    private List<FcmMessageDownstreamResponseStatusJson> results;

    public long getMulticast_id() {
        return multicast_id;
    }

    public void setMulticast_id(long multicast_id) {
        this.multicast_id = multicast_id;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public int getFailure() {
        return failure;
    }

    public void setFailure(int failure) {
        this.failure = failure;
    }

    public int getCanonical_ids() {
        return canonical_ids;
    }

    public void setCanonical_ids(int canonical_ids) {
        this.canonical_ids = canonical_ids;
    }

    public List<FcmMessageDownstreamResponseStatusJson> getResults() {
        return results;
    }

    public void setResults(List<FcmMessageDownstreamResponseStatusJson> results) {
        this.results = results;
    }
    
    
}
