package ai.betterme;

import java.util.ArrayList;
import java.util.List;


public final class EventBus {


    private record Registration(Class<? extends Event> type, Listener<Event> listener) { }


    private final List<Registration> registrations = new ArrayList<>();


    public <E extends Event> Subscription subscribe(Class<E> type, Listener<E> listener) {
        if(null == type || null == listener){
            throw new IllegalArgumentException("type or listener is null.");
        }
        Registration registration = new Registration(type, (Listener<Event>) listener);
        registrations.add(registration);
        return ()-> registrations.remove(registration);
    }


    public void publish(Event event) {
        if(null == event){
            throw new IllegalArgumentException("event could not be null.");
        }
        List<Registration> registrations = List.copyOf(this.registrations);
        for(Registration registration:registrations){
            if(registration.type().isInstance(event)){
                try {
                    registration.listener().onEvent(event);
                }catch (RuntimeException e){
                    e.printStackTrace();
                }
            }
        }

    }
}
