package student;
// credit: https://discuss.codechef.com/t/java-need-help-in-storing-a-pair-in-a-set/66365
public class Pair<X, Y>{
    public final X first;
    public final Y second;

    public Pair(X first, Y second){
        this.first = first;
        this.second = second;
    }
    public static <X, Y> Pair <X, Y> of(X a, Y b){
        return new Pair<>(a, b);
    }
}
