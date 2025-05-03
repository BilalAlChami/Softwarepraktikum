package de.thkoeln.swp.bks.managersteuerung.impl;

import de.thkoeln.swp.bks.bksdbmodel.entities.Konto;
import de.thkoeln.swp.bks.bksdbmodel.impl.IDatabaseImpl;
import de.thkoeln.swp.bks.datenhaltungapi.ISonderKontoService;
import de.thkoeln.swp.bks.steuerungapi.grenz.KontoGrenz;
import de.thkoeln.swp.bks.steuerungapi.grenz.KundeGrenz;
import de.thkoeln.swp.bks.steuerungapi.manager.IKontoManagerSteuerung;
import javax.persistence.EntityManager;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.module.FindException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author Soufian El Berkani
 */


public class IKontoManagerSteuerungImpl implements IKontoManagerSteuerung {

    /**
     * Implementierung der IKontoManagerSteuerung-Schnittstelle.
     * Diese Klasse verwaltet die Konten der Anwendung.
     */
    private EntityManager em;
    private ISonderKontoService iSonderKontoService;
    private static final Logger LOGGER = Logger.getLogger(IKontoManagerSteuerungImpl.class.getName());

    /**
     * Konstruktor, der den EntityManager initialisiert und die erforderlichen Dienste lädt.
     */
    public IKontoManagerSteuerungImpl() {
        IDatabaseImpl iDatabaseImpl = new IDatabaseImpl();
        this.em = iDatabaseImpl.getEntityManager();
        boolean loadError = false;
        Iterator<ISonderKontoService> iterator = ServiceLoader.load(ISonderKontoService.class).iterator();
        if (iterator.hasNext()) {
            this.iSonderKontoService = iterator.next();
            this.iSonderKontoService.setEntityManager(this.em);
        } else {
            loadError = true;
        }
        if (loadError)
            throw new FindException("ISonderKontoService-Implementierung wurde nicht gefunden");
    }

    /**
     * Holt die Liste aller überzogenen Konten.
     * /LF110/ Anzeige der überzogenen Konten
     * @return Eine Liste von KontoGrenz-Objekten.
     */
    @Override
    public List<KontoGrenz> getAlleUeberzogenenKonten() {
        List<KontoGrenz> ueberzogenGrenzKontos = new ArrayList<>();
        List<Konto> ueberzogenKontos = this.iSonderKontoService.getUeberzogeneKontos();
        for (Konto ueberzogenKonto : ueberzogenKontos) {
            ueberzogenGrenzKontos.add(new KontoGrenz(
                    ueberzogenKonto.getKtoid(),
                    ueberzogenKonto.getArt(),
                    ueberzogenKonto.getErstellungsdatum(),
                    ueberzogenKonto.getKontostand(),
                    ueberzogenKonto.getDispo(),
                    ueberzogenKonto.getStatus(),
                    new KundeGrenz(
                            ueberzogenKonto.getKunde().getKid(),
                            ueberzogenKonto.getKunde().getTitel(),
                            ueberzogenKonto.getKunde().getName(),
                            ueberzogenKonto.getKunde().getVorname(),
                            ueberzogenKonto.getKunde().getGeburtsdatum(),
                            ueberzogenKonto.getKunde().getAdresse(),
                            ueberzogenKonto.getKunde().getTelefon(),
                            ueberzogenKonto.getKunde().getGeschlecht(),
                            ueberzogenKonto.getKunde().getFamilientstand(),
                            ueberzogenKonto.getKunde().getNationalitaet()
                    )
            ));
        }
        return ueberzogenGrenzKontos;
    }

    /**
     * Erstellt eine Mahnung und speichert sie in einer Datei.
     * /LF113/ Mahnung schreiben
     * @param file Die Datei, in der die Mahnung gespeichert werden soll.
     * @param s Der Inhalt der Mahnung.
     * @return true, wenn die Mahnung erfolgreich erstellt wurde, sonst false.
     */
    @Override
    public boolean erstelleMahnung(File file, String s) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
            writer.println(s);
            return true;
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "File konnte nicht gespeichert werden!!!", ex);
            return false;
        }
    }

    /**
     * Holt ein Konto anhand seiner ID.
     * /LF85/ Kontolimit-Antrag bearbeiten
     * @param i Die ID des Kontos.
     * @return Ein KontoGrenz-Objekt, das die Kontoinformationen enthält.
     */
    @Override
    public KontoGrenz getKontoById(int i) {
        Konto konto = this.iSonderKontoService.getKontoByID(i);
        return new KontoGrenz(
                konto.getKtoid(),
                konto.getArt(),
                konto.getErstellungsdatum(),
                konto.getKontostand(),
                konto.getDispo(),
                konto.getStatus(),
                new KundeGrenz(
                        konto.getKunde().getKid(),
                        konto.getKunde().getTitel(),
                        konto.getKunde().getName(),
                        konto.getKunde().getVorname(),
                        konto.getKunde().getGeburtsdatum(),
                        konto.getKunde().getAdresse(),
                        konto.getKunde().getTelefon(),
                        konto.getKunde().getGeschlecht(),
                        konto.getKunde().getFamilientstand(),
                        konto.getKunde().getNationalitaet()
                )
        );
    }

    /**
     * Setzt ein neues Dispo-Limit für ein Konto.
     * /LF116/ Konto sperren bzw. entsperren
     * @param i Die ID des Kontos.
     * @param v Das neue Dispo-Limit.
     * @return true, wenn das Dispo-Limit erfolgreich gesetzt wurde, sonst false.
     */
    @Override
    public boolean setzeDispoLimit(int i, double v) {

        em.getTransaction().begin();
        boolean dispoGesetzt = this.iSonderKontoService.neuesDispoSetzen(i, v);
        if (dispoGesetzt) {
            em.getTransaction().commit();
        } else {
            em.getTransaction().rollback();
        }
        return dispoGesetzt;
    }
}
