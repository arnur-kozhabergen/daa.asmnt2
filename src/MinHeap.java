import java.util.NoSuchElementException;

public class MinHeap {
    private int[] data = new int[10];
    private int size = 0;

    public void insert(int value) {
        if (size == data.length) {
            int[] bigger = new int[data.length * 2];
            for (int i = 0; i < size; i++) bigger[i] = data[i];
            data = bigger;
        }
        int i = size++;
        while (i > 0 && data[(i - 1) / 2] > value) {
            data[i] = data[(i - 1) / 2];
            i = (i - 1) / 2;
        }
        data[i] = value;
    }

    public int peekMin() {
        if (size == 0) throw new NoSuchElementException();
        return data[0];
    }

    public int extractMin() {
        int minimum = peekMin();
        int last = data[--size];
        int i = 0;
        while (2 * i + 1 < size) {
            int child = 2 * i + 1;
            if (child + 1 < size && data[child + 1] < data[child]) child++;
            if (last <= data[child]) break;
            data[i] = data[child];
            i = child;
        }
        if (size > 0) data[i] = last;
        return minimum;
    }
}
