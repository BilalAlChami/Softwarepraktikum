package de.thkoeln.swp.bks.managergui.control;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ErfolgFehlerController {
    public Label dialogText;
    public Button okButton;


    public void setData (String dialogText){
        this.dialogText.setText(dialogText);
    }

    public void okButtonAction(ActionEvent actionEvent) {
        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        if(window != null) window.close();
    }
}
