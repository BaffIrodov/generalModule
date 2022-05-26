package org.mainModule.common;

import java.util.concurrent.TimeUnit;

public class CommonUtils {
    public static void waiter(int timeoutInMS){
        try {
            TimeUnit.MILLISECONDS.sleep(timeoutInMS);
        } catch (InterruptedException e){
            System.out.println(e.getMessage());
        }
    }
}
