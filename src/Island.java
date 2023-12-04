/**
 * @author Yuchi Zhang
 * @version 0.0.1
 * @date 2023-11-16 15:27
 */
public class Island {
    private final String name;
    private final long[] minScores;  // 每个分数段的最小分数

    public Island(String name, long[] minScores) {
        this.name = name;
        this.minScores = minScores;
    }

    public String getName() {
        return name;
    }

    public long[] getMinScores() {
        return this.minScores;
    }

    public long getMinScore(int pokemonCount) {
        return minScores[pokemonCount-3];  // 数组下标从0开始，所以要减3
    }
}
