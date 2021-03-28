package views;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

public class AlbumForm extends View {

    public ImageView albumCoverIv;
    public TextField albumNameTf;
    public Button albumCoverBtn;
    public Button createAlbumBtn;

    @Override
    public void beforeShown() {
        setTitle("Create new album!");
    }
}
