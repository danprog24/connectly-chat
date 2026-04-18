package com.dannycode.chatApp.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.dannycode.chatApp.model.Message;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MessageCacheService {

    private final LRUCache<String, List<Message>> cache;
    private final int messagesPerRoom;

    // Stats (volatile for visibility across threads without full locking)
    private volatile long hits   = 0;
    private volatile long misses = 0;

    public MessageCacheService(MessageCacheProperties props) {
        this.cache           = new LRUCache<>(props.getCapacity());
        this.messagesPerRoom = props.getMessagesPerRoom();
        log.info("MessageCacheService initialised — capacity={} rooms, {} messages/room",
                props.getCapacity(), messagesPerRoom);
    }

    /**
     * Returns cached messages for the room, or {@code null} on a cache miss.
     *
     * <p>Returns an unmodifiable view so callers cannot corrupt the cached list.
     */
    public List<Message> get(String roomName) {
        List<Message> result = cache.get(roomName);
        if (result != null) {
            hits++;
            log.debug("Cache HIT  for room '{}'", roomName);
            return Collections.unmodifiableList(result);
        }
        misses++;
        log.debug("Cache MISS for room '{}'", roomName);
        return null;
    }

    public void put(String roomName, List<Message> messages) {
        List<Message> toCache = messages;
        if (messages.size() > messagesPerRoom) {
            toCache = new ArrayList<>(
                messages.subList(messages.size() - messagesPerRoom, messages.size())
            );
        } else {
            toCache = new ArrayList<>(messages);
        }
        cache.put(roomName, toCache);
        log.debug("Cached {} messages for room '{}'", toCache.size(), roomName);
    }

    public void append(String roomName, Message message) {
        List<Message> cached = cache.get(roomName);
        if (cached == null) return;   // room not cached; skip

        List<Message> updated = new ArrayList<>(cached);
        updated.add(message);

        if (updated.size() > messagesPerRoom) {
            updated = updated.subList(updated.size() - messagesPerRoom, updated.size());
            updated = new ArrayList<>(updated); // materialise subList
        }

        cache.put(roomName, updated);
        log.debug("Appended message to cache for room '{}'", roomName);
    }

    /**
     * Removes the room from the cache (e.g. after room deletion or bulk update).
     */
    public void evict(String roomName) {
        cache.evict(roomName);
        log.debug("Evicted cache entry for room '{}'", roomName);
    }

  
    public long getHits()   { return hits; }
    public long getMisses() { return misses; }
    public int  getSize()   { return cache.size(); }

    public double getHitRate() {
        long total = hits + misses;
        return total == 0 ? 0.0 : (double) hits / total * 100.0;
    }
}
