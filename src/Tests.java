import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Random;

public class Tests {
    public static void main(String[] args) {
        DynamicArray array = new DynamicArray();
        LinkedList list = new LinkedList();
        ArrayList<Integer> expected = new ArrayList<>();

        invalid(() -> array.get(0));
        invalid(() -> list.remove(0));
        array.add(7);
        list.add(7);
        check(array.get(0) == 7 && list.get(0) == 7);
        check(array.remove(0) == 7 && list.remove(0) == 7);

        Random random = new Random(42);
        for (int i = 0; i < 10000; i++) {
            int value = random.nextInt(100);
            expected.add(value);
            array.add(value);
            list.add(value);
        }
        for (int i = 0; i < 200; i++) {
            int index = i % 2 == 0 ? 0 : expected.size() / 2;
            expected.add(index, 5);
            array.add(index, 5);
            list.add(index, 5);
        }
        for (int i = 0; i < expected.size(); i++) {
            check(array.get(i) == expected.get(i));
            check(list.get(i) == expected.get(i));
        }
        for (int value = 0; value <= 100; value++) {
            check(array.contains(value) == expected.contains(value));
            check(list.contains(value) == expected.contains(value));
        }
        invalid(() -> array.add(-1, 0));
        invalid(() -> list.add(10201, 0));
        invalid(() -> array.get(10200));
        invalid(() -> list.get(-1));
        while (!expected.isEmpty()) {
            int index = expected.size() / 2;
            int value = expected.remove(index);
            check(array.remove(index) == value);
            check(list.remove(index) == value);
        }

        MinHeap heap = new MinHeap();
        PriorityQueue<Integer> queue = new PriorityQueue<>();
        invalid(() -> heap.peekMin());
        invalid(() -> heap.extractMin());
        for (int i = 100; i >= 0; i--) {
            heap.insert(i);
            check(heap.valid());
        }
        for (int i = 0; i <= 100; i++) {
            check(heap.extractMin() == i);
            check(heap.valid());
        }
        for (int i = 0; i < 100000; i++) {
            int value = random.nextInt(1000);
            heap.insert(value);
            queue.add(value);
            check(heap.peekMin() == queue.peek());
        }
        while (!queue.isEmpty()) check(heap.extractMin() == queue.remove());
        invalid(() -> heap.extractMin());
        System.out.println("All tests passed");
    }

    private static void check(boolean result) {
        if (!result) throw new AssertionError();
    }

    private static void invalid(Runnable action) {
        try {
            action.run();
            throw new AssertionError();
        } catch (IndexOutOfBoundsException | java.util.NoSuchElementException expected) {
        }
    }
}
