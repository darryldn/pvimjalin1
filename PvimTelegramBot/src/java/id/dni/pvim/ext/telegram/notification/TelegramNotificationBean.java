/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.telegram.notification;

import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;

/**
 *
 * @author darryl.sulistyan
 */
@Singleton
public class TelegramNotificationBean implements TelegramNotificationBeanLocal {

    @Resource
    private TimerService timerService;

    private static final String TIMER_NAME = "@id.dni.pvim.ext.telegram.notification.TelegramNotificationBean#TIMER";
    
    @Override
    public void init() {
//        int lk = 0;
//        
//        // cancelling previous timers.
//        // assume timers are persistent!
//        for (Object obj : timerService.getTimers()) {
//            javax.ejb.Timer timer = (javax.ejb.Timer) obj;
//            Serializable s = timer.getInfo();
//            if (s instanceof TelegramTimerInfo) {
//                TelegramTimerInfo timerInfo = (TelegramTimerInfo) s;
//                String scheduled = timerInfo.getTimerName();
//                if (scheduled.equals(TIMER_NAME)) {
//                    lk = timerInfo.getKey();
//                    Logger.getLogger(this.getClass().getName()).log(Level.FINEST, 
//                            " - previous timer cancelled! Timer info: {0}", timerInfo);
//                    timer.cancel();
//                }
//            }
//        }
//
//        TelegramTimerInfo timerInfo = new TelegramTimerInfo();
//        timerInfo.setTimerName(TIMER_NAME);
//        timerInfo.setKey(lk+1);
//        timerService.createTimer(0, TelegramConfig.getNotificationInterval(), timerInfo);
        
    }
    
    @Timeout
    private void notification(Timer t) {
        
//        try {
//            
//            // 1. read database for all new tickets
//            INewlyAssignedTicketRepository newTicketRepos = new NewlyAssignedTicketsRepository(
//                    PVIMDBConnectionFactory.getInstance().getDataSource());
//            List<NewlyAssignedTicketVo> lvo = newTicketRepos.query(new NewlyAssignedTicketSpec());
//            
//            // 2. foreach ticket, send message to that one guy
//            String deeplink = TelegramConfig.getDeeplinkPrefix();
//            INotifiedTicketsRepository repos = new NotifiedTicketsRepository();
//            for (NewlyAssignedTicketVo ticket : lvo) {
//                String content = deeplink + ticket.getTicket_number();
//                try {
//                    if (MessageSender.sendMessage(ticket.getChat_id(), content)) {
//                        
//                        // add entry to ticket notification table
//                        NotifiedTicketsVo vo = new NotifiedTicketsVo();
//                        vo.setTicketID(ticket.getTicket_id());
//                        vo.setLastupdated();
//                        repos.insert(vo);
//                    
//                    }
//                    
//                } catch (IOException ex) {
//                    Logger.getLogger(TelegramNotificationBean.class.getName()).log(Level.SEVERE, null, ex);
//                    
//                }
//            }
//            
//        } catch (PvExtPersistenceException ex) {
//            Logger.getLogger(TelegramNotificationBean.class.getName()).log(Level.SEVERE, null, ex);
//            
//        }
        
    }
    
    
}
