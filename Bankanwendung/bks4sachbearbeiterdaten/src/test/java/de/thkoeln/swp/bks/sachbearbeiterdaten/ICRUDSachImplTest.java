package de.thkoeln.swp.bks.sachbearbeiterdaten;

import de.thkoeln.swp.bks.bksdbmodel.entities.Sachbearbeiter;
import de.thkoeln.swp.bks.bksdbmodel.impl.IDatabaseImpl;
import de.thkoeln.swp.bks.bksdbmodel.services.IDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

public class ICRUDSachImplTest{

    public ICRUDSachImpl classUnderTest;
    public static EntityManager em;
    public static final IDatabase db = new IDatabaseImpl();

    /**
     * @Before: angenommen()
     * Angenommen der EntityManager wird korrekt geholt,
     * UND die Implementierung der ICRUDSach Schnittstelle wird als classUnderTest instanziiert,
     * UND der EntityManager em wird per setEntityManager Methode der classUnderTest gesetzt,
     * UND die Transaktion von em wird gestartet,
     * UND die Daten der betreffenden Entitäten wurden im Persistence Context gelöscht.
     */
    @Before
    public void angenommen(){

        db.useDevPU();
        em = db.getEntityManager();

        classUnderTest = new ICRUDSachImpl();
        classUnderTest.setEntityManager(em);

        em.getTransaction().begin();

        em.createQuery("delete from Antrag").executeUpdate();
        em.createQuery("delete from Sachbearbeiter").executeUpdate();

    }

    /**
     * @After: amEnde()
     * Am Ende wird die Transaktion zurück gesetzt.
     */
    @After
    public void amEnde(){
        em.getTransaction().rollback();
    }

    /**
     * @Test: getSachByID_00()
     * WENN ein Testsachbearbeiter bereits in der DB existiert,
     * UND die Methode getSachByID mit der Id des Testsachbearbeiters aufgerufen wird,
     * DANN sollte sie den Testsachbearbeiter zurückliefern.
     */
    @Test
    public void getSachByID_00(){
        Sachbearbeiter sachbearbeiter = new Sachbearbeiter(null,"Herr","sachName","sachVorName","m", LocalDate.of(1998, 6, 8),"A1");
        em.persist(sachbearbeiter);
        Sachbearbeiter sachbearbeiterUnderTest = classUnderTest.getSachByID(sachbearbeiter.getSid());

        assertEquals(sachbearbeiter,sachbearbeiterUnderTest);
    }

    /**
     * @Test: getSachByID_01()
     * WENN ein Testsachbearbeiter nicht in der DB existiert,
     * UND die Methode getSachByID mit der Id des Testsachbearbeiters aufgerufen wird,
     * DANN sollte sie NULL zurückliefern.
     */
    @Test
    public void getSachByID_01(){
        Sachbearbeiter sachbearbeiter = new Sachbearbeiter(null,"Herr","sachName","sachVorName","m", LocalDate.of(1998, 6, 8),"A1");
        em.persist(sachbearbeiter);
        em.remove(sachbearbeiter);
        Sachbearbeiter sachbearbeiterUnderTest = classUnderTest.getSachByID(sachbearbeiter.getSid());
        assertNull(sachbearbeiterUnderTest);

    }

    /**
     * @Test: getSachListe_00()
     * WENN x (x>0) Sachbearbeiter in der DB existieren,
     * UND die Methode getSachListe aufgerufen wird,
     * DANN sollte sie eine Liste mit x Sachbearbeitern zurückliefern
     */
    @Test
    public void getSachListe_00(){
        Sachbearbeiter sachbearbeiter = new Sachbearbeiter(null,"Herr","sachName","sachVorName","m", LocalDate.of(1998, 6, 8),"A1");
        em.persist(sachbearbeiter);
        List<Sachbearbeiter> sachListe = classUnderTest.getSachListe();
        assertEquals(1,sachListe.size());
    }

    /**
     * @Test: getSachListe_01()
     * WENN keine Sachbearbeiter in der DB existieren,
     * UND die Methode getSachListe aufgerufen wird,
     * DANN sollte sie eine leere Liste zurückliefern.
     */
    @Test
    public void getSachListe_01(){
        List<Sachbearbeiter> sachListe = classUnderTest.getSachListe();
        assertTrue(sachListe.isEmpty());
    }

    /**
     * @Test: insertSach_00()
     * WENN die Methode insertSach mit einem Testsachbearbeiter aufgerufen wird,
     * UND die ID des Testsachbearbeiters gleich null ist,
     * DANN sollte sie TRUE zurückliefern,
     * UND der Testsachbearbeiter sollte im Persistence Context existieren.
     */
    @Test
    public void insertSach_00(){
        Sachbearbeiter sachbearbeiter = new Sachbearbeiter(null,"Herr","sachName","sachVorName","m", LocalDate.of(1998, 6, 8),"A1");
        assertTrue(classUnderTest.insertSach(sachbearbeiter));
        Sachbearbeiter sachbearbeiterUnderTest = em.find(Sachbearbeiter.class, sachbearbeiter.getSid());
        assertEquals(sachbearbeiter,sachbearbeiterUnderTest);
    }

    /**
     * @Test: insertSach_01()
     * WENN die Methode insertSach mit einem Testsachbearbeiter aufgerufen wird,
     * UND die ID des Testsachbearbeiter ungleich null ist,
     * DANN sollte sie FALSE zurückliefern,
     * UND der Persistence Context wurde nicht verändert.
     */
    @Test
    public void insertSach_01(){
        Sachbearbeiter sachbearbeiter = new Sachbearbeiter(1,"Herr","sachName","sachVorName","m", LocalDate.of(1998, 6, 8),"A1");
        assertFalse(classUnderTest.insertSach(sachbearbeiter));
        Sachbearbeiter sachbearbeiterUnderTest = em.find(Sachbearbeiter.class, sachbearbeiter.getSid());
        assertNull(sachbearbeiterUnderTest);
    }

    /**
     * @Test: editSach_00()
     * WENN ein Testsachbearbeiter in der DB existiert,
     * UND die Methode editSach mit einem veränderten Testsachbearbeiter (aber gleicher ID) aufgerufen wird,
     * DANN sollte sie TRUE zurückliefern,
     * UND der Testsachbearbeiter sollte im Persistence Context verändert sein.
     */
    @Test
    public void editSach_00(){
        Sachbearbeiter sachbearbeiter = new Sachbearbeiter(null,"Herr","sachName","sachVorName","m", LocalDate.of(1998, 6, 8),"A1");
        em.persist(sachbearbeiter);


        sachbearbeiter.setName("newName");
        sachbearbeiter.setGeburtsdatum(LocalDate.of(1955, 2, 6));

        assertTrue(classUnderTest.editSach(sachbearbeiter));
        Sachbearbeiter sachbearbeiter1 = em.find(Sachbearbeiter.class,sachbearbeiter.getSid());

        assertEquals("newName",sachbearbeiter1.getName());
        assertEquals(LocalDate.of(1955, 2, 6),sachbearbeiter1.getGeburtsdatum());
    }

    /**
     * @Test: editSach_01()
     * WENN ein Testsachbearbeiter nicht in der DB existiert,
     * UND die Methode editSach mit dem Testsachbearbeiter aufgerufen wird,
     * DANN sollte sie FALSE zurückliefern,
     * UND der Testsachbearbeiter sollte nicht im Persistence Context existieren.
     */
    @Test
    public void editSach_01(){
        Sachbearbeiter sachbearbeiter = new Sachbearbeiter(null,"Herr","sachName","sachVorName","m", LocalDate.of(1998, 6, 8),"A1");
        em.persist(sachbearbeiter);
        em.remove(sachbearbeiter);

        sachbearbeiter.setName("newName");
        sachbearbeiter.setGeburtsdatum(LocalDate.of(1955, 2, 6));
        assertFalse(classUnderTest.editSach(sachbearbeiter));
        Sachbearbeiter sachbearbeiterUnderTest = em.find(Sachbearbeiter.class, sachbearbeiter.getSid());
        assertNull(sachbearbeiterUnderTest);
    }

    /**
     * @Test: deleteSach_00()
     * WENN ein Testsachbearbeiter in der DB existiert,
     * UND die Methode deleteSach mit der ID des Testsachbearbeiters aufgerufen wird,
     * DANN sollte sie TRUE zurückliefern,
     * UND der Testsachbearbeiter sollte nicht mehr im Persistence Context existieren.
     */
    @Test
    public void deleteSach_00(){
        Sachbearbeiter sachbearbeiter = new Sachbearbeiter(null,"Herr","sachName","sachVorName","m", LocalDate.of(1998, 6, 8),"A1");
        em.persist(sachbearbeiter);
        boolean sachbearbeiter_delete = classUnderTest.deleteSach(sachbearbeiter.getSid());

        assertTrue(sachbearbeiter_delete);
        assertFalse(em.contains(sachbearbeiter));
    }

    /**
     * @Test: deleteSach_01()
     * WENN ein Testsachbearbeiter nicht in der DB existiert,
     * UND die Methode deleteSach mit der ID des Testsachbearbeiters aufgerufen wird,
     * DANN sollte sie FALSE zurückliefern.
     */
    @Test
    public void deleteSach_01(){
        Sachbearbeiter sachbearbeiter = new Sachbearbeiter(null,"Herr","sachName","sachVorName","m", LocalDate.of(1998, 6, 8),"A1");
        em.persist(sachbearbeiter);
        em.remove(sachbearbeiter);
        boolean sachbearbeiter_delete = classUnderTest.deleteSach(sachbearbeiter.getSid());

        assertFalse(sachbearbeiter_delete);

    }

    /**
     * @Test: getSachA1_00()
     * WENN x (x>0) Sachbearbeiter in der DB existieren, die zur Abteilung A1 gehören,
     * UND die Methode getSachA1 aufgerufen wird,
     * DANN sollte sie eine Liste mit x Sachbearbeitern zurückliefern.
     */
    @Test
    public void getSachA1_00(){
        Sachbearbeiter sachbearbeiter = new Sachbearbeiter(null,"Herr","sachName","sachVorName","m", LocalDate.of(1998, 6, 8),"A1");
        em.persist(sachbearbeiter);
        List<Sachbearbeiter> sachListe = classUnderTest.getSachA1();
        assertEquals(1,sachListe.size());
        for (Sachbearbeiter sachbearbeiterList : sachListe) {
            assertEquals(sachbearbeiterList.getAbteilung(), "A1");
        }

    }

    /**
     * @Test: getSachA1_01()
     * WENN keine Sachbearbeiter in der DB existieren, die zur Abteilung A1 gehören,
     * UND die Methode getSachA1 aufgerufen wird,
     * DANN sollte sie eine leere Liste zurückliefern.
     */
    @Test
    public void getSachA1_01(){
        Sachbearbeiter sachbearbeiter = new Sachbearbeiter(null,"Herr","sachName","sachVorName","m", LocalDate.of(1998, 6, 8),"A2");
        em.persist(sachbearbeiter);

        List<Sachbearbeiter> sachListe = classUnderTest.getSachA1();
        assertTrue(sachListe.isEmpty());
    }

    /**
     * @Test: getSachA2_00()
     * WENN x (x>0) Sachbearbeiter in der DB existieren, die zur Abteilung A2 gehören,
     * UND die Methode getSachA2 aufgerufen wird,
     * DANN sollte sie eine Liste mit x Sachbearbeitern zurückliefern.
     */
    @Test
    public void getSachA2_00(){
        Sachbearbeiter sachbearbeiter = new Sachbearbeiter(null,"Herr","sachName","sachVorName","m", LocalDate.of(1998, 6, 8),"A2");
        em.persist(sachbearbeiter);
        List<Sachbearbeiter> sachListe = classUnderTest.getSachA2();
        assertEquals(1,sachListe.size());
        for (Sachbearbeiter sachbearbeiterList : sachListe) {
            assertEquals(sachbearbeiterList.getAbteilung(), "A2");
        }
    }

    /**
     * @Test: getSachA2_01()
     * WENN keine Sachbearbeiter in der DB existieren, die zur Abteilung A2 gehören,
     * UND die Methode getSachA2 aufgerufen wird,
     * DANN sollte sie eine leere Liste zurückliefern.
     */
    @Test
    public void getSachA2_01(){
        Sachbearbeiter sachbearbeiter = new Sachbearbeiter(null,"Herr","sachName","sachVorName","m", LocalDate.of(1998, 6, 8),"A1");
        em.persist(sachbearbeiter);

        List<Sachbearbeiter> sachListe2 = classUnderTest.getSachA2();
        assertTrue(sachListe2.isEmpty());

    }

}