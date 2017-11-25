import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.princeton.cs.algs4.StdRandom;

public class RandomizedQueue<Item> implements Iterable<Item> {
    private Item[] queue;
    private int[] empty;
    private int emptyIdx;
    private int size;

    @SuppressWarnings("unchecked")
    public RandomizedQueue() { // construct an empty randomized queue
        queue = (Item[]) new Object[0];
    }

    public boolean isEmpty() { // is the queue empty?
        return size == 0;
    }

    public int size() { // return the number of items on the queue
        return size;
    }

    @SuppressWarnings("unchecked")
    public void enqueue(Item item) { // add the item
        if (item == null)
            throw new NullPointerException();

        if (queue.length < size + 1) {
            Item[] oldQueue = queue;
            queue = (Item[]) new Object[Math.max(size * 2, 1)];
            for (int j = 0; j < size; j++) {
                queue[j] = oldQueue[j];
            }

            empty = new int[queue.length];
            emptyIdx = 0;
            for (int j = size; j < empty.length; j++) {
                empty[emptyIdx++] = j;
            }
        }

        queue[empty[--emptyIdx]] = item;
        size++;
    }

    public Item dequeue() { // remove and return a random item
        if (size == 0)
            throw new NoSuchElementException();

        while (true) {
            int idx = StdRandom.uniform(0, queue.length);

            if (queue[idx] != null) {
                Item item = queue[idx];
                queue[idx] = null;
                empty[emptyIdx++] = idx;
                size--;

                if (size <= queue.length / 4)
                    shrinkQueue();

                return item;
            }
        }
    }

    private void shrinkQueue() {
        assert size <= queue.length / 4;

        @SuppressWarnings("unchecked")
        Item[] newQ = (Item[]) new Object[queue.length / 4];
        int nidx = 0;

        for (int j = 0; j < queue.length; j++) {
            if (queue[j] != null)
                newQ[nidx++] = queue[j];
        }
        queue = newQ;
        
        empty = new int[queue.length];
        emptyIdx = 0;
        for (int j = size; j < empty.length; j++) {
            empty[emptyIdx++] = j;
        }
    }

    public Item sample() { // return (but do not remove) a random item
        if (size == 0)
            throw new NoSuchElementException();

        while (true) {
            int idx = StdRandom.uniform(0, queue.length);
            if (queue[idx] != null)
                return queue[idx];
        }
    }

    public Iterator<Item> iterator() { // return an independent iterator over
                                       // items in random order
        return new Iterator<Item>() {
            private int left = size;
            private int[] order = new int[size];

            private Iterator<Item> initializeOrder() {
                int oi = 0;
                for (int j = 0; j < queue.length; j++) {
                    if (queue[j] != null) {
                        order[oi++] = j;
                    }
                }
                StdRandom.shuffle(order);
                return this;
            }

            @Override
            public boolean hasNext() {
                return left > 0;
            }

            @Override
            public Item next() {
                if (left <= 0)
                    throw new NoSuchElementException();
                return queue[order[--left]];
            }

        }.initializeOrder();

    }

    public static void main(String[] args) { // unit testing
        RandomizedQueue<Integer> rq = new RandomizedQueue<>();

        for (int j = 0; j < 10; j++) {
            rq.enqueue(j);
        }

        for (int j = 0; j < 5; j++) {
            System.out.println("Sample " + j + ": " + rq.sample());
        }

        for (int j = 0; j < 5; j++) {
            System.out.println("Deque " + j + ": " + rq.dequeue());
        }
        System.out.println("rq size " + rq.size());

        for (int j = 0; j < 20; j++) {
            rq.enqueue(10 + j);
        }
        System.out.println("inserted 5; rq size " + rq.size());

        for (int j = rq.size(); j > 0; j--) {
            System.out.println("Deque " + j + ": " + rq.dequeue());
        }
        System.out.println("deq everything; rq empty " + rq.isEmpty());

        for (int j = 0; j < 10; j++) {
            rq.enqueue(j);
        }
        for (int j : rq) {
            System.out.println("Iterating 1: " + j);
            for (int k : rq) {
                System.out.println("Iterating 2: " + k);
            }
        }
    }
}