package views;

import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class PlaylistChooser extends View {

    public Button selectBtn;
    public TableView playlistTv;
    public TextField searchTf;

    @Override
    public void beforeShown() {
        setTitle("Choose a playlist!");
    }
}
