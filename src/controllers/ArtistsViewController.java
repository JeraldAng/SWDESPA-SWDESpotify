package controllers;

import classes.ArtistsPicker;
import iterator.FollowedArtistsCollection;
import iterator.FollowerArtistsCollection;
import iterator.Iterator;
import models.Follows;
import models.Session;
import models.Users;
import views.ArtistsView;
import views.View;

import java.util.ArrayList;
import java.util.HashMap;

public class ArtistsViewController extends Controller {

    private ArtistsView artistsView;
    private String type;
    private Users user;

    private ArtistsPicker artistsPicker;

    public ArtistsViewController(View view) {
        super(view);

        artistsView = (ArtistsView) view;

        HashMap<String, Object> data = view.getData();

        if(data.containsKey("type")){
            type = (String) data.get("type");
        }

        if(data.containsKey("user")){
            user = (Users) data.get("user");
        }

        artistsPicker = new ArtistsPicker(artistsView.artistsTv, null);

        Iterator<Users> artists = null;

        if(type.equals("FOLLOWS")){
            FollowedArtistsCollection followedArtistsCollection = new FollowedArtistsCollection(user);
            artists = followedArtistsCollection.getIterator();
        }else if(type.equals("FOLLOWERS")){
            FollowerArtistsCollection followerArtistsCollection = new FollowerArtistsCollection(user);
            artists = followerArtistsCollection.getIterator();
        }

        artistsPicker.setUsersList(artists);
    }
}
