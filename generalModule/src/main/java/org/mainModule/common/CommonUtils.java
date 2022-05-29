package org.mainModule.common;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CommonUtils {
    public static void waiter(int timeoutInMS){
        try {
            TimeUnit.MILLISECONDS.sleep(timeoutInMS);
        } catch (InterruptedException e){
            System.out.println(e.getMessage());
        }
    }

    public static List<String> hltvLinkTemplate(List<String> notProcessedList){
        return notProcessedList.stream().map(notProcessedLink -> {
            return "https://www.hltv.org" + notProcessedLink;
        }).collect(Collectors.toList());
    }
}
