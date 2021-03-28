package iterator;

import models.*;

import java.util.ArrayList;

public class PlaylistSongsCollection implements Container {

    private Songs[] songs;

    public PlaylistSongsCollection(Playlists playlists){
        ArrayList<PlaylistSongs> playlistSongs = (ArrayList<PlaylistSongs>) playlists.get(PlaylistSongs.class);
        if(playlistSongs != null){
            songs = new Songs[playlistSongs.size()];
            Integer i = 0;
            for (PlaylistSongs playlistSong : playlistSongs) {
                // TODO: 4/8/2019 check if song is null add error handling
                songs[i] = (Songs) playlistSong.get(Songs.class);
                i+=1;
            }
        }else{
            songs = new Songs[0];
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
