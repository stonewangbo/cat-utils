package com.s1coder.cat.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 类名: NotReEnterLock <br>
 * 用途: 不可重入锁 <br>
 *
 * @author wangbo <br>
 * Dec 29, 2017 6:35:05 PM
 */
public class NotReEnterLock {
    private static final Logger logger = LoggerFactory.getLogger(NotReEnterLock.class);

    AtomicBoolean lock = new AtomicBoolean(false);

    private String name;

    public void lock(String name) throws InterruptedException {

        while (!lock.compareAndSet(false, true)) {
            Thread.sleep(500);
            logger.debug("wait lock:{}...", name);
        }
        logger.info("lock:{}", name);
        this.name = name;
    }

    public void unlock() {

        if (lock.compareAndSet(true, false)) {
            logger.info("unlock:{}", name);
        }
    }

}
