/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.telegram.repo.spec;

import id.dni.pvim.ext.repo.db.spec.ISqlSpecification;
import id.dni.pvim.ext.telegram.repo.db.vo.TelegramSubscriberVo;

/**
 *
 * @author darryl.sulistyan
 */
public class TelegramSubscribersListOfPhonesSpec implements ISqlSpecification {

    private static final String SQL_FIND_BY_PHONENUM_LIST = 
            new StringBuilder()
                    .append("SELECT ").append(TelegramSubscriberVo.FIELD_SUBS_ID).append(", ")
                                      .append(TelegramSubscriberVo.FIELD_CHAT_ID).append(", ")
                                      .append(TelegramSubscriberVo.FIELD_PHONE_NUM).append(", ")
                                      .append(TelegramSubscriberVo.FIELD_LASTUPDATE).append(", ")
                                      .append(TelegramSubscriberVo.FIELD_PASSKEY).append(" ")
                    .append("FROM ")  .append(TelegramSubscriberVo.TABLE_NAME).append(" ")
                    .append("WHERE ") .append(TelegramSubscriberVo.FIELD_PHONE_NUM).append(" in ")
            .toString();
    
    private final String sqlQuery;
    private final String[] phones;
    
    public TelegramSubscribersListOfPhonesSpec(String[] phones) {
        
        this.phones = phones;
        
        StringBuilder sb = new StringBuilder();
        sb.append(SQL_FIND_BY_PHONENUM_LIST);
        sb.append("(");
        for (int i=0; phones != null && i<phones.length; ++i) {
            sb.append("?,");
        }
        if (phones != null && phones.length >= 1) {
            sb.setLength(sb.length()-1); // remove last ,
        }
        sb.append(")");
        this.sqlQuery = sb.toString();
        
    }
    
    @Override
    public String toParameterizedSqlQuery() {
        return this.sqlQuery;
    }

    @Override
    public Object[] getSqlParams() {
        return (Object[])(phones);
    }
    
}
