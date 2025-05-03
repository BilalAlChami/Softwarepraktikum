package de.thkoeln.swp.bks.managergui.control;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;


public class SuccessFeedbackController {

    public void okayClicked(ActionEvent actionEvent){
        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        window.close();
    }

}
