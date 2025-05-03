package de.thkoeln.swp.bks.managergui.control;

import de.thkoeln.swp.bks.managersteuerung.impl.IKontoManagerSteuerungImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class MahnungSchreibenController {
    public Label kontoID;
    public Label kontoArt;
    public Label kontoDatum;
    public Label kontoStand;

    public Label dispo;
    public Label kontoStatus;
    public Label kundeID;
    public Label kundeTitel;
    public Label kundeName;
    public Label kundeVorname;
    public Label kundeDatum;
    public Button mahnungSpeichern;
    public Button backButton;
    public TextArea mahnungInhalt;
    public IKontoManagerSteuerungImpl iKontoManagerSteuerung = new IKontoManagerSteuerungImpl();


    public void mahnungSpeichern(ActionEvent actionEvent) {
        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        String erfolg;
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", ".txt");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showSaveDialog(window);
        if (file != null){
            final String inhalt = this.mahnungInhalt.getText();
            this.iKontoManagerSteuerung.erstelleMahnung(file, inhalt);
        }
        Parent content;
        try {
            FXMLLoader viewLoader = new FXMLLoader();
            viewLoader.setLocation(getClass().getResource("/ueberzogeneKonten.fxml"));
            content = viewLoader.load();
            window.setTitle("SWP - BKS4 - Manager");
            window.setScene(new Scene(content, 800, 500));
            window.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        assert file != null;
        File f = new File(file.getAbsolutePath());
        if(f.exists() && !f.isDirectory()) {
            erfolg = "Mahnung wurde erfolgreich gespeichert!";
        }else {
            erfolg = "Ein Fehler ist aufgetretten !!!";
        }
        Parent content2;
        try {
            FXMLLoader viewLoader2 = new FXMLLoader();
            viewLoader2.setLocation(getClass().getResource("/erfolgFehler.fxml"));
            content2 = viewLoader2.load();
            ErfolgFehlerController erfolgFehlerController = viewLoader2.getController();
            erfolgFehlerController.setData(erfolg);
            Stage dialog = new Stage();
            dialog.setTitle("SWP - BKS4 - Manager");
            dialog.setScene(new Scene(content2, 400, 200));
            dialog.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void setData (String kontoID,
                         String kontoArt,
                         String kontoDatum,
                         String kontoStand,
                         String kontoDispo,
                         String kontoStatus,
                         String kundeID,
                         String kundeTitel,
                         String kundeName,
                         String kundeVorname,
                         String kundeDatume){
        this.kontoID.setText(kontoID);
        this.kontoArt.setText(kontoArt);
        this.kontoDatum.setText(kontoDatum);
        this.kontoStand.setText(kontoStand);
        this.dispo.setText(kontoDispo);
        this.kontoStatus.setText(kontoStatus);
        this.kundeID.setText(kundeID);
        this.kundeTitel.setText(kundeTitel);
        this.kundeName.setText(kundeName);
        this.kundeVorname.setText(kundeVorname);
        this.kundeDatum.setText(kundeDatume);

    }
    public void backButtonAction(ActionEvent actionEvent) {
        Parent content;
        try {
            FXMLLoader viewLoader = new FXMLLoader();
            viewLoader.setLocation(getClass().getResource("/ueberzogeneKonten.fxml"));
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
}
