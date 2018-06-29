/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wn.tasman.notification.ext.dni.services;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 *
 * @author darryl.sulistyan
 */
public interface AsyncSender {
    
    public <T> Future<T> doAsync(Callable<T> callable);
    
}
