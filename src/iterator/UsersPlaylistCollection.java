package iterator;

import models.Playlists;
import models.Songs;
import models.Users;

import java.util.ArrayList;

public class UsersPlaylistCollection implements Container {

    private Playlists[] playlists;

    public UsersPlaylistCollection(Users user, String keyword){
        ArrayList<Object> bind = new ArrayList<>();
        bind.add(user.user_id);
        bind.add(keyword);
        ArrayList<Playlists> playlists = (ArrayList<Playlists>) new Playlists().find("user_id = ? AND name LIKE ?", bind);

        if(playlists == null){
            playlists = new ArrayList<>();
        }

        this.playlists = new Playlists[playlists.size()];

        Integer i = 0;
        for(Playlists playlist : playlists){
            this.playlists[i] = playlist;
            i += 1;
        }
    }

    @Override
    public Iterator<Playlists> getIterator() {
        return new PlaylistIterator();
    }

    private class PlaylistIterator implements Iterator<Playlists> {

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
