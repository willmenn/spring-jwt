package com.spring.jwt;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class ExpiringMap<T, V> {

    private ConcurrentLinkedQueue<ExpireObject> queue;
    private ConcurrentHashMap<T, ExpireObjectForMap<V>> map;

    public ExpiringMap() {
        this.queue = new ConcurrentLinkedQueue<>();
        this.map = new ConcurrentHashMap<>();
    }

    public Object getOrDefault(T key, V defaultValue) {
        ExpireObjectForMap object = map.get(key);
        if (object == null) {
            return defaultValue;
        } else if (object.expireObject.time.isBefore(now())) {
            removeCompletely(key, object);
        }
        expireLastData();
        return defaultValue;
    }

    private void removeCompletely(T key, ExpireObjectForMap object) {
        queue.remove(object.expireObject);
        map.remove(key);
    }

    public Object put(T key, V value, LocalDateTime expirationTime) {
        ExpireObject expireObject = new ExpireObject(key);
        queue.add(expireObject);
        return map.put(key, new ExpireObjectForMap<>(value, expireObject));
    }

    public void clean() {
        queue.stream()
                .filter(t -> t.time.isBefore(now()))
                .collect(Collectors.toList())
                .forEach(elem -> {
                    map.remove(elem.key);
                    queue.remove(elem);
                });
    }

    private synchronized void expireLastData() {
        LocalDateTime now = now();
        ExpireObject maybeExpiredObject = queue.remove();
        if (!maybeExpiredObject.time.isBefore(now)) {
            queue.add(maybeExpiredObject);
        } else {
            map.remove(maybeExpiredObject.key);
        }
    }

    private LocalDateTime now() {
        return LocalDateTime.now();
    }

    private class ExpireObject<T> {
        private LocalDateTime time;

        private T key;

        private ExpireObject(T key) {
            this.key = key;
            this.time = now();
        }

    }

    private class ExpireObjectForMap<V> {
        private V value;
        private ExpireObject expireObject;

        private ExpireObjectForMap(V value, ExpireObject expireObject) {
            this.value = value;
            this.expireObject = expireObject;
        }
    }
}
