package ca.powercool.powercoolhub.utilities;

import java.util.List;
import java.util.Map;

public class PrintUtility {
    // Utility method to print a 2D list
    public static <T> void print2DList(List<List<T>> list) {
        for (List<T> row : list) {
            for (T element : row) {
                System.out.print(element + "\t");
            }
            System.out.println();
        }
    }

    // Utility method to print a 2D map
    public static <K, V> void print2DMap(Map<K, List<V>> map) {
        for (Map.Entry<K, List<V>> entry : map.entrySet()) {
            System.out.print(entry.getKey() + ":\t");
            for (V value : entry.getValue()) {
                System.out.print(value + "\t");
            }
            System.out.println();
        }
    }
}
