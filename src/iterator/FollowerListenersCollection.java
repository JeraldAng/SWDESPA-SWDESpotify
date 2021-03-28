package iterator;

import models.Follows;
import models.Session;
import models.Users;

import java.util.ArrayList;

public class FollowerListenersCollection implements Container {

    private Users[] users;

    public FollowerListenersCollection(Users user){
        ArrayList<Object> bind = new ArrayList<>();

        Users currentUser = (Users) Session.get("user");

        bind.add(user.user_id);

        ArrayList<Users> usersList = new ArrayList<>();

        ArrayList<Follows> follows = (ArrayList<Follows>) new Follows().find("user_id_followed = ?", bind);
        if(follows != null) {
            for (Follows follow : follows) {
                Users userFollowed =  follow.getUser();
                if (userFollowed != null && userFollowed.user_type == 2 && currentUser.user_id != userFollowed.user_id) {
                    usersList.add(userFollowed);
                }
            }
        }

        if(usersList == null){
            usersList = new ArrayList<>();
        }

        users = new Users[usersList.size()];

        Integer i = 0;
        for(Users theUser : usersList){
            users[i] = theUser;
            i += 1;
        }
    }

    @Override
    public Iterator<Users> getIterator() {
        return new UsersIterator();
    }

    private class UsersIterator implements Iterator<Users> {

        int index = 0;

        @Override
        public boolean hasNext() {
            if(index < users.length){
                return true;
            }
            return false;
        }

        @Override
        public Users next() {

            if(this.hasNext()){
                return users[index++];
            }
            return null;
        }

        @Override
        public void first() {
            index = 0;
        }
    }
}
