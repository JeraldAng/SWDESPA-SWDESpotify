package views;

import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class SongChooser extends View {

    public TableView songListTv;
    public Button selectBtn;
    public TextField searchTf;

    @Override
    public void beforeShown() {
        setTitle("Please select some song(s)");
    }
}
