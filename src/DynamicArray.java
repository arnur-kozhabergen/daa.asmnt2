public class DynamicArray {
    private int[] data = new int[10];
    private int size = 0;

    public void add(int value) {
        add(size, value);
    }

    public void add(int index, int value) {
        if (index < 0 || index > size) throw new IndexOutOfBoundsException();
        if (size == data.length) {
            int[] bigger = new int[data.length * 2];
            for (int i = 0; i < size; i++) bigger[i] = data[i];
            data = bigger;
        }
        for (int i = size; i > index; i--) data[i] = data[i - 1];
        data[index] = value;
        size++;
    }

    public int remove(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        int value = data[index];
        for (int i = index; i < size - 1; i++) data[i] = data[i + 1];
        size--;
        return value;
    }

    public int get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        return data[index];
    }

    public boolean contains(int value) {
        for (int i = 0; i < size; i++) if (data[i] == value) return true;
        return false;
    }
}
