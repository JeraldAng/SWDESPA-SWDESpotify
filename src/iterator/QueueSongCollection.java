package iterator;

import models.Songs;
import models.Users;

import java.util.ArrayList;
import java.util.Collection;

public class QueueSongCollection implements Container {

    private Songs[] songs;

    public QueueSongCollection(Collection<Songs> songsList){
        songs = new Songs[songsList.size()];
        Integer i = 0;
        for(Songs song : songsList){
            songs[i] = song;
            i+=1;
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
