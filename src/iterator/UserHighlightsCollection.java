package iterator;

import models.Highlights;
import models.Playlists;
import models.Users;

import java.util.ArrayList;

public class UserHighlightsCollection implements Container {

    private Playlists[] playlists;

    public UserHighlightsCollection(Users user, String keyword){
        ArrayList<Playlists> playlistList = new ArrayList<>();
        ArrayList<Object> bind = new ArrayList<>();
        bind.add(user.user_id);
        bind.add(keyword);
        ArrayList<Highlights> highlights = (ArrayList<Highlights>) new Highlights().find("user_id = ? AND name LIKE ?", bind);

        if (highlights != null) {
            for (Highlights highlight : highlights) {
                playlistList.add((Playlists) highlight.get(Playlists.class));
            }
        }

        if(playlistList == null){
            playlistList = new ArrayList<>();
        }

        playlists = new Playlists[playlistList.size()];

        Integer i = 0;
        for(Playlists playlist : playlistList){
            playlists[i] = playlist;
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
            if (index < playlists.length) {
                return true;
            }
            return false;
        }

        @Override
        public Playlists next() {

            if (this.hasNext()) {
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

