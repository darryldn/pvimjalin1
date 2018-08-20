/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.service;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 *
 * @author darryl.sulistyan
 */
public interface AsyncRunnerService {
    
    public void run(Runnable job);
    
    /**
     * Performs asynchronous call of callable. The result is later put into
     * Future object.
     * 
     * When this function is called, it immediately exit without callable completed
     * 
     * To wait until callable finishes, you can invoke Future.get function.
     * It will wait until callable finishes.
     * 
     * Async call should be done this way, not using ExecutorService because
     * the thread from local ExecutorService is not managed by the container and
     * will interfere with Wildfly as a whole.
     * 
     * @param <T>
     * @param callable
     * @return 
     */
    public <T> Future<T> doAsync(Callable<T> callable);
    
}
