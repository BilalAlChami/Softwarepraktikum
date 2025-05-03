module de.thkoeln.swp.bks.managergui {

    exports de.thkoeln.swp.bks.managergui.application;
    requires de.thkoeln.swp.bks.steuerungapi;
    
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;

    requires de.thkoeln.swp.bks.managersteuerung;

    opens de.thkoeln.swp.bks.managergui.control to javafx.fxml;
}
