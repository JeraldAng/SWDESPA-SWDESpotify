package observer;

public class FollowersObserver extends Observer {

    public FollowersObserver (Subject subject){
        this.subject = subject;
        this.subject.attach(this);
    }

    @Override
    public void update() {

    }
}
