import java.util.ArrayList;

public class QueueExample {
    private ArrayList<String> queue;

    public QueueExample() {
        this.queue = new ArrayList<>();
    }

    public void add(String element) {
        queue.add(element);
    }

    public String remove() {
        if (queue.isEmpty()) {
            return null;
        }
        String element = queue.get(0);
        queue.remove(0);
        return element;
    }

    public String peek() {
        if (queue.isEmpty()) {
            return null;
        }
        return queue.get(0);
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int size() {
        return queue.size();
    }

    public static void main(String[] args) {
        QueueExample q = new QueueExample();
        q.add("25");
        q.add("10");
        System.out.println(q.peek()); // Apple
        System.out.println(q.size()); // 3
        System.out.println(q.remove()); // Apple
        System.out.println(q.isEmpty()); // false
    }
}
