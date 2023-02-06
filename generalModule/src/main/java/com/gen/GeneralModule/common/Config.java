package com.gen.GeneralModule.common;

import java.util.Arrays;
import java.util.List;

public class Config {

    //количество грядущих матчей, которое надо распарсить
    public static int totalMatchesCount = 20;

    //если сила больше в это число раз, то предикт выдается (должно совпадать с calculating)
    public static float compareMultiplier = 2.1f;
    public static int betLimit = 12500;

    public static List<Integer> activeMaps = Arrays.asList(1,2,3,4,5,6,7);

}
