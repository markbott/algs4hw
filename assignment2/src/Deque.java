import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {
    private Node head;
    private Node tail;
    private int size;

    private class Node {
        private Item item;
        private Node next;
        private Node prev;
    }

    public Deque() { // construct an empty deque

    }

    public boolean isEmpty() { // is the deque empty?
        return size == 0;
    }

    public int size() { // return the number of items on the deque
        return size;
    }

    public void addFirst(Item item) { // add the item to the front
        if (item == null)
            throw new NullPointerException();

        Node second = head;
        head = new Node();
        head.item = item;
        head.next = second;
        head.prev = null;

        if (size == 0) {
            tail = head;
        } else {
            second.prev = head;
        }
        size++;
    }

    public void addLast(Item item) { // add the item to the end
        if (item == null)
            throw new NullPointerException();

        Node secondLast = tail;
        tail = new Node();
        tail.item = item;
        tail.prev = secondLast;
        tail.next = null;

        if (size == 0) {
            head = tail;
        } else {
            secondLast.next = tail;
        }

        size++;
    }

    public Item removeFirst() { // remove and return the item from the front
        if (isEmpty())
            throw new NoSuchElementException();

        Item first = head.item;
        head = head.next;
        size--;

        if (size == 0) {
            tail = null;
        } else {
            head.prev = null;
        }

        return first;
    }

    public Item removeLast() { // remove and return the item from the end
        if (isEmpty())
            throw new NoSuchElementException();

        Item last = tail.item;
        tail = tail.prev;
        size--;

        if (size == 0) {
            head = null;
        } else {
            tail.next = null;
        }

        return last;
    }

    public Iterator<Item> iterator() { // return an iterator over items in order
                                       // from front to end
        return new Iterator<Item>() {
            private Node current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public Item next() {
                if (current == null)
                    throw new NoSuchElementException();

                Item item = current.item;
                current = current.next;
                return item;
            }

        };
    }

    public static void main(String[] args) { // unit testing
        Deque<Integer> d = new Deque<>();

        assert d.isEmpty();

        try {
            d.removeFirst();
            assert false;
        } catch (NoSuchElementException nse) {
            // good
        }

        d.addFirst(2);
        d.addFirst(1);
        d.addLast(3);
        d.addLast(4);

        System.out.println("1..4");
        for (Integer i : d) {
            System.out.println(i);
        }

        System.out.println("1 = " + d.removeFirst());
        System.out.println("2 = " + d.removeFirst());
        System.out.println("3 .. 4");
        for (Integer i : d) {
            System.out.println(i);
        }
        System.out.println("3 = " + d.removeFirst());
        System.out.println("4 = " + d.removeFirst());

        try {
            System.out.println("2 = " + d.removeFirst());
            assert false;
        } catch (NoSuchElementException nsee) {
            // good
        }

        d.addLast(3);
        d.addLast(4);
        d.addFirst(2);
        d.addFirst(1);

        System.out.println("1..4");
        for (Integer i : d) {
            System.out.println(i);
        }

        System.out
                .println("4..1 " + d.removeLast() + " " + d.removeLast() + " " + d.removeLast() + " " + d.removeLast());
    }
}