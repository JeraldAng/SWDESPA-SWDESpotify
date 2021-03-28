package iterator;

import models.LikedSongs;
import models.Songs;
import models.Users;

import java.util.ArrayList;

public class UsersLikedSongsCollection implements Container {

    private Songs[] songs;

    public UsersLikedSongsCollection(Users user, String keyword){
        ArrayList<Songs> songsList = new ArrayList<>();
        ArrayList<Object> bind = new ArrayList<>();
        bind.add(user.user_id);
        bind.add(keyword);
        ArrayList<LikedSongs> likedSongs = (ArrayList<LikedSongs>) new LikedSongs().find("user_id = ? AND name LIKE ?", bind);

        if (likedSongs != null) {
            for (LikedSongs likedSong : likedSongs) {
                songsList.add((Songs) likedSong.get(Songs.class));
            }
        }

        if(songsList == null){
            songsList = new ArrayList<>();
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
