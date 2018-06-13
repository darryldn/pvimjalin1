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
public class TelegramSubscribersByChatIDSpec implements ISqlSpecification {

    private static final String SQL = 
            new StringBuilder()
                    .append(" SELECT ").append(TelegramSubscriberVo.FIELD_SUBS_ID).append(", ")
                                      .append(TelegramSubscriberVo.FIELD_CHAT_ID).append(", ")
                                      .append(TelegramSubscriberVo.FIELD_PHONE_NUM).append(", ")
                                      .append(TelegramSubscriberVo.FIELD_LASTUPDATE).append(", ")
                                      .append(TelegramSubscriberVo.FIELD_PASSKEY).append(" ")
                    .append(" FROM ")  .append(TelegramSubscriberVo.TABLE_NAME).append(" ")
                    .append(" WHERE ") .append(TelegramSubscriberVo.FIELD_CHAT_ID).append("=? ")
            .toString();
    
    private final long chatID;
    public TelegramSubscribersByChatIDSpec(long chatID) {
        this.chatID = chatID;
    }
    
    @Override
    public String toParameterizedSqlQuery() {
        return SQL;
    }

    @Override
    public Object[] getSqlParams() {
        return (Object[])(new Long[]{chatID});
    }
    
}
