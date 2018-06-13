/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.telegram.repo;

import id.dni.pvim.ext.repo.ICRUDRepository;
import id.dni.pvim.ext.repo.ISpecification;
import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import id.dni.pvim.ext.telegram.repo.db.vo.NotifiedTicketsVo;

/**
 *
 * @author darryl.sulistyan
 */
interface INotifiedTicketsRepository extends ICRUDRepository<NotifiedTicketsVo> {
    
    public int remove(ISpecification spec) throws PvExtPersistenceException;
    
}
