package de.thkoeln.swp.bks.sachbearbeiterdaten;

import de.thkoeln.swp.bks.bksdbmodel.entities.Konto;
import de.thkoeln.swp.bks.bksdbmodel.entities.Ueberweisung;
import de.thkoeln.swp.bks.bksdbmodel.entities.UeberweisungsVorlage;
import de.thkoeln.swp.bks.bksdbmodel.exceptions.NoEntityManagerException;
import de.thkoeln.swp.bks.datenhaltungapi.IUeberweisungsVorlage;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

public class IUeberweisungsVorlageImpl implements IUeberweisungsVorlage {
    private EntityManager em;

    @Override
    public void setEntityManager(EntityManager entityManager) {
       this.em = entityManager;
    }

    @Override
    public UeberweisungsVorlage getUeberweisungsVorlageByID(int uvid) {
        if(this.em==null) throw new NoEntityManagerException();
        return em.find(UeberweisungsVorlage.class, uvid);
    }

    @Override
    public List<UeberweisungsVorlage> getUeberweisungsVorlagenDesKunden(int kid) {
        if(this.em==null) throw new NoEntityManagerException();
        TypedQuery<UeberweisungsVorlage> typedQuery = em.createQuery(
                "SELECT uvk FROM UeberweisungsVorlage uvk WHERE uvk.kunde.kid = :kid", UeberweisungsVorlage.class);
        typedQuery.setParameter("kid", kid);
        return typedQuery.getResultList();
    }

    @Override
    public List<UeberweisungsVorlage> getUeberweisungsVorlagenDesKontos(int ktoid) {
        if(this.em==null) throw new NoEntityManagerException();
        TypedQuery<UeberweisungsVorlage> typedQuery = em.createQuery(
                "SELECT uvk FROM UeberweisungsVorlage uvk WHERE uvk.vonkonto.ktoid = :ktoid", UeberweisungsVorlage.class);
        typedQuery.setParameter("ktoid", ktoid);
        return typedQuery.getResultList();
    }

    @Override
    public boolean insertUeberweisungsVorlage(UeberweisungsVorlage uv) {
        if(this.em==null) throw new NoEntityManagerException();
        if (uv == null) {
            return false;
        }
        try {
            em.persist(uv);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteUeberweisungsVorlage(int uvid){
        if(this.em==null) throw new NoEntityManagerException();
        UeberweisungsVorlage uv = em.find(UeberweisungsVorlage.class, uvid);

        if (uv != null) {
            em.remove(uv);
            return true;
        }

        return false;
    }

  @Override
    public boolean editUeberweisungsVorlage(UeberweisungsVorlage uv) {

        UeberweisungsVorlage existingUeberweisungsVorlage = em.find(UeberweisungsVorlage.class, uv.getUvid());
        if (existingUeberweisungsVorlage != null) {
            em.merge(uv);
            return true;
        }
        return false;
    }

    @Override
    public List<Ueberweisung> getUeberweisungenZuKonto(int ktoid){

        List<Ueberweisung> ueberweisungen = new ArrayList<>();

        if(em == null) throw new NoEntityManagerException();
        Konto konto = em.find(Konto.class, ktoid);

        if(konto != null){
            TypedQuery<Ueberweisung> query = em.createQuery("SELECT ub FROM  Ueberweisung ub  WHERE ub.zukonto= : konto ", Ueberweisung.class);
            query.setParameter("konto", konto);
            ueberweisungen = query.getResultList();
        }

        return ueberweisungen;
    }


    @Override
    public List<Ueberweisung> getUeberweisungenVonKonto(int ktoid){

        List<Ueberweisung> ueberweisungen = new ArrayList<>();

        if(em == null) throw new NoEntityManagerException();

        Konto konto = em.find(Konto.class, ktoid);
        if(konto != null){
            TypedQuery<Ueberweisung> query = em.createQuery("SELECT ub FROM  Ueberweisung ub  WHERE ub.vonkonto= : konto ", Ueberweisung.class);
            query.setParameter("konto", konto);
            ueberweisungen = query.getResultList();
        }

        return ueberweisungen;
    }

    @Override
    public List<Ueberweisung> getLoeschbareUeberweisungen(){
        if(em == null) throw new NoEntityManagerException();

        // Aktualisieren der Abfrage, um nur die relevanten Überweisungen direkt zu filtern
        TypedQuery<Ueberweisung> query = em.createQuery(
                "SELECT a FROM Ueberweisung a WHERE a.status IN ('us', 'st', 'nu')", Ueberweisung.class);

        return query.getResultList();
    }


    @Override
    public boolean loescheUeberweisung(int ubid) {
        if(em == null) throw new NoEntityManagerException();
        Ueberweisung ueberweisung = em.find(Ueberweisung.class, ubid);
        if (ueberweisung == null) {
            return false;
        }
        if (ueberweisung.getStatus().equals("us") ||
                ueberweisung.getStatus().equals("st") ||
                ueberweisung.getStatus().equals("nu")) {
                em.remove(ueberweisung);
                return true;
            }
            return false;
        }


    @Override
    public List<Ueberweisung> getWartendeUeberweisungen() {
        TypedQuery<Ueberweisung> query = em.createQuery(
                "SELECT u FROM Ueberweisung u WHERE u.status = 'wt'", Ueberweisung.class);
        return query.getResultList();
    }
}
