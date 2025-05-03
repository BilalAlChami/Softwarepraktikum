package de.thkoeln.swp.bks.managersteuerung.impl;

import de.thkoeln.swp.bks.bksdbmodel.entities.Admin;
import de.thkoeln.swp.bks.bksdbmodel.entities.Antrag;
import de.thkoeln.swp.bks.bksdbmodel.impl.IDatabaseImpl;
import de.thkoeln.swp.bks.bksdbmodel.services.IDatabase;
import de.thkoeln.swp.bks.datenhaltungapi.IAntragManager;
import de.thkoeln.swp.bks.datenhaltungapi.IAntragStellenAdmin;
import de.thkoeln.swp.bks.datenhaltungapi.ICRUDManager;
import de.thkoeln.swp.bks.datenhaltungapi.ISonderKontoService;
import de.thkoeln.swp.bks.steuerungapi.grenz.AdminGrenz;
import de.thkoeln.swp.bks.steuerungapi.grenz.AntragGrenz;
import de.thkoeln.swp.bks.steuerungapi.manager.IAntragManagerSteuerung;

import javax.persistence.EntityManager;
import java.lang.module.FindException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Logger;


/**
 * @author Soufian El Berkani
 * @autor Bilal Al Chami
 * @autor Omar Samig
 */

public class IAntragManagerSteuerungImpl implements IAntragManagerSteuerung {
    /**
     * Implementierung der IAntragManagerSteuerung-Schnittstelle.
     * Diese Klasse verwaltet die Anträge der Anwendung.
     */

    private static final Logger LOGGER = Logger.getLogger(IAntragManagerSteuerungImpl.class.getName());
    private static final IDatabase iDatabase = new IDatabaseImpl();
    private EntityManager em;
    private IAntragStellenAdmin iantragstellenAdmin;
    private IAntragManager iantragManager;
    private ICRUDManager icrudManager;
    private ISonderKontoService isonderkontoService;
    int mID;

    /**
     * Konstruktor, der den EntityManager initialisiert und die erforderlichen Dienste lädt.
     */
    public IAntragManagerSteuerungImpl() {
        IDatabaseImpl iDatabaseImpl = new IDatabaseImpl();
        this.em = iDatabaseImpl.getEntityManager();
        boolean loadErrorManager = false;
        Iterator<ICRUDManager> iteratorManager = ServiceLoader.load(ICRUDManager.class).iterator();
        if (iteratorManager.hasNext()) {
            this.icrudManager = iteratorManager.next();
            this.icrudManager.setEntityManager(this.em);
        } else {
            loadErrorManager = true;
        }
        if (loadErrorManager)
            throw new FindException("ICRUDManager-Implementierung wurde nicht gefunden");
        boolean loadErrorStellenAdmin = false;
        Iterator<IAntragStellenAdmin> iteratorStellenAdmin = ServiceLoader.load(IAntragStellenAdmin.class).iterator();
        if (iteratorStellenAdmin.hasNext()) {
            this.iantragstellenAdmin = iteratorStellenAdmin.next();
            this.iantragstellenAdmin.setEntityManager(this.em);
        } else {
            loadErrorStellenAdmin = true;
        }
        if (loadErrorStellenAdmin)
            throw new FindException("IAntragStellenAdmin-Implementierung wurde nicht gefunden");
        boolean loadErrorAntragManager = false;
        Iterator<IAntragManager> iteratorAntragManager = ServiceLoader.load(IAntragManager.class).iterator();
        if (iteratorAntragManager.hasNext()) {
            this.iantragManager = iteratorAntragManager.next();
            this.iantragManager.setEntityManager(this.em);
        } else {
            loadErrorAntragManager = true;
        }
        if (loadErrorAntragManager)
            throw new FindException("IAntragManager-Implementierung wurde nicht gefunden");
        boolean loadError = false;
        Iterator<ISonderKontoService> iterator = ServiceLoader.load(ISonderKontoService.class).iterator();
        if (iterator.hasNext()) {
            this.isonderkontoService = iterator.next();
            this.isonderkontoService.setEntityManager(this.em);
        } else {
            loadError = true;
        }
        if (loadError)
            throw new FindException("ISonderKontoService-Implementierung wurde nicht gefunden");
    }

    /**
     * Retrieves a list of personal applications belonging to the currently active manager.
     *
     * @return a list of AntragGrenz objects representing the personal applications
     */
    public List<AntragGrenz> getEigeneAntraege() {

        int activeManagerID = IActivateComponentImpl.getManagerID();


        List<Antrag> eigeneAntraege = this.iantragManager.getListeEigenerAntraege(activeManagerID);

        List<AntragGrenz> eigeneAntraegeGrenz = new ArrayList<>();
        for (Antrag antrag : eigeneAntraege) {
            AntragGrenz antragGrenz = new AntragGrenz();
            antragGrenz.setAtid(antrag.getAtid());
            antragGrenz.setTyp(antrag.getTyp());
            antragGrenz.setDaten(antrag.getDaten());
            antragGrenz.setKommentare(antrag.getKommentare());
            antragGrenz.setStatus(antrag.getStatus());
            eigeneAntraegeGrenz.add(antragGrenz);
        }

        return eigeneAntraegeGrenz;
    }

    /**
     * Setzt den Status eines Antrags auf "genehmigt".
     * /LF80/
     * @param antragGrenz Der Antrag, der genehmigt werden soll.
     * @return  true wenn der Antrag erfolgreich genehmigt wurde, ansonsten false.
     */
    @Override
    public boolean setAntragGenehmigt(AntragGrenz antragGrenz) {

        boolean result;
        this.em.getTransaction().begin();
        if (antragGrenz.getTyp().equals("ks") && antragGrenz.getAdmin() != null) {
            result = iantragManager.setKSAntragGenehmigt(antragGrenz.getAtid(), antragGrenz
                    .getAdmin().getAid());
        } else {
            result = iantragManager.setAntragGenehmigt(antragGrenz.getAtid());
        }
        if (result) {
            em.getTransaction().commit();
        } else {
            em.getTransaction().rollback();
        }
        return result;
    }

    /**
     * Setzt den Antrag als abgelehnt.
     * /LF85/ Kontolimit-Antrag bearbeiten
     * @param antragId Die ID des Antrags, der abgelehnt werden soll.
     * @return true, wenn der Antrag erfolgreich abgelehnt wurde, sonst false.
     */
    @Override
    public boolean setAntragAbgelehnt(int antragId) {
        boolean antragAbgelehnt;

        em.getTransaction().begin();
        antragAbgelehnt = iantragManager.setAntragAbgelehnt(antragId);
        if (antragAbgelehnt) em.getTransaction().commit();
        else em.getTransaction().rollback();
        return antragAbgelehnt;
    }


    /**
     * Aktualisiert das Kontolimit.
     * @param i Die ID des Antrags.
     * @param i1 Die ID des Kontos.
     * @param v Das neue Limit.
     * @return true, wenn das Kontolimit erfolgreich aktualisiert wurde, sonst false.
     */
    @Override
    public boolean updateKontolimit(int i, int i1, double v) {
        if (this.em.getTransaction().isActive()) {
            this.em.getTransaction().rollback();
        }
        boolean erfolgreich = false;
        this.em.getTransaction().begin();

        boolean isDispoUpdated = this.isonderkontoService.neuesDispoSetzen(i1, v);
        boolean isAntragGenehmigt = this.iantragManager.setAntragGenehmigt(i);
        boolean isAntragBearbeitet = this.iantragManager.setAntragBearbeitet(i);

        if (isDispoUpdated && isAntragGenehmigt && isAntragBearbeitet) {
            this.em.getTransaction().commit();
            erfolgreich = true;
        } else {
            this.em.getTransaction().rollback();
        }

        return erfolgreich;
    }

    @Override
    public boolean addAntrag(AntragGrenz antragGrenz) {
        return false;
    }

    /**
     * Gibt eine Liste aller Administratoren zurück.
     * /LF80/ /LF90/
     * @return Eine Liste von AdminGrenz Objekten, die alle Administratoren repräsentieren.
     */
    @Override
    public List<AdminGrenz> getAlleAdmins() {
        List<Admin> adminListe = iantragstellenAdmin.getAdminListe();
        List<AdminGrenz> adminGrenzList = new ArrayList<>();
        for (Admin admin : adminListe) {
            AdminGrenz adminGrenz = new AdminGrenz(
                    admin.getAid(),
                    admin.getTitel(),
                    admin.getName(),
                    admin.getVorname(),
                    admin.getGeschlecht(),
                    admin.getGeburtsdatum(),
                    admin.getAdresse(),
                    admin.getTelefon(),
                    admin.getAbteilung()
            );
            adminGrenzList.add(adminGrenz);
        }
        return adminGrenzList;
    }
    /**
     * Sets an application as canceled based on the provided application ID.
     *
     * @param antragId the ID of the application to be canceled
     * @return true if the application was successfully canceled, false otherwise
     */
    public boolean setAntragStorniert(int antragId) {

        int managerId = IActivateComponentImpl.getManagerID();


        this.em.getTransaction().begin();


        boolean storniert = this.iantragManager.setAntragStorniert(antragId, managerId);

        if (storniert) {
            this.em.getTransaction().commit();
        } else {
            this.em.getTransaction().rollback();
        }

        return storniert;
    }

    /**
     * Gibt eine Liste aller Anträge zurück, die den Typ "ks" oder "ko" haben.
     * /LF80/
     * @return Eine Liste von {@code AntragGrenz} Objekten, die Anträge des Typs "ks" oder "ko" repräsentieren.
     */
    @Override
    public List<AntragGrenz> getAntragListKsKo() {
        int managerId = IActivateComponentImpl.getManagerID();
        List<Antrag> antragListKsKoKl = this.iantragManager.getAntragListe(managerId);
        List<AntragGrenz> antragGrenzListKsKo = new ArrayList<>();

        for (Antrag antrag : antragListKsKoKl) {
            if (!antrag.getTyp().equals("kl")) {
                AntragGrenz antragGrenzKsKo = new AntragGrenz();
                antragGrenzKsKo.setAtid(antrag.getAtid());
                antragGrenzKsKo.setTyp(antrag.getTyp());
                antragGrenzKsKo.setDaten(antrag.getDaten());
                antragGrenzKsKo.setKommentare(antrag.getKommentare());
                antragGrenzKsKo.setStatus(antrag.getStatus());
                antragGrenzListKsKo.add(antragGrenzKsKo);
            }
        }
        return antragGrenzListKsKo;
    }

    /**
     * Holt die Liste der Anträge für Konto-Limit (KL).
     * @return Eine Liste von AntragGrenz-Objekten.
     */
    @Override
    public List<AntragGrenz> getAntragListKl() {
        int managerId = IActivateComponentImpl.getManagerID();
        List<Antrag> antragListKsKoKl = this.iantragManager.getAntragListe(managerId);
        List<AntragGrenz> antragGrenzListKl = new ArrayList<>();
        AntragGrenz antragGrenzKl = new AntragGrenz();
        for (Antrag antrag : antragListKsKoKl) {
            if (antrag.getTyp().equals("kl")) {
                antragGrenzKl.setAtid(antrag.getAtid());
                antragGrenzKl.setTyp(antrag.getTyp());
                antragGrenzKl.setDaten(antrag.getDaten());
                antragGrenzKl.setKommentare(antrag.getKommentare());
                antragGrenzKl.setStatus(antrag.getStatus());
                antragGrenzListKl.add(antragGrenzKl);
            }
        }
        return antragGrenzListKl;
    }
}
