package com.graphAlgorithm.view.componenets;

import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Random;

public class Dialog {
    private ChoiceDialog choiceDialog;
    private Alert informationDialog;
    private LinkedList<String> choiceDialogsOptions = new LinkedList<>();
    private TextInputDialog textInputDialog;

    public void makeChoiceDialog(String title, String headerText, String contentText){
         choiceDialog = new ChoiceDialog<>(choiceDialogsOptions.get(0), choiceDialogsOptions);
         choiceDialog.setTitle(title);
         choiceDialog.setHeaderText(headerText);
         choiceDialog.setContentText(contentText);
         choiceDialog.setGraphic(new ImageView(new Image(new File("/source/choice.png")
                         .toURI().toString())));
         Stage stage = (Stage) choiceDialog.getDialogPane().getScene().getWindow();
         javafx.scene.image.Image iconImage = new javafx.scene.image.Image(new File("/source/options.png")
                 .toURI().toString());
         stage.getIcons().add(iconImage);
    }

    public int getSelectedItem_Integer(){
        if (choiceDialog.getSelectedItem() == "Random Vertex") {
            Random rand = new Random();
            return rand.nextInt(choiceDialogsOptions.size()-1);
        }else{
            return Integer.parseInt(choiceDialog.getSelectedItem().toString());
        }
    }

    public void setChoiceOptionWithRandomVertesec(LinkedList<LinkedList<GraphNode>> nodesList){
        setChoiceOption(nodesList);
        choiceDialogsOptions.addFirst("Random Vertex");
    }

    public void setChoiceOption(LinkedList<LinkedList<GraphNode>> nodesList){
        choiceDialogsOptions.clear();
        for (LinkedList<GraphNode> graphNodes : nodesList)
            choiceDialogsOptions.add(String.valueOf(graphNodes.get(0).getIndex()));
    }

    public void showInformationDialog(String Header, String ContentText){
        informationDialog = new Alert(Alert.AlertType.INFORMATION);
        informationDialog.setHeaderText(Header);
        informationDialog.setContentText(ContentText);
        informationDialog.showAndWait();
    }

    public LinkedList<String> getChoiceDialogsOptions() {
        return choiceDialogsOptions;
    }

    public ChoiceDialog getChoiceDialog() {
        return choiceDialog;
    }

    public Integer NumberInputDialogShow(String Title) throws NumberFormatException{
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("");
        dialog.setContentText(Title);
        Optional<String> result = dialog.showAndWait();
        if(Integer.parseInt(result.get()) != 0){
            return Integer.parseInt(result.get());
        }else return null;
    }

    private boolean validate(String text)
    {
        return text.matches("[0-9]*");
    }
}
