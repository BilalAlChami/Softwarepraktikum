package de.thkoeln.swp.bks.sachbearbeiterdaten;

import de.thkoeln.swp.bks.bksdbmodel.entities.Sachbearbeiter;
import de.thkoeln.swp.bks.bksdbmodel.exceptions.NoEntityManagerException;
import de.thkoeln.swp.bks.datenhaltungapi.ICRUDSach;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.List;

public class ICRUDSachImpl implements ICRUDSach{

    private EntityManager em;

    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    @Override
    public Sachbearbeiter getSachByID(int sid) {
        if(em == null){
            throw new NoEntityManagerException();
        }
        Sachbearbeiter sachbearbeiter = em.find(Sachbearbeiter.class,sid);
        return sachbearbeiter;
    }

    @Override
    public List<Sachbearbeiter> getSachListe() {
        if (em == null) {
            throw new NoEntityManagerException();
        }
        List<Sachbearbeiter> sachbearbeiterList = em.createNamedQuery("Sachbearbeiter.findAll", Sachbearbeiter.class).getResultList();
        return sachbearbeiterList;
    }

    @Override
    public boolean insertSach(Sachbearbeiter sachbearbeiter) {

        if (em == null) {
            throw new NoEntityManagerException();
        }

        if (sachbearbeiter == null || sachbearbeiter.getSid() != null) {
            return false;
        }

        em.persist(sachbearbeiter);

        return true;
    }

    @Override
    public boolean editSach(Sachbearbeiter sachbearbeiter) {
        if (em == null) {
            throw new NoEntityManagerException();
        }
        if(sachbearbeiter != null && (em.find(Sachbearbeiter.class, sachbearbeiter.getSid()) != null)){
            em.merge(sachbearbeiter);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteSach(int i) {
        if (em == null) {
            throw new NoEntityManagerException();
        }
        Sachbearbeiter sachbearbeiter = em.find(Sachbearbeiter.class, i);

        if (sachbearbeiter != null) {
            em.remove(sachbearbeiter);
            return true;
        }
        return false;
    }

    @Override
    public List<Sachbearbeiter> getSachA1() {
        if (em == null) {
            throw new NoEntityManagerException();
        }
        return em.createNamedQuery("Sachbearbeiter.findByAbteilung", Sachbearbeiter.class) .setParameter("abteilung", "A1").getResultList();
    }

    @Override
    public List<Sachbearbeiter> getSachA2() {
        if (em == null) {
            throw new NoEntityManagerException();
        }
        return em.createNamedQuery("Sachbearbeiter.findByAbteilung", Sachbearbeiter.class) .setParameter("abteilung", "A2").getResultList();
    }
}