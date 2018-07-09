/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.service;

import id.dni.pvim.ext.dto.PvWsCassette;
import java.util.List;
import springstuff.exceptions.RemoteWsException;

/**
 *
 * @author darryl.sulistyan
 */
public interface IPvWebService {
    
    public List<PvWsCassette> getCassetteBalance(String machineId) throws RemoteWsException;
    
}
