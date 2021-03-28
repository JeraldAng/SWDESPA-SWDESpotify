package views;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class PlaylistForm extends View {

    public TextField playlistNameTf;
    public Button createBtn;

    @Override
    public void beforeShown() {
        setTitle("Create a new playlist");
    }
}
