package iterator;

import models.Follows;
import models.Session;
import models.Playlists;
import models.Users;

import java.util.ArrayList;

public class FollowedUsersPlaylistsCollection implements Container {

    private Playlists[] playlists;

    public FollowedUsersPlaylistsCollection(Users user){

        ArrayList<Follows> follows = (ArrayList<Follows>) user.get(Follows.class);
        Users currentUser = (Users) Session.get("user");

        ArrayList<Playlists> playLists = new ArrayList<>();

        if(follows != null) {
            for (Follows follow : follows) {
                Users followedUser = follow.getUserFollowed();
                if (followedUser.user_id != currentUser.user_id) {
                    ArrayList<Playlists> userPlaylists = (ArrayList<Playlists>) followedUser.get(Playlists.class);
                    if(userPlaylists != null) {
                        playLists.addAll(userPlaylists);
                    }
                }
            }
        }

        playlists = new Playlists[playLists.size()];

        Integer i = 0;
        for(Playlists playlist : playLists){
            playlists[i] = playlist;
            i += 1;
        }
    }

    @Override
    public Iterator<Playlists> getIterator() {
        return new FollowedUsersPlaylistsCollection.PlaylistsIterator();
    }

    private class PlaylistsIterator implements Iterator<Playlists> {

        int index = 0;

        @Override
        public boolean hasNext() {

            if(index < playlists.length){
                return true;
            }
            return false;
        }

        @Override
        public Playlists next() {

            if(this.hasNext()){
                return playlists[index++];
            }
            return null;
        }

        @Override
        public void first() {
            index = 0;
        }
    }
}
