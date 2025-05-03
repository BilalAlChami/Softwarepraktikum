package de.thkoeln.swp.bks.managergui.control;

import de.thkoeln.swp.bks.managersteuerung.impl.IKontoManagerSteuerungImpl;
import de.thkoeln.swp.bks.steuerungapi.grenz.KontoGrenz;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class UeberzogeneKontosController implements Initializable {

    @FXML
    private TableView<KontoGrenz> kontoTabelle;

    @FXML
    private TableColumn<KontoGrenz, Integer> kontoID;

    @FXML
    private TableColumn<KontoGrenz, String> name;

    @FXML
    private TableColumn<KontoGrenz, String> vorname;

    @FXML
    private TableColumn<KontoGrenz, Double> kontoStand;

    @FXML
    private TableColumn<KontoGrenz, Double> dispo;

    @FXML
    private TableColumn<KontoGrenz, String> Kontostatus;

    @FXML
    private TableColumn<KontoGrenz, String> art;

    private IKontoManagerSteuerungImpl iKontoManagerSteuerung;
    private KontoGrenz selectedKonto;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        iKontoManagerSteuerung = new IKontoManagerSteuerungImpl();
        updateTableData();
    }

    @FXML
    private void antraege(ActionEvent actionEvent) {
        loadScene(actionEvent);
    }

    @FXML
    private void mahnungSchreiben(ActionEvent actionEvent) {
        if (selectedKonto == null) {
            return;
        }
        loadMahnungSchreibenScene(actionEvent);
    }

    @FXML
    private void kontoSperren(ActionEvent actionEvent) {
        if (selectedKonto == null) {
            return;
        }
        loadKontoSperrenScene(actionEvent);
    }

    @FXML
    private void kontoSelected(MouseEvent mouseEvent) {
        selectedKonto = kontoTabelle.getSelectionModel().getSelectedItem();
    }

    @FXML
    private void tabelleAktualisieren() {
        updateTableData();
    }

    private void updateTableData() {
        List<KontoGrenz> kontoList = iKontoManagerSteuerung.getAlleUeberzogenenKonten();
        for (KontoGrenz konto : kontoList) {
            System.out.println(konto);
        }
        ObservableList<KontoGrenz> tableList = FXCollections.observableArrayList(kontoList);
        kontoTabelle.setItems(tableList);
        kontoTabelle.refresh();
        bindTableColumns();
    }

    private void bindTableColumns() {
        kontoID.setCellValueFactory(new PropertyValueFactory<>("ktoid"));
        name.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKunde().getName()));
        vorname.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKunde().getVorname()));
        kontoStand.setCellValueFactory(new PropertyValueFactory<>("kontostand"));
        dispo.setCellValueFactory(new PropertyValueFactory<>("dispo"));
        Kontostatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        art.setCellValueFactory(new PropertyValueFactory<>("art"));
    }

    private void loadScene(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/manager.fxml"));
            Parent content = loader.load();
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setTitle("SWP - BKS4 - Manager");
            window.setScene(new Scene(content, 800, 500));
            window.show();
        } catch (IOException e) {
            showErrorDialog();
            e.printStackTrace();
        }
    }

    private void loadMahnungSchreibenScene(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mahnungSchreiben.fxml"));
            Parent content = loader.load();
            MahnungSchreibenController controller = loader.getController();
            controller.setData(
                    String.valueOf(selectedKonto.getKtoid()),
                    selectedKonto.getArt(),
                    selectedKonto.getErstellungsdatum().toString(),
                    String.valueOf(selectedKonto.getKontostand()),
                    String.valueOf(selectedKonto.getDispo()),
                    selectedKonto.getStatus(),
                    String.valueOf(selectedKonto.getKunde().getKid()),
                    selectedKonto.getKunde().getTitel(),
                    selectedKonto.getKunde().getName(),
                    selectedKonto.getKunde().getVorname(),
                    selectedKonto.getKunde().getGeburtsdatum().toString()

            );
            setStage(actionEvent, content);
        } catch (IOException e) {
            showErrorDialog();
            e.printStackTrace();
        }
    }

    private void loadKontoSperrenScene(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/kontoSperren.fxml"));
            Parent content = loader.load();
            KontoSperrenController controller = loader.getController();
            controller.setData(
                    selectedKonto.getKtoid(),
                    selectedKonto.getArt(),
                    selectedKonto.getErstellungsdatum().toString(),
                    String.valueOf(selectedKonto.getKontostand()),
                    selectedKonto.getStatus(),
                    String.valueOf(selectedKonto.getKunde().getKid()),
                    selectedKonto.getKunde().getTitel(),
                    selectedKonto.getKunde().getName(),
                    selectedKonto.getKunde().getVorname(),
                    selectedKonto.getKunde().getGeburtsdatum().toString(),
                    selectedKonto.getDispo()
            );
            setStage(actionEvent, content);
        } catch (IOException e) {
            showErrorDialog();
            e.printStackTrace();
        }
    }

    private void setStage(ActionEvent actionEvent, Parent content) {
        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        window.setTitle("SWP - BKS4 - Manager");
        window.setScene(new Scene(content, 800, 500));
        window.show();
    }

    private void showErrorDialog() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("Failed to load the scene.");
        alert.showAndWait();
    }
}
