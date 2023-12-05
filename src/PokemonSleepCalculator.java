import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * @author Yuchi Zhang
 * @version 0.0.1
 * @date 2023-11-16 15:27
 */
public class PokemonSleepCalculator {
    private static final double MAX_SLEEP_SCORE = 100.0;
    private static final double FULL_SLEEP_HOURS = 8.5;

    // 创建四个岛屿的实例
    private static final Island[] ISLANDS = new Island[] {
            new Island("萌绿", new long[] {0, 972750, 2076360, 4622038, 8331004, 19563356, 19564316}),
            new Island("天青", new long[] {0, 1580337, 3511048, 7156032, 13441344, 30473422, 30502920}),
            new Island("灰褐", new long[] {0, 1838628, 4623489, 9925344, 19563120, 43685001, 43762600}),
            new Island("冰原", new long[] {0, 2834850, 7143777, 15415155, 28537400, 65143536, 65712275})
    };

    public enum SleepType {
        LIGHT_SLEEP,
        NORMAL_SLEEP,
        DEEP_SLEEP
    }

    public static void main(String[] args) {
        // 测试数据
        System.out.println(calculateSleepTime(0, 7, 0, 154159, 1, SleepType.DEEP_SLEEP));  // 第一个岛，7只宝可梦，第一次睡眠，当前能量355654
        //System.out.println(calculateSleepTime(2, 7, 4, 355654));  // 第一个岛，7只宝可梦，已睡4小时，当前能量355654
    }

    public static String calculateSleepTime(int islandIndex, int targetPokemonCount, double sleptHours, double currentEnergy, double sleepScoreMultiplier, SleepType sleepType) {
        Island island = ISLANDS[islandIndex];
        long neededScore = island.getMinScore(targetPokemonCount);
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        if (sleptHours == 0) {
            double requiredMultiplier = Math.ceil(neededScore * 100 / (currentEnergy * MAX_SLEEP_SCORE * sleepScoreMultiplier)) / 100; // multiply by 100, round up, then divide by 100            double remainingTime = requiredMultiplier * FULL_SLEEP_HOURS; // calculate the remaining time based on the rounded up multiplier//            double remainingTime = neededScore / (currentEnergy * MAX_SLEEP_SCORE * sleepScoreMultiplier) * FULL_SLEEP_HOURS; // in hours
            double remainingTime = requiredMultiplier * FULL_SLEEP_HOURS; // calculate the remaining time based on the rounded up multiplier
            if (requiredMultiplier > 8.5) {
                return "The required score cannot be achieved with the current energy.";
            }
            int hours = (int) remainingTime;
            int minutes = (int) Math.ceil((remainingTime - hours) * 60); // round up the minutes
            String advice = "init";
            LocalDateTime timeEndOfWaiting;
            LocalDateTime timeAdjusted;
            LocalDateTime timeEndOfRest;
            //根据睡眠类型给予不同建议
            switch (sleepType) {
                case LIGHT_SLEEP:
                    timeEndOfWaiting = now.plusMinutes(20);
                    timeAdjusted = timeEndOfWaiting.plusHours(hours).plusMinutes(minutes).minusMinutes(20);
                    advice = "请维持较弱的声响"
                            + "\n你需要再睡" + hours + "小时" + minutes + "分钟才能在" + island.getName() + "遇到" + targetPokemonCount + "只宝可梦。"
                            + "\n秒睡过程如下："
                            + "\n初始记录时间: " + now.format(formatter)
                            + "\n↓ 静置20分钟"
                            + "\n静置后时间: " + timeEndOfWaiting.format(formatter)
                            + "\n↓ 有一定幅度地摇动手机->向后调整" + hours + "小时" + minutes + "分钟并减去20分钟"
                            + "\n调整后时间: " + timeAdjusted.format(formatter) + " -> 对应实际时间: " + timeEndOfWaiting.format(formatter);
                    break;
                case NORMAL_SLEEP:
                    timeEndOfWaiting = now.plusMinutes(20);
                    timeAdjusted = timeEndOfWaiting.plusHours(hours).plusMinutes(minutes).minusMinutes(30); // subtract the first 20 minutes and the later 10 minutes
                    timeEndOfRest = timeAdjusted.plusMinutes(10); // timeC after 10 minutes of statically remaining
                    advice = "请注意保持安静"
                            + "\n你需要再睡" + hours + "小时" + minutes + "分钟才能在" + island.getName() + "遇到" + targetPokemonCount + "只宝可梦。"
                            + "\n秒睡过程如下："
                            + "\n初始记录时间: " + now.format(formatter)
                            + "\n↓ 静置20分钟"
                            + "\n静置后时间: " + timeEndOfWaiting.format(formatter)
                            + "\n↓ 向后调整" + hours + "小时" + minutes + "分钟并减去30分钟"
                            + "\n调整后时间: " + timeAdjusted.format(formatter) + " -> 对应实际时间: " + timeEndOfWaiting.format(formatter)
                            + "\n↓ 静置10分钟"
                            + "\n最终时间: " + timeEndOfRest.format(formatter) + " -> 对应实际时间: " + timeEndOfWaiting.plusMinutes(10).format(formatter);
                    break;
                case DEEP_SLEEP:
                    timeEndOfWaiting = now.plusMinutes(20);
                    timeAdjusted = timeEndOfWaiting.plusHours(hours).plusMinutes(minutes).minusMinutes(20);
                    advice = "请注意保持安静"
                            + "\n你需要再睡" + hours + "小时" + minutes + "分钟才能在" + island.getName() + "遇到" + targetPokemonCount + "只宝可梦。"
                            + "\n秒睡过程如下："
                            + "\n初始记录时间: " + now.format(formatter)
                            + "\n↓ 静置20分钟"
                            + "\n静置后时间: " + timeEndOfWaiting.format(formatter)
                            + "\n↓ 向后调整" + hours + "小时" + minutes + "分钟并减去20分钟"
                            + "\n调整后时间: " + timeAdjusted.format(formatter) + " -> 对应实际时间: " + timeEndOfWaiting.format(formatter);
                    break;
            }
            return advice;
        } else {
            double sleptScore = Math.min(MAX_SLEEP_SCORE, sleptHours / FULL_SLEEP_HOURS * MAX_SLEEP_SCORE);
            double currentScore = sleptScore * currentEnergy * sleepScoreMultiplier;
            int reachablePokemonCount = getReachablePokemonCount(island, currentScore);
            return "你的当前能量能在" + island.getName() + "遇到" + reachablePokemonCount + "只宝可梦。";
        }
    }

    private static int getReachablePokemonCount(Island island, double remainingMaxEnergy) {
        long[] minScores = island.getMinScores();
        for (int i = minScores.length - 1; i >= 0; i--) {
            if (minScores[i] <= remainingMaxEnergy) {
                return i + 3;  // 数组下标从0开始，所以要加3
            }
        }
        return 0;
    }

    private static String roundToTwoDecimalPlaces(double value) {
        return String.format("%.2f", value);
    }
}