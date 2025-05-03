package de.thkoeln.swp.bks.managergui.control;

import de.thkoeln.swp.bks.managersteuerung.impl.IAntragManagerSteuerungImpl;
import de.thkoeln.swp.bks.managersteuerung.impl.IKontoManagerSteuerungImpl;
import de.thkoeln.swp.bks.steuerungapi.grenz.AntragGrenz;
import de.thkoeln.swp.bks.steuerungapi.grenz.KontoGrenz;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class KontoLimitController {
    @FXML
    public Label antragID;
    @FXML
    public Label antragTyp;
    @FXML
    public Label antragSachbearbeiter;
    @FXML
    public Label antragStatus;
    @FXML
    public Label antragDaten;
    @FXML
    public Label kontoID;
    @FXML
    public Label kontoArt;
    @FXML
    public Label kontoDatum;
    @FXML
    public Label kontoStand;
    @FXML
    public Label kontoStatus;
    @FXML
    public Label kundeID;
    @FXML
    public Label kundeTitel;
    @FXML
    public Label kundeName;
    @FXML
    public Label kundeVorname;
    @FXML
    public Label kundeDatum;
    @FXML
    private TextArea antragKommentare;
    @FXML
    private TextField  kontoDispo;
    @FXML
    public Button backButton;
    @FXML
    public Button dispositionskreditAendern;
    @FXML
    public Button antragAblehnen;

    @FXML
    private TableView<AntragGrenz> kundeAntraege;

    private int kid;
    private double dispo;

    private IKontoManagerSteuerungImpl iKontoManagerSteuerung = new IKontoManagerSteuerungImpl();
    private IAntragManagerSteuerungImpl iAntragManagerSteuerung = new IAntragManagerSteuerungImpl();

    private AntragGrenz aktuellesAntrag;

    public KontoLimitController() {
    }


    @FXML
    private void backButtonAction(ActionEvent actionEvent) {
        try {
            FXMLLoader viewLoader = new FXMLLoader();
            viewLoader.setLocation(getClass().getResource("/manager.fxml"));
            Parent content = viewLoader.load();
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setTitle("SWP - BKS4 - Manager");
            window.setScene(new Scene(content, 800, 500));
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setAntrag(AntragGrenz antrag) {
        this.aktuellesAntrag = antrag;
        antragID.setText(String.valueOf(antrag.getAtid()));
        antragTyp.setText(antrag.getTyp());
        antragSachbearbeiter.setText(String.valueOf(antrag.getSachbearbeiter()));
        antragStatus.setText(antrag.getStatus());
        antragDaten.setText(antrag.getDaten());
        antragKommentare.setText(antrag.getKommentare());

        KontoGrenz konto = this.iKontoManagerSteuerung.getKontoById(aktuellesAntrag.getAtid());

        if (konto != null) {
            this.kontoID.setText("ID: " + konto.getKtoid());
            this.kontoArt.setText("Art: " + konto.getArt());
            this.kontoDatum.setText("Erstellungsdatum: " + konto.getErstellungsdatum().toString());
            this.kontoStand.setText("Stand: " + konto.getKontostand());
            this.kontoStatus.setText("Status: " + konto.getStatus());
            this.kundeID.setText("ID: " + konto.getKunde().getKid());
            this.kundeTitel.setText("Titel: " + konto.getKunde().getTitel());
            this.kundeName.setText("Name: " + konto.getKunde().getName());
            this.kundeVorname.setText("Vorname: " + konto.getKunde().getVorname());
            this.kundeDatum.setText("Geb. Datum: " + konto.getKunde().getGeburtsdatum().toString());
            this.kontoDispo.setText(String.valueOf(konto.getDispo()));
            this.kid = konto.getKtoid();
            this.dispo = konto.getDispo();
        } else {
            System.out.println("Konto konnte nicht gefunden werden.");
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

        if (!"kl".equals(selectedItem.getTyp())) {
            showAlert("Fehler", "Unbekannter Antragstyp", Alert.AlertType.ERROR);
            return;
        }

        int antragId = selectedItem.getAtid();
        boolean success = iAntragManagerSteuerung.setAntragAbgelehnt(antragId);

        if (success) {
            showAlert("Erfolgreich", "Antrag erfolgreich abgelehnt", Alert.AlertType.CONFIRMATION);
        } else {
            showAlert("Fehler", "Fehler beim Ablehnen des Antrags", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void dispoAendern(ActionEvent actionEvent) {
        String erfolg;
        try {
            dispo = Double.parseDouble(kontoDispo.getText());
        } catch (NumberFormatException ex) {
            kontoDispo.setText(String.valueOf(dispo));
            return;
        }

        if (iKontoManagerSteuerung.setzeDispoLimit(kid, dispo) && iAntragManagerSteuerung.setAntragGenehmigt(new AntragGrenz())) {
            erfolg = "Dispo wurde geändert!";
        } else {
            erfolg = "Ein Fehler ist aufgetreten!";
        }
        showAlert("Ergebnis", erfolg, Alert.AlertType.INFORMATION);
        loadManagerView(actionEvent);
    }

    @FXML
    private void updateData() {
        try {
            this.kid = Integer.parseInt(this.kontoID.getText().replace("ID: ", ""));
        } catch (NumberFormatException ex) {
            return;
        }
        try {
            KontoGrenz konto = iKontoManagerSteuerung.getKontoById(this.kid);

            this.kontoArt.setText("Art: " + konto.getArt());
            this.kontoDatum.setText("Erstellungsdatum: " + konto.getErstellungsdatum().toString());
            this.kontoStand.setText("Stand: " + konto.getKontostand());
            this.kontoStatus.setText("Status: " + konto.getStatus());
            this.kundeID.setText("ID: " + konto.getKunde().getKid());
            this.kundeTitel.setText("Titel: " + konto.getKunde().getTitel());
            this.kundeName.setText("Name: " + konto.getKunde().getName());
            this.kundeVorname.setText("Vorname: " + konto.getKunde().getVorname());
            this.kundeDatum.setText("Geb. Datum: " + konto.getKunde().getGeburtsdatum().toString());
            this.kontoDispo.setText(String.valueOf(konto.getDispo()));
            this.dispo = konto.getDispo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadManagerView(ActionEvent actionEvent) {
        try {
            FXMLLoader viewLoader = new FXMLLoader();
            viewLoader.setLocation(getClass().getResource("/manager.fxml"));
            Parent content = viewLoader.load();
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setTitle("SWP - BKS4 - Manager");
            window.setScene(new Scene(content, 800, 500));
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
