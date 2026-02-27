package coloringmethods;

public class BooleanColorScheme implements ColorScheme {
    public int getColor(int iter, int maxIter) {
        if(iter == maxIter) return 0x00000000;
        else return 0xFFFFFFFF;
    }
}
