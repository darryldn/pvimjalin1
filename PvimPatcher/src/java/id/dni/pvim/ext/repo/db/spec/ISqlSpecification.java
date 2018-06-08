/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.repo.db.spec;

import id.dni.pvim.ext.repo.ISpecification;

/**
 *
 * @author darryl.sulistyan
 */
public interface ISqlSpecification extends ISpecification {
    
    public String toParameterizedSqlQuery();
    
    public Object[] getSqlParams();
    
}
