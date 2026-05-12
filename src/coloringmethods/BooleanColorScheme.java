package coloringmethods;

public class BooleanColorScheme implements ColorScheme {
    public int getColor(int iter, int maxIter) {
        if(iter == maxIter) return 0;
        else return -1;
    }
}
