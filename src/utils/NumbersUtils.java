package utils;

import java.util.concurrent.ThreadLocalRandom;

public class NumbersUtils {

    public static Integer rand(Integer min, Integer max){
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

}
