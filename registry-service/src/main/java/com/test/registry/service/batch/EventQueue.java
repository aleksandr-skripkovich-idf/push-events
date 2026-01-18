package com.test.registry.service.batch;

import com.test.push.events.api.model.EventCreatedMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class EventQueue {

    private final ConcurrentLinkedQueue<EventCreatedMessage> queue = new ConcurrentLinkedQueue<>();

    public void add(EventCreatedMessage message) {
        queue.add(message);
    }

    public List<EventCreatedMessage> drainAll() {
        List<EventCreatedMessage> messages = new ArrayList<>();
        EventCreatedMessage message;
        while ((message = queue.poll()) != null) {
            messages.add(message);
        }
        return messages;
    }

    public int size() {
        return queue.size();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}
