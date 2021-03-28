package iterator;

import models.Notifications;
import models.Users;

import java.util.ArrayList;
import java.util.HashMap;

public class UsersNotificationCollection implements Container {

    private Notifications[] notifications;

    public UsersNotificationCollection(Users user, boolean unseen){

        HashMap<String, Object> options = new HashMap<>();
        ArrayList<Object> bind = new ArrayList<>();

        bind.add(user.user_id);
        options.put("where", "user_id = ?");
        options.put("order", "date_created DESC");
        options.put("bind", bind);

        if(unseen){
            options.replace("where", "user_id = ? AND seen = 0");
        }

        ArrayList<Notifications> notificationsList = (ArrayList<Notifications>) new Notifications().find(options);

        if(notificationsList == null){
            notificationsList = new ArrayList<>();
        }

        notifications = new Notifications[notificationsList.size()];

        Integer i = 0;

        for(Notifications notification : notificationsList){
            notifications[i] = notification;
            i += 1;
        }
    }

    @Override
    public Iterator<Notifications> getIterator() {
        return new NotificationsIterator();
    }

    private class NotificationsIterator implements Iterator<Notifications> {

        int index = 0;

        @Override
        public boolean hasNext() {
            if(index < notifications.length){
                return true;
            }
            return false;
        }

        @Override
        public Notifications next() {

            if(this.hasNext()){
                return notifications[index++];
            }
            return null;
        }

        @Override
        public void first() {
            index = 0;
        }

    }
}
