package controllers;

import classes.ArtistsPicker;
import classes.ListenerPicker;
import iterator.FollowedListenerCollection;
import iterator.FollowerListenersCollection;
import iterator.Iterator;
import models.Follows;
import models.Session;
import models.Users;
import views.ListenersView;
import views.View;

import java.util.ArrayList;
import java.util.HashMap;

public class ListenersViewController extends Controller {

    private ListenersView listenersView;

    private ListenerPicker listenerPicker;

    private String type;
    private Users user;

    public ListenersViewController(View view) {
        super(view);

        listenersView = (ListenersView) view;

        HashMap<String, Object> data = view.getData();

        if(data.containsKey("type")){
            type = (String) data.get("type");
        }

        if(data.containsKey("user")){
            user = (Users) data.get("user");
        }

        listenerPicker = new ListenerPicker(listenersView.listenersTv, null);

        Iterator<Users> listeners = null;

        if(type.equals("FOLLOWS")){
            FollowedListenerCollection followedListenerCollection = new FollowedListenerCollection(user);
            listeners = followedListenerCollection.getIterator();
        }else if(type.equals("FOLLOWERS")){
            FollowerListenersCollection followerListenersCollection = new FollowerListenersCollection(user);
            listeners = followerListenersCollection.getIterator();
        }

        listenerPicker.setUsersList(listeners);

    }
}
