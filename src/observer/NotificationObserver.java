package observer;

public class NotificationObserver extends Observer {

    public NotificationObserver (Subject subject){
        this.subject = subject;
        this.subject.attach(this);
    }

    @Override
    public void update() {

    }
}
