package de.thkoeln.swp.bks.managergui.control;
import de.thkoeln.swp.bks.managersteuerung.impl.IActivateComponentImpl;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


public class ManagerController implements Initializable{
    public Integer managerId ;
    IAntragManagerSteuerungImpl iAntragManagerSteuerung = new IAntragManagerSteuerungImpl();

    @FXML
    TableView<AntragGrenz> antragview ;

    @FXML
    TableColumn<String, AntragGrenz> TypColumn;

    @FXML
    TableColumn<Integer, AntragGrenz> IdColumn;

    @FXML
    TableColumn<String, AntragGrenz> KommentareColumn;

    @FXML
    TableColumn<String, AntragGrenz> StatusColumn;

    @FXML
    TableColumn<String, AntragGrenz> DatenColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        managerId = IActivateComponentImpl.getManagerID();

        ObservableList<AntragGrenz>  tt= getAntreage();
        TypColumn.setCellValueFactory(
                new PropertyValueFactory<>("Typ")
        );
        IdColumn.setCellValueFactory(
                new PropertyValueFactory<>("Aid")
        );

        StatusColumn.setCellValueFactory(
                new PropertyValueFactory<>("Status")
        );

        DatenColumn.setCellValueFactory(
                new PropertyValueFactory<>("Daten")
        );

        KommentareColumn.setCellValueFactory(
                new PropertyValueFactory<>("Kommentare")
        );

        antragview.setItems(tt);

        antragview.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                AntragGrenz selectedItem = antragview.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    handleDoubleClick(selectedItem);
                } else {
                    System.out.println("Kein Element ausgewählt");
                }
            }
        });
    }

    private void handleDoubleClick(AntragGrenz selectedItem) {
        try {
            if (selectedItem.getTyp().toString().equals("ko") || selectedItem.getTyp().toString().equals("ks")) {
                System.out.println("Doppelklick auf Element: " + selectedItem);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AntragBearbeiten.fxml"));
                Parent root = loader.load();

                AntragBearbeitenController controller = loader.getController();
                controller.setData(selectedItem);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Antrag Bearbeiten");
                stage.show();
            }
            else if (selectedItem.getTyp().toString().equals("kl")) {
                System.out.println("Doppelklick auf Element: " + selectedItem);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/kontolimitAntraege.fxml"));
                Parent root = loader.load();

                KontoLimitController controller = loader.getController();
                controller.setAntrag(selectedItem);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Kontolimit Antrag");
                stage.show();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void eigeneAntraege(ActionEvent actionEvent){
        Parent content;
        try {

            FXMLLoader viewLoader = new FXMLLoader();
            viewLoader.setLocation(getClass().getResource("/EigeneAntraege.fxml"));
            content = viewLoader.load();

            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setTitle("SWP - BKS4 - Manager");
            window.setScene(new Scene(content, 800, 500));
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ueberzogeneKonten(ActionEvent actionEvent){
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
    public ObservableList<AntragGrenz> getAntreage(){
        ObservableList<AntragGrenz> antragGrenzs = FXCollections.observableArrayList();
        antragGrenzs.addAll(new IAntragManagerSteuerungImpl().getAntragListKl());
        antragGrenzs.addAll(iAntragManagerSteuerung.getAntragListKsKo());
        return antragGrenzs;
    }


    public boolean antragAblehnen(AntragGrenz antrag) {
        return new IAntragManagerSteuerungImpl().setAntragAbgelehnt(antrag.getAtid());
    }

    public boolean antragGenehmigen(AntragGrenz antrag) {
        return new IAntragManagerSteuerungImpl().setAntragGenehmigt(antrag);
    }
    @FXML
    public void kskoClicked() {
        managerId = IActivateComponentImpl.getManagerID();
        List<AntragGrenz> klAntraege = iAntragManagerSteuerung.getAntragListKsKo();
        ObservableList<AntragGrenz> tableKlFilter = FXCollections.observableArrayList();

        for (AntragGrenz antrag : klAntraege) {
            tableKlFilter.add(antrag);
        }
        antragview.setItems(tableKlFilter);
    }


        public void antragBearbeitenClicked(ActionEvent actionEvent) {
        try {
            Parent content;

            AntragGrenz ag = antragview.getSelectionModel().getSelectedItem();
            FXMLLoader viewLoader = new FXMLLoader();

            viewLoader.setLocation(getClass().getResource("/AntragBearbeiten.fxml"));
            content = viewLoader.load();

            Stage stage;
            stage = new Stage();
            stage.setScene(new Scene(content, 800, 500));
            AntragBearbeitenController abc = viewLoader.getController();
            abc.setData(ag);
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
    @FXML
    public void gestelltenAntraegeClicked(){

    }
    public void klFilterAction(ActionEvent actionEvent) {
        managerId = IActivateComponentImpl.getManagerID();
        List<AntragGrenz> klAntraege = iAntragManagerSteuerung.getAntragListKl();
        ObservableList<AntragGrenz> tableKlFilter = FXCollections.observableArrayList();

        for (AntragGrenz antrag : klAntraege) {
            tableKlFilter.add(antrag);
        }
        antragview.setItems(tableKlFilter);

    }
    public void sachBearbeiterProfil(ActionEvent actionEvent) {
    }
}
