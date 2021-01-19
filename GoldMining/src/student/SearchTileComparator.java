package student;

import java.util.Comparator;

public class SearchTileComparator implements Comparator<SearchTile> {
    @Override
    public int compare(SearchTile first, SearchTile second) {
        if(first.f < second.f){
            return -1;
        }
        if(first.f > second.f){
            return 1;
        }
        return 0;
    }
}