package iterator;

import models.Activities;
import models.Users;

import java.util.ArrayList;
import java.util.HashMap;

public class UserActivitiesCollection implements Container {

    private Activities[] activities;

    public UserActivitiesCollection(Users user){

        HashMap<String, Object> options = new HashMap<>();
        ArrayList<Object> bind = new ArrayList<>();
        bind.add(user.user_id);
        options.put("where", "user_id = ?");
        options.put("order", "date_created DESC");
        options.put("bind", bind);

        ArrayList<Activities> ActivitiesList = (ArrayList<Activities>) new Activities().find(options);;

        if(ActivitiesList == null){
            ActivitiesList = new ArrayList<>();
        }

        activities = new Activities[ActivitiesList.size()];

        Integer i = 0;
        for(Activities theUser : ActivitiesList){
            activities[i] = theUser;
            i += 1;
        }
    }

    @Override
    public Iterator<Activities> getIterator() {
        return new ActivitiesIterator();
    }

    private class ActivitiesIterator implements Iterator<Activities> {

        int index = 0;

        @Override
        public boolean hasNext() {
            if(index < activities.length){
                return true;
            }
            return false;
        }

        @Override
        public Activities next() {

            if(this.hasNext()){
                return activities[index++];
            }
            return null;
        }

        @Override
        public void first() {
            index = 0;
        }
    }

}
