import java.util.HashMap;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/8/12 20:34
 */
public class LRUCache {
    class LinkedNode{
        int key;
        int value;
        LinkedNode prev;
        LinkedNode next;
        public LinkedNode(){

        }
        public LinkedNode(int key, int value){
            this.key = key;
            this.value = value;
        }

    }

    private HashMap<Integer, LinkedNode> cache = new HashMap<>();
    private int size;
    private int capacity;
    private LinkedNode head;
    private LinkedNode tail;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        size = 0;
        head = new LinkedNode();
        tail = new LinkedNode();
        head.next = tail;
        tail.prev = head;
    }

    public int get(int key) {
        LinkedNode node = cache.get(key);
        if(node == null){
            return -1;
        }
        moveToHead(node);
        return node.value;
    }

    public void put(int key, int value) {
        LinkedNode node = cache.get(key);
        if(node == null){
            LinkedNode newNode = new LinkedNode(key, value);
            cache.put(key, newNode);
            addToHead(newNode);
            ++size;
            if(size > capacity){
                LinkedNode tail = removeTail();
                cache.remove(tail.key);
                --size;
            }
        } else {
            node.value = value;
            moveToHead(node);
        }
    }
    public void addToHead(LinkedNode node){
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }
    public void removeNode(LinkedNode node){
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }
    public void moveToHead(LinkedNode node){
        removeNode(node);
        addToHead(node);
    }

    public LinkedNode removeTail(){
        LinkedNode res = tail.prev;
        removeNode(res);
        return res;
    }

    public static void main(String[] args) {
        LRUCache lruCache = new LRUCache(2);
        lruCache.put(1, 1);
        lruCache.put(2, 2);
        lruCache.get(1);
        lruCache.put(3, 3);
        lruCache.get(2);
        lruCache.put(4, 4);
        lruCache.get(1);
        lruCache.get(3);
        lruCache.get(4);
        System.out.println();
    }

}
