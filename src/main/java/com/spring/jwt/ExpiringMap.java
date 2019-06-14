package com.spring.jwt;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
            return defaultValue;
        }
        expireLastData();
        return object.value;
    }

    private void removeCompletely(T key, ExpireObjectForMap object) {
        queue.remove(object.expireObject);
        map.remove(key);
    }

    public Object put(T key, V value, LocalDateTime expirationTime) {
        ExpireObject expireObject;
        if (!queue.contains(Objects.hash(expirationTime))) {
            expireObject = new ExpireObject(key);
            queue.add(expireObject);
        } else {
            expireObject = queue.poll();
            expireObject.addKey(key);
        }

        return map.put(key, new ExpireObjectForMap<>(value, expireObject));
    }

    public void clean() {
        queue.stream()
                .filter(t -> t.time.isBefore(now()))
                .collect(Collectors.toList())
                .forEach(elem -> {
                    elem.keys.forEach(k -> map.remove(k));
                    queue.remove(elem);
                });
    }

    private synchronized void expireLastData() {
        LocalDateTime now = now();
        ExpireObject maybeExpiredObject = queue.remove();

        if (!maybeExpiredObject.time.isBefore(now)) {
            queue.add(maybeExpiredObject);
        } else {
            maybeExpiredObject.keys.forEach(k -> map.remove(k));
        }
    }

    private LocalDateTime now() {
        return LocalDateTime.now();
    }

    private class ExpireObject<T> {
        private LocalDateTime time;
        private List<T> keys;

        private ExpireObject(T keys) {
            this.keys = new ArrayList<>();
            this.keys.add(keys);
            this.time = now();
        }

        void addKey(T key) {
            this.keys.add(key);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ExpireObject<?> that = (ExpireObject<?>) o;
            return Objects.equals(time, that.time);
        }

        @Override
        public int hashCode() {
            return Objects.hash(time, keys);
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
