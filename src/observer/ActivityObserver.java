package observer;

public class ActivityObserver extends Observer {

    public ActivityObserver (Subject subject){
        this.subject = subject;
        this.subject.attach(this);
    }

    @Override
    public void update() {

    }
}
