package com.example.facelock.Event;

import java.util.*;

/**
 * Created by huyhq4 on 5/9/2020.
 */
public class EventManager {
    private static Map<EventType, Set<EventCallback>> eventHandlers = new HashMap<>();
    private static Map<EventType, Set<EventCallback>> onceEventHandlers = new HashMap<>();

    public static void addEventListener(EventType type, EventCallback handler){
        Set handlers = eventHandlers.get(type);
        if(handlers == null){
            handlers = new HashSet();
            eventHandlers.put(type, handlers);
        }
        if(handlers.contains(handler)) return;
        handlers.add(handler);
    }

    public static void addOnceEventListener(EventType type, EventCallback handler){
        Set handlers = onceEventHandlers.get(type);
        if(handlers == null){
            handlers = new HashSet();
            onceEventHandlers.put(type, handlers);
        }
        if(handlers.contains(handler)) return;
        handlers.add(handler);
    }

    public static void removeEventListener(EventType type){
        onceEventHandlers.remove(type);
        eventHandlers.remove(type);

    }

    public static void disPatchEvent(EventType type, Map eventData){
        System.out.println("disPatchEvent: " + type);
        Set handlers = eventHandlers.get(type);
        if(handlers != null) {
            Iterator iterator = handlers.iterator();
            while (iterator.hasNext()) {
                EventCallback handler = (EventCallback) iterator.next();
                handler.execute(eventData);
            }
        }
        handlers = onceEventHandlers.get(type);
        if(handlers != null) {
            Iterator iterator = handlers.iterator();
            while (iterator.hasNext()) {
                EventCallback handler = (EventCallback) iterator.next();
                handlers.remove(handler);
                handler.execute(eventData);
            }
        }
    }
}
