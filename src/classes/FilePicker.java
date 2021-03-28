package classes;

import javafx.scene.Parent;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.util.List;

public class FilePicker {

    private FileChooser fileChooser;
    private Window parent;

    public FilePicker(String windowTitle, Parent parent){
        this.parent = parent.getScene().getWindow();
        fileChooser = new FileChooser();
        fileChooser.setTitle(windowTitle);
    }

    public void addFilter(String description, String extension){
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(description, extension));
    }

    public List<File> choose(){
        return fileChooser.showOpenMultipleDialog(parent);
    }

    public File chooseOne(){
        return fileChooser.showOpenDialog(parent);
    }

}
