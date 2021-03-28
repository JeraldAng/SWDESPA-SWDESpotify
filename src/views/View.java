package views;

import controllers.Controller;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class View {

    private String className;
    private String fxml;
    private String controllerName;

    protected Stage stage;
    protected Scene scene;
    protected Parent root;

    protected HashMap<String, Object> data;

    protected View parent;

    private Callback<Integer, Void> onCloseCallBack;

    private Integer exitCode = 0;
    private Controller controller;

    public View(){
        //init();
    }

    protected String setControllerName(){
        return "controllers."+className+"Controller";
    }

    private HashMap<String, Object> undeclared = new HashMap<>();

    private void traverseChildren(Parent parent){

        if(parent instanceof TabPane){
            for(Tab t : ((TabPane)parent).getTabs()){
                if(t.getId() != null){
                    try {
                        Field f = getClass().getDeclaredField(t.getId());
                        f.set(this, scene.lookup("#" + t.getId()));
                    } catch (Exception e) {
                        if(!undeclared.containsKey(t.getId())) {
                            undeclared.put(t.getId(), t);
                        }
                    }
                }
                if(t.getContent() instanceof Parent) {
                    traverseChildren((Parent)t.getContent());
                }
            }
        }

        for(Node node : parent.getChildrenUnmodifiable()){
            if(node instanceof TabPane){
                traverseChildren(((Parent)node));
            }
            if(node instanceof ScrollPane){
                declareNode(((ScrollPane) node).getContent());
                declareNode(node);
                node = ((ScrollPane) node).getContent();
            }else{
                declareNode(node);
            }
            if(node instanceof Parent){
                traverseChildren((Parent)node);
            }
        }
    }

    private void declareNode(Node node){
        try {
            if (node.getId() == null) return;
            //System.out.println(node.getId());
            Field f = getClass().getDeclaredField(node.getId());
            f.set(this, scene.lookup("#" + node.getId()));
        } catch (Exception e) {
            if(!undeclared.containsKey(node.getId())) {
                undeclared.put(node.getId(), node);
            }
        }
    }

    public View getParent(){
        return parent;
    }

    private void init(Stage s){

        this.className = getClass().getSimpleName();
        this.fxml = "/fxml/"+className+".fxml";
        this.controllerName = setControllerName();

        if(s == null) {
            stage = new Stage();
        }else{
            stage = s;
        }

        stage.setOnHidden(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if(onCloseCallBack != null) {
                    onCloseCallBack.call(exitCode);
                }
                onClose(exitCode);
            }
        });

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            scene = new Scene(root);

            this.root = root;

            traverseChildren(scene.getRoot());

            StringBuilder define = new StringBuilder();

            for (Map.Entry<String, Object> entry : undeclared.entrySet()){
                System.out.println(entry.getKey() + " is not defined in View " + className + ".");
                define.append("public " + entry.getValue().getClass().getSimpleName() + " " + entry.getKey() + ";\n");
            }

            if(!define.toString().isEmpty()){
                System.out.println("Copy paste this to define in view: \n"+define.toString());
            }

            Class controllerClass = Class.forName(controllerName);
            Class[] cArg = new Class[1];
            cArg[0] = View.class;
            controller = (Controller) controllerClass.getDeclaredConstructor(cArg).newInstance(this);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Controller class not found for View "+className+".");
            //e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public String getFXMLUrl(){
        return this.fxml;
    }

    final public void show(){
        init(null);
        beforeShown();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.sizeToScene();
        stage.show();
        onShown();
    }

    final public void setParent(View parent){
        this.parent = parent;
    }

    final public void showDialog(View parent){
        init(null);
        beforeShown();
        setParent(parent);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.sizeToScene();
        stage.show();
        onShown();
    }

    public View loadToPane(Class<?> viewClass, Pane pane, View parent, HashMap<String, Object> data){
        pane.getChildren().clear();
        return addToPane(viewClass, pane, parent, data);
    }

    public View addToPane(Class<?> viewClass, Pane pane, View parent, HashMap<String, Object> data){
        try {
            View v = ((View)viewClass.newInstance());
            v.setParent(parent);
            v.setData(data);
            v.beforeShown();
            v.init(null);
            pane.getChildren().add(v.getScene().getRoot());
            v.onShown();
            return v;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Scene getScene(){
        return this.scene;
    }

    public Parent getRoot(){
        return this.root;
    }

    public Stage getStage(){
        return this.stage;
    }

    public void setData(HashMap<String, Object> data){
        this.data = data;
    }

    public View setScene(Class<?> viewClass){
        return setScene(viewClass, null);
    }

    public View setScene(Class<?> viewClass, HashMap<String, Object> data){
        try {
            View v = ((View)viewClass.newInstance());
            v.init(getStage());
            v.beforeShown();
            v.setData(data);
            v.stage.setScene(v.getScene());
            v.stage.sizeToScene();
            v.onShown();
            return v;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void close(){
        stage.close();
    }

    public void setExitCode(Integer exitCode){
        this.exitCode = exitCode;
    }

    public void setOnClose(Callback<Integer, Void> callback){
        onCloseCallBack = callback;
    }

    private void onClose(Integer exitCode){

    }

    public void remove(){
        ((Pane)parent.getRoot()).getChildren().remove(getRoot());
        onRemove();
        if(this.onRemoveCallback != null){
            onRemoveCallback.call(null);
        }
    }

    public void onRemove(){

    }

    private Callback<Void, Void> onRemoveCallback;
    public void onRemoveCallback(Callback<Void, Void> callback){
        this.onRemoveCallback = callback;
    }

    public void setTitle(String title) {
        stage.setTitle(title);
    }

    public Controller getController(){
        return this.controller;
    }

    public void beforeShown(){

    }

    public void onShown(){

    }

    public HashMap<String,Object> getData (){
        return this.data;
    }

}
