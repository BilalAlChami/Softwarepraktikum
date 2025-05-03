package de.thkoeln.swp.bks.managergui.control;
import de.thkoeln.swp.bks.managersteuerung.impl.IAntragManagerSteuerungImpl;
import de.thkoeln.swp.bks.steuerungapi.grenz.AntragGrenz;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
/**
 * Die Klasse EigeneAntraegeController verwaltet die Benutzeroberfläche und die Interaktionen
 * zur Anzeige und Verwaltung der eigenen Anträge des Benutzers in einer Tabellenansicht.
 */
public class EigeneAntraegeController implements Initializable {

    private Stage stage;
    IAntragManagerSteuerungImpl IAntragManagerSteuerung = new IAntragManagerSteuerungImpl();
    public TableView<AntragGrenz> antragTableViewStornieren;




    /**
     * Navigiert zum Menü der Anträge.
     *
     * @param actionEvent das Ereignis, das diese Aktion auslöst
     */
    public void menuAntraege(ActionEvent actionEvent) {

        Parent content;
        try {

            FXMLLoader viewLoader = new FXMLLoader();
            viewLoader.setLocation(getClass().getResource("/manager.fxml"));
            content = viewLoader.load();

            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setTitle("SWP - BKS4 - Manager");
            window.setScene(new Scene(content, 800, 500));
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Storniert ausgewählte Anträge.
     */
    public void optionenStornieren() {


            ObservableList<AntragGrenz> ausgewaehlteAntraege = antragTableViewStornieren.getSelectionModel().getSelectedItems();

            if (ausgewaehlteAntraege == null || ausgewaehlteAntraege.isEmpty()) {
                System.out.println("Kein Antrag ausgewählt.");
                return;
            }

            for (AntragGrenz antrag : ausgewaehlteAntraege) {
                int antragId = antrag.getAtid().intValue();
                boolean istErfolgreichStorniert = IAntragManagerSteuerung.setAntragStorniert(antragId);

                if (istErfolgreichStorniert) {
                    antragTableViewStornieren.getItems().remove(antrag);
                } else {
                    System.out.println("Antrag konnte nicht storniert werden, ID: " + antragId);
                }
            }

            // Entfernen Sie alle ausgewählten Anträge aus der Tabelle
            antragTableViewStornieren.getItems().removeAll(antragTableViewStornieren.getSelectionModel().getSelectedItems());
        }



    /**
     * Initialisiert die Tabelle und lädt die eigenen Anträge des Benutzers.
     *
     * @param url            die URL der Initialisierungsressource
     * @param resourceBundle das ResourceBundle der Initialisierungsressource
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        TableColumn<AntragGrenz, String> idCol2 = new TableColumn<>("ID");
        TableColumn<AntragGrenz, String> typCol2 = new TableColumn<>("Typ");
        TableColumn<AntragGrenz, String> datenCol2 = new TableColumn<>("Daten");
        TableColumn<AntragGrenz, String> kommentareCol2 = new TableColumn<>("Kommentare");
        TableColumn<AntragGrenz, String> statusCol2 = new TableColumn<>("Status");

        idCol2.setCellValueFactory(new PropertyValueFactory<>("atid"));
        typCol2.setCellValueFactory(new PropertyValueFactory<>("typ"));
        datenCol2.setCellValueFactory(new PropertyValueFactory<>("daten"));
        kommentareCol2.setCellValueFactory(new PropertyValueFactory<>("kommentare"));
        statusCol2.setCellValueFactory(new PropertyValueFactory<>("status"));


        List<AntragGrenz> antraege = this.IAntragManagerSteuerung.getEigeneAntraege();

        if (antraege != null && !antraege.isEmpty()) {
            ObservableList<AntragGrenz> data = FXCollections.observableArrayList(antraege);
            this. antragTableViewStornieren.getColumns().addAll(idCol2, typCol2, datenCol2, kommentareCol2, statusCol2);

            this. antragTableViewStornieren.setItems(data);
            this. antragTableViewStornieren.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        }
    }
}