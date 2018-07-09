/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.service.impl;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import springstuff.service.AsyncRunnerService;

/**
 * Simple class to run custom code in Async mode
 * DO NOT USE ExecutorService in WAR / EAR / EJB context. This is because the thread is
 * not managed by the container!! EJB 3.1 has @Asynchronous annotation. Spring has
 * @Async annotation which does the same thing.
 * 
 * This class must be called via Spring IoC container. As such, 
 * doing this:
 * AsyncRunnerServiceImpl impl = new  AsyncRunnerServiceImpl();
 * impl.run(runnable);
 * 
 * won't work!
 * Inject the instance via @Autowired / @Inject (they're the same)
 * and call run via that.
 * 
 * @author darryl.sulistyan
 */
@Service
public class AsyncRunnerServiceImpl implements AsyncRunnerService {

    @Override
    @Async
    public void run(Runnable job) {
        try {
            job.run();
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
