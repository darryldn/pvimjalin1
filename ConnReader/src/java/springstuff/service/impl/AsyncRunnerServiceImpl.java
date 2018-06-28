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
