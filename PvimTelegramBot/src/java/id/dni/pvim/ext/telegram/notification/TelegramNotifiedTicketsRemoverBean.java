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
public class TelegramNotifiedTicketsRemoverBean implements TelegramNotifiedTicketsRemoverBeanLocal {
    
    @Resource
    private TimerService timerService;

    private static final String TIMER_NAME = "@id.dni.pvim.ext.telegram.notification.TelegramNotifiedTicketsRemoverBean#TIMER";
    
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
//        timerService.createTimer(0, TelegramConfig.getStaleNotifiedTicketsRemoverInterval(), timerInfo);
        
    }
    
    @Timeout
    private void notified(Timer t) {
//        
//        INotifiedTicketsRepository repos = new NotifiedTicketsRepository();
//        try {
//            repos.remove(new RemoveDeletedTicketsFromNotifiedSpec());
//        } catch (PvExtPersistenceException ex) {
//            Logger.getLogger(TelegramNotifiedTicketsRemoverBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        
//        try {
//            repos.remove(new RemoveClosedOrDeletedTicketsFromNotifiedSpec());
//        } catch (PvExtPersistenceException ex) {
//            Logger.getLogger(TelegramNotifiedTicketsRemoverBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        
    }
    
}
