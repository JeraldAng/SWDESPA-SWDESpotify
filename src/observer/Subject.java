package observer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Subject {

    private List<Observer> observers = new ArrayList<Observer>();
    private HashMap<String, Object> state;

    public HashMap<String, Object> getState() {
        return state;
    }

    public void setState(HashMap<String, Object> state) {
        this.state = state;
        notifyAllObservers();
    }

    public void attach(Observer observer) {
        observers.add(observer);
    }

    public void notifyAllObservers() {
        for (Observer observer : observers) {
            observer.update();
        }
    }
}
