package iterator;

import models.Follows;
import models.Session;
import models.Songs;
import models.Users;

import java.util.ArrayList;

public class FollowedUsersSongsCollection implements Container {

    private Songs[] songs;

    public FollowedUsersSongsCollection(Users user){

        ArrayList<Follows> follows = (ArrayList<Follows>) user.get(Follows.class);
        Users currentUser = (Users) Session.get("user");

        ArrayList<Songs> songsList = new ArrayList<>();

        if(follows != null) {
            for (Follows follow : follows) {
                Users followedUser = follow.getUserFollowed();
                if (followedUser.user_type == 1 && followedUser.user_id != currentUser.user_id) {
                    ArrayList<Songs> userSongs = (ArrayList<Songs>) followedUser.get(Songs.class);
                    if(userSongs != null) {
                        songsList.addAll(userSongs);
                    }
                }
            }
        }

        songs = new Songs[songsList.size()];

        Integer i = 0;
        for(Songs song : songsList){
            songs[i] = song;
            i += 1;
        }
    }

    @Override
    public Iterator<Songs> getIterator() {
        return new SongsIterator();
    }

    private class SongsIterator implements Iterator<Songs> {

        int index = 0;

        @Override
        public boolean hasNext() {

            if(index < songs.length){
                return true;
            }
            return false;
        }

        @Override
        public Songs next() {

            if(this.hasNext()){
                return songs[index++];
            }
            return null;
        }

        @Override
        public void first() {
            index = 0;
        }
    }
}
