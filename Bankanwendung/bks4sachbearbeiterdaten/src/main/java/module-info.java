module de.thkoeln.swp.bks.sachbearbeiterdaten {

    requires de.thkoeln.swp.bks.datenhaltungapi;
    requires de.thkoeln.swp.bks.bksdbmodel;
    requires java.logging;

    provides de.thkoeln.swp.bks.datenhaltungapi.IAntragSach with de.thkoeln.swp.bks.sachbearbeiterdaten.IAntragSachImpl;
    provides de.thkoeln.swp.bks.datenhaltungapi.IUeberweisungsVorlage with de.thkoeln.swp.bks.sachbearbeiterdaten.IUeberweisungsVorlageImpl;
    provides de.thkoeln.swp.bks.datenhaltungapi.ICRUDSach with de.thkoeln.swp.bks.sachbearbeiterdaten.ICRUDSachImpl;


}
