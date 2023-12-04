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

    public static void main(String[] args) {
        // 测试数据
        System.out.println(calculateSleepTime(2, 8, 0, 519406, 1));  // 第一个岛，7只宝可梦，第一次睡眠，当前能量355654
        //System.out.println(calculateSleepTime(2, 7, 4, 355654));  // 第一个岛，7只宝可梦，已睡4小时，当前能量355654
    }

    public static String calculateSleepTime(int islandIndex, int targetPokemonCount, double sleptHours, double currentEnergy, double sleepScoreMultiplier) {
        Island island = ISLANDS[islandIndex];
        long neededScore = island.getMinScore(targetPokemonCount);
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        if (sleptHours == 0) {
            double remainingTime = neededScore / (currentEnergy * MAX_SLEEP_SCORE * sleepScoreMultiplier) * FULL_SLEEP_HOURS; // in hours
            int hours = (int) remainingTime;
            int minutes = (int) Math.round((remainingTime - hours) * 60);
            LocalDateTime timeA = now;
            LocalDateTime timeB = timeA.plusMinutes(20);
            LocalDateTime timeC = timeB.plusHours(hours).plusMinutes(minutes).minusMinutes(30); // subtract the first 20 minutes and the later 10 minutes
            LocalDateTime timeD = timeB.plusMinutes(10); // timeB after 10 minutes of statically remaining
            return "你需要再睡" + hours + "小时" + minutes + "分钟才能在" + island.getName() + "遇到" + targetPokemonCount + "只宝可梦。"
                    + "\n\n秒睡过程如下："
                    + "\n初始记录时间A: " + timeA.format(formatter)
                    + "\n↓ 静置20分钟"
                    + "\n时间B: " + timeB.format(formatter)
                    + "\n↓ 向后调整" + hours + "小时" + minutes + "分钟并减去30分钟"
                    + "\n时间C: " + timeC.format(formatter)
                    + "\n↓ 静置10分钟"
                    + "\n时间D: " + timeD.format(formatter);
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