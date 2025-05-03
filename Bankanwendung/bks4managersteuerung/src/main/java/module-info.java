module de.thkoeln.swp.bks.managersteuerung {

    exports de.thkoeln.swp.bks.managersteuerung.impl;

    requires de.thkoeln.swp.bks.componentcontroller;
    requires de.thkoeln.swp.bks.bksdbmodel;
    requires de.thkoeln.swp.bks.datenhaltungapi;
    requires de.thkoeln.swp.bks.steuerungapi;
    requires java.logging;
    
    uses de.thkoeln.swp.bks.datenhaltungapi.ICRUDManager;
    uses de.thkoeln.swp.bks.datenhaltungapi.IAntragManager;
    uses de.thkoeln.swp.bks.datenhaltungapi.IAntragStellenAdmin;
    uses de.thkoeln.swp.bks.datenhaltungapi.ISonderKontoService;




}
