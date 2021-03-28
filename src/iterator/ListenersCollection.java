package iterator;

import models.Users;

import java.util.ArrayList;

public class ListenersCollection implements Container {

    private Users[] users;

    public ListenersCollection(Users user, String keyword){
        ArrayList<Object> bind = new ArrayList<>();
        bind.add(user.user_id);
        bind.add(keyword);
        ArrayList<Users> usersList = (ArrayList<Users>) user.find("user_type = 2 AND user_id <> ? AND username LIKE ?", bind);

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
