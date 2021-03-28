package iterator;

import models.AlbumSongs;
import models.Albums;
import models.Songs;

import java.util.ArrayList;

public class AlbumSongsCollection implements Container {

    private Songs[] songs;

    public AlbumSongsCollection(Albums album){
        ArrayList<AlbumSongs> albumSongs = (ArrayList<AlbumSongs>) album.get(AlbumSongs.class);
        if(albumSongs != null){
            songs = new Songs[albumSongs.size()];
            Integer i = 0;
            for(AlbumSongs albumSong : albumSongs){
                songs[i] = (Songs)albumSong.get(Songs.class);
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
