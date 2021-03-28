package iterator;

import models.Albums;
import models.Users;

import java.util.ArrayList;

public class UserAlbumsCollection implements Container {

    private Albums[] albums;

    public UserAlbumsCollection(Users user, String keyword){
        Albums albums = new Albums();
        ArrayList<Object> bind = new ArrayList<>();
        bind.add(user.user_id);
        bind.add(keyword);

        ArrayList<Albums> albumsList = (ArrayList<Albums>) albums.find("user_id = ? AND album_name LIKE ?", bind);

        if(albumsList == null){
            albumsList = new ArrayList<>();
        }

        this.albums = new Albums[albumsList.size()];

        Integer i = 0;
        for(Albums album : albumsList){
            this.albums[i] = album;
            i += 1;
        }
    }

    @Override
    public Iterator<Albums> getIterator() {
        return new AlbumIterator();
    }

    private class AlbumIterator implements Iterator<Albums> {

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
