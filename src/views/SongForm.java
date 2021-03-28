package views;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class SongForm extends View {

    public TextField nameTf;
    public TextField genreTf;
    public ComboBox albumCb;
    public TextField yearTf;
    public Button saveBtn;


    @Override
    public void beforeShown() {
        super.beforeShown();

        setTitle("Edit Song");
    }
}
