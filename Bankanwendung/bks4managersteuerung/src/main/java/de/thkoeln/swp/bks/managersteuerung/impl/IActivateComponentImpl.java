package de.thkoeln.swp.bks.managersteuerung.impl;

import de.thkoeln.swp.bks.bksdbmodel.entities.Manager;
import de.thkoeln.swp.bks.bksdbmodel.exceptions.NoEntityManagerException;
import de.thkoeln.swp.bks.bksdbmodel.impl.IDatabaseImpl;
import de.thkoeln.swp.bks.componentcontroller.services.CompType;
import de.thkoeln.swp.bks.componentcontroller.services.IActivateComponent;
import de.thkoeln.swp.bks.datenhaltungapi.ICRUDManager;

import javax.persistence.EntityManager;
import java.lang.module.FindException;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Implementierung der IActivateComponent-Schnittstelle zur Verwaltung der Aktivierung und Deaktivierung von Komponenten.
 *
 * @autor Bilal Al Chami
 */
public class IActivateComponentImpl implements IActivateComponent {

    private boolean isActivated;
    private static EntityManager entityManager;
    private static int managerID;
    private ICRUDManager icrudManager;

    /**
     * Konstruktor für die IActivateComponentImpl-Klasse, der den EntityManager initialisiert.
     */
    public IActivateComponentImpl() {
        IDatabaseImpl db = new IDatabaseImpl();
        this.entityManager = db.getEntityManager();
        this.isActivated = false;
        Iterator<ICRUDManager> iterator = ServiceLoader.<ICRUDManager>load(ICRUDManager.class).iterator();
        if (iterator.hasNext()) {
            this.icrudManager = iterator.next();
            this.icrudManager.setEntityManager(this.entityManager);
        } else {
            throw new FindException("Es konnte keine ICRUDKunde-Implementierung gefunden werden");
        }
    }

    @Override
    public CompType getCompType() {
        return CompType.MANAGER;
    }

    /**
     * @param managerID die ID des zu aktivierenden Managers.
     * @return true, wenn die Komponente erfolgreich aktiviert wurde, false sonst.
     * @throws NoEntityManagerException wenn der EntityManager nicht verfügbar ist.
     */
    @Override
    public boolean activateComponent(int managerID) {
        IActivateComponentImpl.managerID = managerID;
        if (this.icrudManager.getManagerByID(managerID) != null && !this.isActivated) {
            this.isActivated = true;
            return true;
        }
        return false;
    }

    /**
     * @return true, wenn die Komponente erfolgreich deaktiviert wurde, false sonst.
     */
    @Override
    public boolean deactivateComponent() {
        if (isActivated) {
            isActivated = false;
            return true;
        }
        return false;
    }

    /**
     * @return true, wenn die Komponente derzeit aktiviert ist, false sonst.
     */
    @Override
    public boolean isActivated() {
        return isActivated;
    }

    /**
     * Gibt die ID des derzeit aktivierten Managers zurück.
     *
     * @return die ID des derzeit aktivierten Managers.
     */
    public static int getManagerID() {
        return managerID;
    }
}
