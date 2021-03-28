package iterator;

import models.Playlists;
import models.Songs;
import models.Users;

import java.util.ArrayList;

public class SongsNotInPlaylistCollection implements Container {

    private Songs[] songs;

    public SongsNotInPlaylistCollection(Users user, Playlists playlist, String keyword) {
        ArrayList<Object> bind = new ArrayList<>();
        bind.add(user.user_id);
        bind.add(playlist.playlist_id);
        bind.add(keyword);
        ArrayList<Songs> songsList = (ArrayList<Songs>) new Songs().find("user_id = ? AND song_id NOT IN (SELECT song_id FROM playlist_songs WHERE playlist_id = ?) AND name LIKE ?", bind);

        songs = new Songs[songsList.size()];

        Integer i = 0;
        for (Songs song : songsList) {
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

            if (index < songs.length) {
                return true;
            }
            return false;
        }

        @Override
        public Songs next() {

            if (this.hasNext()) {
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