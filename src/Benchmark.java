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
        try (PrintWriter out = new PrintWriter("results/changes.csv")) {
            out.println("operation,structure,n,average_ms,movements_or_accesses,theory");
            for (int n : new int[]{100, 1000, 10000, 100000}) {
                int[] values = new Random(42).ints(n).toArray();
                for (int kind = 0; kind < 2; kind++) {
                    for (int middle = 0; middle < 2; middle++) {
                        for (int remove = 0; remove < 2; remove++) {
                            int index = middle == 0 ? 0 : n / 2;
                            long time = 0;
                            long count = 0;
                            for (int run = 0; run < 5; run++) {
                                DynamicArray array = kind == 0 ? new DynamicArray() : null;
                                LinkedList list = kind == 1 ? new LinkedList() : null;
                                for (int value : values) {
                                    if (kind == 0) array.add(value);
                                    else list.add(value);
                                }
                                int size = n;
                                int capacity = 10;
                                while (capacity < n) capacity *= 2;
                                for (int i = 0; i < 1000; i++) {
                                    if (remove == 1 && size <= index) {
                                        array = kind == 0 ? new DynamicArray() : null;
                                        list = kind == 1 ? new LinkedList() : null;
                                        for (int value : values) {
                                            if (kind == 0) array.add(value);
                                            else list.add(value);
                                        }
                                        size = n;
                                    }
                                    if (kind == 0) {
                                        if (remove == 0) {
                                            count += size - index + 1;
                                            if (size == capacity) { count += size; capacity *= 2; }
                                        } else count += size - index;
                                    } else count += index + 1;
                                    int value = values[i % n];
                                    long start = System.nanoTime();
                                    if (kind == 0) {
                                        if (remove == 0) array.add(index, value);
                                        else array.remove(index);
                                    } else {
                                        if (remove == 0) list.add(index, value);
                                        else list.remove(index);
                                    }
                                    time += System.nanoTime() - start;
                                    size += remove == 0 ? 1 : -1;
                                }
                            }
                            String operation = (remove == 0 ? "insert" : "remove") + (middle == 0 ? "_front" : "_middle");
                            String structure = kind == 0 ? "DynamicArray" : "LinkedList";
                            String theory = kind == 1 && middle == 0 ? "O(m)" : kind == 0 && remove == 0 ? "O(m(n+m))" : "O(mn)";
                            out.printf(java.util.Locale.US, "%s,%s,%d,%.4f,%d,%s%n", operation, structure, n, time / 5000000.0, count / 5, theory);
                        }
                    }
                }
                System.out.println("changes n = " + n);
            }
        }
        try (PrintWriter out = new PrintWriter("results/heap.csv")) {
            out.println("operation,n,average_ms,comparisons,theory");
            for (int n : new int[]{100, 1000, 10000, 100000}) {
                int[] values = new Random(42).ints(n).toArray();
                long insertTime = 0;
                long extractTime = 0;
                long insertComparisons = 0;
                long extractComparisons = 0;
                for (int run = 0; run < 5; run++) {
                    MinHeap heap = new MinHeap();
                    long start = System.nanoTime();
                    for (int value : values) heap.insert(value);
                    insertTime += System.nanoTime() - start;
                    insertComparisons += heap.comparisons;
                    heap.comparisons = 0;
                    int[] extracted = new int[n];
                    start = System.nanoTime();
                    for (int i = 0; i < n; i++) extracted[i] = heap.extractMin();
                    extractTime += System.nanoTime() - start;
                    extractComparisons += heap.comparisons;
                    for (int i = 1; i < n; i++) {
                        if (extracted[i] < extracted[i - 1]) throw new AssertionError("Heap order");
                    }
                }
                out.printf(java.util.Locale.US, "insert,%d,%.4f,%d,O(n log n)%n", n, insertTime / 5000000.0, insertComparisons / 5);
                out.printf(java.util.Locale.US, "extract,%d,%.4f,%d,O(n log n)%n", n, extractTime / 5000000.0, extractComparisons / 5);
                System.out.println("heap n = " + n);
            }
        }
    }
}
