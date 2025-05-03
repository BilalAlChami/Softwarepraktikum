package de.thkoeln.swp.bks.sachbearbeiterdaten;

import de.thkoeln.swp.bks.bksdbmodel.entities.Antrag;
import de.thkoeln.swp.bks.bksdbmodel.entities.Sachbearbeiter;
import de.thkoeln.swp.bks.bksdbmodel.exceptions.NoEntityManagerException;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import de.thkoeln.swp.bks.datenhaltungapi.IAntragSach;
import java.util.List;

import java.util.logging.Logger;

/**
 * Diese Klasse implementiert das Interface IAntragSach und stellt die
 * Funktionalität zur Bearbeitung von Anträgen bereit. (AP1.11 und AP1.12)
 *
 * @author Shajangarya Manogaran, Raluca Imbrescu
 */


public class IAntragSachImpl implements IAntragSach {


    private static final Logger LOGGER = Logger.getLogger(IAntragSachImpl.class.getName());
    private EntityManager em;

    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<Antrag> getAntragListe(int sid) {
        if (em == null) {
            throw new NoEntityManagerException();
        }
        TypedQuery<Antrag> antragTypedQuery = em.createQuery("SELECT a FROM Antrag a WHERE a.sachbearbeiter.sid = :sid", Antrag.class);
        antragTypedQuery.setParameter("sid", sid);
        return antragTypedQuery.getResultList();
    }

    @Override
    public boolean setAntragBearbeitet(int antid, int sid) {
        if (em == null) {
            throw new NoEntityManagerException();
        }
        Antrag antrag = em.find(Antrag.class, antid);
        if (antrag == null) {
            return false;
        }
        Sachbearbeiter sachbearbeiter = antrag.getSachbearbeiter();
        if (sachbearbeiter == null) {
            return false;
        }
        if (sachbearbeiter.getSid() != sid) {
            return false;
        }
        if (antrag.getTyp().equals("ko")) {
            if (antrag.getStatus().equals("g")) {
                antrag.setStatus("b");
                return true;
            }
        }
        return false;


    }

    @Override
    public boolean antragStornieren(int antid, int sid) {
        if (em == null) {
            throw new NoEntityManagerException();
        }
        Antrag antrag = em.find(Antrag.class, antid);
        if (antrag == null) {
            return false;
        }
        Sachbearbeiter sachbearbeiter = antrag.getSachbearbeiter();
        if (sachbearbeiter == null) {
            return false;
        }
        if (sachbearbeiter.getSid() != sid) {
            return false;
        }

        String antragTyp = antrag.getTyp();
        switch (antragTyp) {
            case "ko":
                if (antrag.getStatus().equals("i") || antrag.getStatus().equals("s")) {
                    return false;
                }
                if (antrag.getStatus().equals("n") || antrag.getStatus().equals("g")) {
                    antrag.setStatus("s");
                    return true;
                }
                if (antrag.getStatus().equals("a")) {
                    return false;
                }
                break;
            case "ks":

                if (antrag.getStatus().equals("i") || antrag.getStatus().equals("a")) {
                    return false;
                }

                if (antrag.getStatus().equals("n") || antrag.getStatus().equals("g")) {
                    antrag.setStatus("s");
                    return true;
                }
                break;
            case "kl":
                if (antrag.getStatus().equals("a") || antrag.getStatus().equals("s")){
                    return false;
                }

                if (antrag.getStatus().equals("n")) {
                    antrag.setStatus("s");
                    return true;
                }
                break;
            case "so":
                return false;
            case "sb":
                return false;
            case "sd":
                return false;
        }
        return true;
    }

    @Override
    public List<Antrag> getNeueAntraege(int sid) {
        if (em == null) {
            throw new NoEntityManagerException();
        }
        TypedQuery<Antrag> antragTypedQuery = em.createQuery("SELECT a FROM Antrag a WHERE a.sachbearbeiter.sid = :sid AND a.status = 'a'", Antrag.class);
        antragTypedQuery.setParameter("sid", sid);
        return antragTypedQuery.getResultList();
    }

    @Override
    public List<Antrag> getAlleAntraege() {
        if (em == null) {
            throw new NoEntityManagerException();
        }
        List<Antrag> alleAntraege = em.createNamedQuery("Antrag.findAll", Antrag.class).getResultList();
        return alleAntraege;
    }


    @Override
    public boolean deleteAntrag ( int antid, int sid){
        if (em == null) throw new NoEntityManagerException();
        Antrag antrag = em.find(Antrag.class, antid);
        //Antragsobjekt prüfen
        if (antrag == null) return false;
        Sachbearbeiter sachbearbeiter = antrag.getSachbearbeiter();
        // Id des Sachbearbeiters prüfen
        if (sachbearbeiter == null) return false;
        if (sachbearbeiter.getSid() != sid) return false;
        else {
            String typ = antrag.getTyp();
            if (typ.equals("ks") || typ.equals("ko") || typ.equals("kl")) {
                //statuts überprüfen
                String status = antrag.getStatus();
                if (status.equals("a")) {
                    //Antrag löschen
                    em.remove(antrag);
                    return true;
                } else return false;
            } else return false;
        }
    }

    @Override
    public List<Antrag> getAbgelehnteAntraege ( int sid){
        List<Antrag> abgelehnteAntraege;
        if (em == null) throw new NoEntityManagerException();
        TypedQuery<Antrag> query = em.createQuery("SELECT a FROM Antrag a WHERE sachbearbeiter=" + sid + "AND status='a'",
                Antrag.class);
        abgelehnteAntraege = query.getResultList();
        return abgelehnteAntraege;


    }

    @Override
    public List<Antrag> getBearbeiteteAntraege ( int sid){
        List<Antrag> bearbeiteteAntraege;
        if (em == null) throw new NoEntityManagerException();
        TypedQuery<Antrag> query = em.createQuery("SELECT a FROM Antrag a WHERE sachbearbeiter=" + sid + "AND status='b'",
                Antrag.class);
        bearbeiteteAntraege = query.getResultList();
        return bearbeiteteAntraege;
    }

    @Override
    public List<Antrag> getStornierteAntraege ( int sid){
        List<Antrag> stornierteAntraege;
        if (em == null) throw new NoEntityManagerException();
        TypedQuery<Antrag> query = em.createQuery("SELECT a FROM Antrag a WHERE sachbearbeiter=" + sid + "AND status='s'",
                Antrag.class);
        stornierteAntraege = query.getResultList();
        return stornierteAntraege;
    }


    @Override
    public List<Antrag> getGenehmigteAntraege ( int sid){
        List<Antrag> genehmigeAntraege;
        if (em == null) throw new NoEntityManagerException();
        TypedQuery<Antrag> query= em.createQuery("SELECT a FROM Antrag a WHERE sachbearbeiter="+sid+ "AND status='g'",
                Antrag.class);
        genehmigeAntraege=query.getResultList();
        return genehmigeAntraege;
    }

}