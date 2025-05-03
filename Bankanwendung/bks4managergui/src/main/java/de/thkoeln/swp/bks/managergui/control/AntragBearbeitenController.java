package de.thkoeln.swp.bks.managergui.control;

import de.thkoeln.swp.bks.managersteuerung.impl.IAntragManagerSteuerungImpl;
import de.thkoeln.swp.bks.steuerungapi.grenz.AdminGrenz;
import de.thkoeln.swp.bks.steuerungapi.grenz.AntragGrenz;
import javafx.collections.FXCollections;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AntragBearbeitenController implements Initializable {

    public AntragGrenz antragGrenz ;

    @FXML
    public TextArea antragkommentar;

    @FXML
    public Button genehmigenButton;
    @FXML
    public Button ablehnenButton;
    public TextArea antragsdaten;

    public Label antragID;
    public Label antragSachbearbeiter;
    public Label antragStatus;
    public Label antragTyp;

    @FXML
    TableView<AdminGrenz> adminview ;

    @FXML
    TableColumn<Integer, AdminGrenz> IdColumn;

    @FXML
    TableColumn<String, AdminGrenz> vornameColumn;

    @FXML
    TableColumn<String, AdminGrenz> nameColumn;

    @FXML
    TableColumn<String, AdminGrenz> abteilungColumn;
    @FXML
    private TableView<AntragGrenz> kundeAntraege;


    IAntragManagerSteuerungImpl iAntragManagerSteuerungImpl = new IAntragManagerSteuerungImpl();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        ObservableList<AdminGrenz> tt= getAdmin();

        IdColumn.setCellValueFactory(
                new PropertyValueFactory<>("Aid")
        );

        nameColumn.setCellValueFactory(
                new PropertyValueFactory<>("Name")
        );

        vornameColumn.setCellValueFactory(
                new PropertyValueFactory<>("Vorname")
        );

        abteilungColumn.setCellValueFactory(
                new PropertyValueFactory<>("Abteilung")
        );

        adminview.setItems(tt);
    }

    public ObservableList<AdminGrenz> getAdmin(){

        ObservableList<AdminGrenz> adminGrenzs = FXCollections.observableArrayList();

        adminGrenzs.addAll(new IAntragManagerSteuerungImpl().getAlleAdmins());
        return adminGrenzs;

    }

    @FXML
    public void antragGenehmigen(ActionEvent event) {
        AntragGrenz selectedItem = (AntragGrenz) this.kundeAntraege.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            String typValue = selectedItem.getTyp();
            int antragId = selectedItem.getAtid().intValue();

            switch (typValue) {
                case "ko":
                    this.iAntragManagerSteuerungImpl = new IAntragManagerSteuerungImpl();
                    this.iAntragManagerSteuerungImpl.setAntragGenehmigt(selectedItem);

                    Alert successAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    successAlert.setTitle("Erfolgreich");
                    successAlert.setHeaderText("KO-Antrag erfolgreich genehmigt");
                    successAlert.setContentText("Der ausgewählte Antrag wurde erfolgreich genehmigt.");
                    successAlert.showAndWait();
                    break;

                case "kl":
                    Alert errorAlertKL = new Alert(Alert.AlertType.ERROR);
                    errorAlertKL.setTitle("Fehler");
                    errorAlertKL.setHeaderText("Unbekannter Antragstyp");
                    errorAlertKL.setContentText("Bitte wählen Sie nur Anträge vom Typ KO und KS.");
                    errorAlertKL.showAndWait();
                    break;

                default:
                    Alert errorAlertUnknown = new Alert(Alert.AlertType.ERROR);
                    errorAlertUnknown.setTitle("Fehler");
                    errorAlertUnknown.setHeaderText("Unbekannter Antragstyp");
                    errorAlertUnknown.setContentText("Der ausgewählte Antrag hat einen unbekannten Typ.");
                    errorAlertUnknown.showAndWait();
                    break;
            }
        } else {
            Alert noSelectionAlert = new Alert(Alert.AlertType.INFORMATION);
            noSelectionAlert.setTitle("Fehler");
            noSelectionAlert.setHeaderText("Keine Auswahl");
            noSelectionAlert.setContentText("Bitte wählen Sie einen Antrag aus.");
            noSelectionAlert.showAndWait();
        }
    }

    @FXML
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void antragAblehnenAction(ActionEvent actionEvent) {
        AntragGrenz selectedItem = kundeAntraege.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            showAlert("Fehler", "Kein Antrag ausgewählt", Alert.AlertType.ERROR);
            return;
        }

        if (!("ko".equals(selectedItem.getTyp())||"ks".equals(selectedItem.getTyp()))) {
            showAlert("Fehler", "Unbekannter Antragstyp", Alert.AlertType.ERROR);
            return;
        }

        int antragId = selectedItem.getAtid();
        boolean success = iAntragManagerSteuerungImpl.setAntragAbgelehnt(antragId);

        if (success) {
            showAlert("Erfolgreich", "Antrag erfolgreich abgelehnt", Alert.AlertType.CONFIRMATION);
        } else {
            showAlert("Fehler", "Fehler beim Ablehnen des Antrags", Alert.AlertType.ERROR);
        }
    }

    public void antraege(ActionEvent actionEvent) {

        Parent content;
        try {
            FXMLLoader viewLoader = new FXMLLoader();
            viewLoader.setLocation(getClass().getResource("/manager.fxml"));
            content = viewLoader.load();
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setTitle("SWP - BKS4 - Manager");
            window.setScene(new Scene(content, 800, 500));
            window.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setData(AntragGrenz antrag) {
        antragID.setText(String.valueOf(antrag.getAtid()));
        antragSachbearbeiter.setText(String.valueOf(antrag.getSachbearbeiter()));
        antragStatus.setText(antrag.getStatus());
        antragsdaten.setText(antrag.getDaten());
    }

}