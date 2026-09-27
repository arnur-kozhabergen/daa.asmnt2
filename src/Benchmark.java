import java.io.File;
import java.io.PrintWriter;
import java.util.Random;

public class Benchmark {
    public static void main(String[] args) throws Exception {
        new File("results").mkdirs();
        try (PrintWriter out = new PrintWriter("results/access.csv")) {
            out.println("structure,n,average_ms,element_accesses,theory");
            for (int n : new int[]{100, 1000, 10000, 100000}) {
                int[] values = new Random(42).ints(n).toArray();
                int[] indices = new Random(42).ints(10000, 0, n).toArray();
                long arrayTime = 0;
                long listTime = 0;
                long listAccesses = 0;
                long checksum = 0;
                for (int index : indices) listAccesses += index + 1;
                for (int run = 0; run < 5; run++) {
                    DynamicArray array = new DynamicArray();
                    LinkedList list = new LinkedList();
                    for (int value : values) {
                        array.add(value);
                        list.add(value);
                    }
                    long start = System.nanoTime();
                    for (int index : indices) checksum += array.get(index);
                    arrayTime += System.nanoTime() - start;
                    start = System.nanoTime();
                    for (int index : indices) checksum += list.get(index);
                    listTime += System.nanoTime() - start;
                }
                out.printf(java.util.Locale.US, "DynamicArray,%d,%.4f,10000,O(m)%n", n, arrayTime / 5000000.0);
                out.printf(java.util.Locale.US, "LinkedList,%d,%.4f,%d,O(mn)%n", n, listTime / 5000000.0, listAccesses);
                System.out.println("n = " + n + ", check = " + checksum);
            }
        }
        try (PrintWriter out = new PrintWriter("results/search.csv")) {
            out.println("structure,n,average_ms,comparisons,theory");
            for (int n : new int[]{100, 1000, 10000, 100000}) {
                int[] values = new Random(42).ints(n, 0, n * 2).toArray();
                int[] queries = new int[1000];
                Random random = new Random(43);
                for (int i = 0; i < queries.length; i++) {
                    queries[i] = i % 2 == 0 ? values[random.nextInt(n)] : -1;
                }
                long comparisons = 0;
                for (int query : queries) {
                    for (int value : values) {
                        comparisons++;
                        if (value == query) break;
                    }
                }
                long arrayTime = 0;
                long listTime = 0;
                int found = 0;
                for (int run = 0; run < 5; run++) {
                    DynamicArray array = new DynamicArray();
                    LinkedList list = new LinkedList();
                    for (int value : values) {
                        array.add(value);
                        list.add(value);
                    }
                    long start = System.nanoTime();
                    for (int query : queries) if (array.contains(query)) found++;
                    arrayTime += System.nanoTime() - start;
                    start = System.nanoTime();
                    for (int query : queries) if (list.contains(query)) found++;
                    listTime += System.nanoTime() - start;
                }
                out.printf(java.util.Locale.US, "DynamicArray,%d,%.4f,%d,O(mn)%n", n, arrayTime / 5000000.0, comparisons);
                out.printf(java.util.Locale.US, "LinkedList,%d,%.4f,%d,O(mn)%n", n, listTime / 5000000.0, comparisons);
                System.out.println("search n = " + n + ", found = " + found);
            }
        }
    }
}
