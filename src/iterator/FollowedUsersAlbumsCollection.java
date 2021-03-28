package iterator;

import models.Follows;
import models.Albums;
import models.Session;
import models.Users;

import java.util.ArrayList;

public class FollowedUsersAlbumsCollection implements Container {

    private Albums[] albums;

    public FollowedUsersAlbumsCollection(Users user){

        ArrayList<Follows> follows = (ArrayList<Follows>) user.get(Follows.class);
        Users currentUser = (Users) Session.get("user");

        ArrayList<Albums> albumsList = new ArrayList<>();

        if(follows != null) {
            for (Follows follow : follows) {
                Users followedUser = follow.getUserFollowed();
                if (followedUser.user_type == 1 && followedUser.user_id != currentUser.user_id) {
                    ArrayList<Albums> userAlbums = (ArrayList<Albums>) followedUser.get(Albums.class);
                    if(userAlbums != null) {
                        albumsList.addAll(userAlbums);
                    }
                }
            }
        }

        albums = new Albums[albumsList.size()];

        Integer i = 0;
        for(Albums album : albumsList){
            albums[i] = album;
            i += 1;
        }
    }

    @Override
    public Iterator<Albums> getIterator() {
        return new FollowedUsersAlbumsCollection.AlbumsIterator();
    }

    private class AlbumsIterator implements Iterator<Albums> {

        int index = 0;

        @Override
        public boolean hasNext() {

            if(index < albums.length){
                return true;
            }
            return false;
        }

        @Override
        public Albums next() {

            if(this.hasNext()){
                return albums[index++];
            }
            return null;
        }

        @Override
        public void first() {
            index = 0;
        }
    }
}
