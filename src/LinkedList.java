public class LinkedList {
    private static class Node {
        int value;
        Node next;

        Node(int value) {
            this.value = value;
        }
    }

    private Node head;
    private Node tail;
    private int size = 0;

    public void add(int value) {
        add(size, value);
    }

    public void add(int index, int value) {
        if (index < 0 || index > size) throw new IndexOutOfBoundsException();
        Node node = new Node(value);
        if (index == 0) {
            node.next = head;
            head = node;
        } else if (index == size) {
            tail.next = node;
        } else {
            Node previous = at(index - 1);
            node.next = previous.next;
            previous.next = node;
        }
        if (index == size) tail = node;
        size++;
    }

    public int remove(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        Node removed;
        if (index == 0) {
            removed = head;
            head = head.next;
        } else {
            Node previous = at(index - 1);
            removed = previous.next;
            previous.next = removed.next;
            if (removed == tail) tail = previous;
        }
        size--;
        if (size == 0) tail = null;
        return removed.value;
    }

    public int get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        return at(index).value;
    }

    public boolean contains(int value) {
        for (Node node = head; node != null; node = node.next) {
            if (node.value == value) return true;
        }
        return false;
    }

    private Node at(int index) {
        Node node = head;
        for (int i = 0; i < index; i++) node = node.next;
        return node;
    }
}
