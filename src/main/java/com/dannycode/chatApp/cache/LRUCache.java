package com.dannycode.chatApp.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Generic thread-safe LRU Cache backed by a HashMap + doubly-linked list.
 *
 * <p>Thread safety is provided by a {@link ReentrantReadWriteLock}:
 * <ul>
 *   <li>Multiple concurrent reads are allowed simultaneously.</li>
 *   <li>Writes (put, eviction) acquire an exclusive lock.</li>
 * </ul>
 *
 * <p>Both {@code get} and {@code put} run in O(1) average time.
 *
 * @param <K> key type
 * @param <V> value type
 */
public class LRUCache<K, V> {

    // -------------------------------------------------------------------------
    // Internal node
    // -------------------------------------------------------------------------

    private class Node {
        K key;
        V value;
        Node prev, next;

        Node(K key, V value) {
            this.key   = key;
            this.value = value;
        }
    }

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    private final int capacity;
    private final Map<K, Node> map;

    /** Sentinel head — next is always MRU. */
    private final Node head;
    /** Sentinel tail — prev is always LRU. */
    private final Node tail;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public LRUCache(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be > 0");
        this.capacity = capacity;
        this.map      = new HashMap<>();
        this.head     = new Node(null, null);
        this.tail     = new Node(null, null);
        head.next     = tail;
        tail.prev     = head;
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Returns the cached value for {@code key}, or {@code null} if absent.
     * Promotes the entry to most-recently-used.
     */
    public V get(K key) {
        lock.writeLock().lock();   // write lock because we reorder the list
        try {
            Node node = map.get(key);
            if (node == null) return null;
            moveToFront(node);
            return node.value;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Inserts or updates {@code key → value}, evicting the LRU entry if needed.
     */
    public void put(K key, V value) {
        lock.writeLock().lock();
        try {
            Node existing = map.get(key);
            if (existing != null) {
                existing.value = value;
                moveToFront(existing);
                return;
            }
            if (map.size() == capacity) evictLRU();
            Node node = new Node(key, value);
            map.put(key, node);
            insertAtFront(node);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Removes {@code key} from the cache entirely (e.g. after room deletion).
     */
    public void evict(K key) {
        lock.writeLock().lock();
        try {
            Node node = map.remove(key);
            if (node != null) removeNode(node);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /** Returns the number of entries currently in the cache. */
    public int size() {
        lock.readLock().lock();
        try { return map.size(); }
        finally { lock.readLock().unlock(); }
    }

    public int capacity() { return capacity; }

    // -------------------------------------------------------------------------
    // Private helpers  (caller must hold write lock)
    // -------------------------------------------------------------------------

    private void moveToFront(Node node) {
        removeNode(node);
        insertAtFront(node);
    }

    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void insertAtFront(Node node) {
        node.next      = head.next;
        node.prev      = head;
        head.next.prev = node;
        head.next      = node;
    }

    private void evictLRU() {
        Node lru = tail.prev;
        removeNode(lru);
        map.remove(lru.key);
    }
}
