package de.thkoeln.swp.bks.managergui.control;

import de.thkoeln.swp.bks.managersteuerung.impl.IKontoManagerSteuerungImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class KontoSperrenController {
    public Label kontoID;
    public int kid;
    public Label kontoArt;
    public Label kontoDatum;
    public Label kontoStand;
    public Label kontoStatus;
    public Label kundeID;
    public Label kundeTitel;
    public Label kundeName;
    public Label kundeVorname;
    public Label kundeDatum;
    public TextField kontoDispo;
    public double dispo;
    public Button dipositionskreditAendern;
    public Button backButton;
    public IKontoManagerSteuerungImpl iKontoManagerSteuerung = new IKontoManagerSteuerungImpl();

    public void kontoSperren(ActionEvent actionEvent) {
        String erfolg;
        try {
            dispo = Double.parseDouble(kontoDispo.getText());
        }catch (NumberFormatException ex){
            kontoDispo.setText(String.valueOf(dispo));
            return;
        }
        if( iKontoManagerSteuerung.setzeDispoLimit( kid ,dispo)){
            erfolg = "Dispositionskredit wurde geaendert !";
        }else{
            erfolg = "Ein fehler ist aufgetreten !";
        }
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
        Parent content2;
        try {
            FXMLLoader viewLoader2 = new FXMLLoader();
            viewLoader2.setLocation(getClass().getResource("/erfolgFehler.fxml"));
            content2 = viewLoader2.load();

            ErfolgFehlerController erfolgFehlerController = viewLoader2.getController();
            erfolgFehlerController.setData(erfolg);
            Stage dialog = new Stage();
            dialog.setTitle("SWP - BKS4 - Dialog");
            dialog.setScene(new Scene(content2, 400, 200));
            dialog.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void setData(int kontoID,
                        String kontoArt,
                        String kontoDatum,
                        String kontoStand,
                        String kontoStatus,
                        String kundeID,
                        String kundeTitel,
                        String kundeName,
                        String kundeVorname,
                        String kundeDatum,
                        Double kontoDispo
    ){

        this.kontoID.setText(String.valueOf(kontoID));
        this.kid = kontoID;
        this.kontoArt.setText(kontoArt);
        this.kontoDatum.setText(kontoDatum);
        this.kontoStand.setText(kontoStand);
        this.kontoStatus.setText(kontoStatus);
        this.kundeID.setText(kundeID);
        this.kundeTitel.setText(kundeTitel);
        this.kundeName.setText(kundeName);
        this.kundeVorname.setText(kundeVorname);
        this.kundeDatum.setText(kundeDatum);
        this.kontoDispo.setText(String.valueOf(kontoDispo));
        this.dispo = kontoDispo;

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
