package de.thkoeln.swp.bks.sachbearbeiterdaten;

import de.thkoeln.swp.bks.bksdbmodel.entities.Antrag;
import de.thkoeln.swp.bks.bksdbmodel.entities.Sachbearbeiter;
import de.thkoeln.swp.bks.bksdbmodel.impl.IDatabaseImpl;
import de.thkoeln.swp.bks.bksdbmodel.services.IDatabase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.*;

/**
 * Diese Klasse implementiert die Testfälle des Interface IAntragSach und stellt die
 * Funktionalität zur Bearbeitung von Anträgen bereit. (AP1.11 und AP1.12)
 *
 * @author Shajangarya Manogaran, Raluca Imbrescu
 */

public class IAntragSachImplTest {
    public IAntragSachImpl classUnderTest;
    public static EntityManager em;
    public static IDatabase db = new IDatabaseImpl();

    /**@Before: angenommen()
    Angenommen der EntityManager wird korrekt geholt,
    UND die Implementierung der IAntragSach Schnittstelle wird als classUnderTest instanziiert,
    UND der EntityManager em wird per setEntityManager Methode der classUnderTest gesetzt,
    UND die Transaktion von em wird gestartet,
    UND die Daten der betreffenden Entitäten wurden im Persistence Context gelöscht.*/
    @Before
    public void angenommen(){
        em = db.getEntityManager();
        classUnderTest = new IAntragSachImpl();
        classUnderTest.setEntityManager(em);
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Antrag").executeUpdate();
        em.createQuery("DELETE FROM Sachbearbeiter").executeUpdate();
    }

    /**@After: amEnde()
    Am Ende wird die Transaktion zurück gesetzt.
     */
    @After
    public void amEnde(){
        em.getTransaction().rollback();
    }

    /**@Test: getAntragListe_00()
    WENN ein TestSachbearbeiter bereits in der Datenbank existiert,
    UND x (x>0) Anträge in der DB existieren, die dieser TestSachbearbeiter erstellt hat,
    UND die Methode getAntragListe mit der ID des TestSachbearbeiters aufgerufen wird,
    DANN sollte sie die Liste mit diesen x Anträgen zurückliefern.
     */
    @Test
    public void getAntragliste_00(){
        Sachbearbeiter testSachbearbeiter = new Sachbearbeiter(null, "Frau", "Manogaran", "Shajangarya", "w", LocalDate.now(),"A1");
        em.persist(testSachbearbeiter);

        Antrag testAntrag = new Antrag(null, "so", "Daten", "test", "a");
        testAntrag.setSachbearbeiter(testSachbearbeiter);
        em.persist(testAntrag);

        List<Antrag> antragList = classUnderTest.getAntragListe(testSachbearbeiter.getSid());
        assertEquals(1,antragList.size());
    }

    /**@Test: getAntragListe_01()
     * WENN ein TestSachbearbeiter bereits in der Datenbank existiert,
     * UND keine Anträge in der DB existieren, die dieser TestSachbearbeiter erstellt hat,
     * UND die Methode getAntragListe mit der ID des TestSachbearbeiters aufgerufen wird,
     * DANN sollte sie eine leere Liste zurückliefern.
     */
    @Test
    public void getAntragListe_01(){
        Sachbearbeiter testSachbearbeiter = new Sachbearbeiter(null, "Frau", "Obama", "Michelle", "w", LocalDate.now(),"A1");
        em.persist(testSachbearbeiter);

        List<Antrag> antragList = classUnderTest.getAntragListe(testSachbearbeiter.getSid());
        assertTrue(antragList.isEmpty());
    }

    /**@Test: getAntragListe_02()
     * WENN ein TestSachbearbeiter nicht in der Datenbank existiert,
     * UND die Methode getAntragListe mit der ID des TestSachbearbeiters aufgerufen wird,
     * DANN sollte sie eine leere Liste zurückliefern.
     */
    @Test
    public void getAntragListe_02(){
        Sachbearbeiter testSachbearbeiter = new Sachbearbeiter(null, "Frau", "Manogaran", "Shajangarya", "w", LocalDate.now(),"A1");
        em.persist(testSachbearbeiter);
        em.remove(testSachbearbeiter);

        List<Antrag> antragList = classUnderTest.getAntragListe(testSachbearbeiter.getSid());
        assertTrue(antragList.isEmpty());
    }


    /**@Test: setAntragBearbeitet_00()
     * WENN ein Testantrag bereits in der DB existiert,
     * UND ein TestSachbearbeiter bereits in der DB existiert,
     * UND der Testantrag den Typ "Kontoeröffnungs-Antrag" besitzt,
     * UND der Testantrag den Status "genehmigt" besitzt,
     * UND der Testantrag vom TestSachbearbeiter erstellt wurde,
     * UND die Methode setAntragBearbeitet mit der ID des Testantrags und der ID des
     * TestSachbearbeiters aufgerufen wird,
     * DANN sollte der Status des Testantrags auf "bearbeitet" gesetzt sein,
     * UND TRUE zurückliefern
     */
    @Test
    public void setAntragBearbeitet_00(){
        Sachbearbeiter testSachbearbeiter = new Sachbearbeiter(null, "Frau", "Manogaran", "Shajangarya", "w", LocalDate.now(),"A1");
        em.persist(testSachbearbeiter);

        Antrag testAntrag = new Antrag(null,"ko","daten","test","g");
        testAntrag.setSachbearbeiter(testSachbearbeiter);
        em.persist(testAntrag);

        boolean actual = classUnderTest.setAntragBearbeitet(testAntrag.getAtid(),testSachbearbeiter.getSid());
        assertTrue(actual);
        assertEquals("b",testAntrag.getStatus());
    }

    /**@Test: setAntragBearbeitet_01()
     * WENN ein Testantrag bereits in der DB existiert,
     * UND ein TestSachbearbeiter bereits in der DB existiert,
     * UND der Testantrag den Typ "Kontolimit-Antrag" besitzt,
     * UND der Testantrag den Status "genehmigt" besitzt,
     * UND der Testantrag vom TestSachbearbeiter erstellt wurde,
     * UND die Methode setAntragBearbeitet mit der Id des Testantrags und der ID des
     * TestSachbearbeiters aufgerufen wird,
     * DANN sollte sie FALSE zurückliefern,
     * UND der Testantrag wurde nicht verändert.
     */
    @Test
    public void setAntragBearbeitet_01(){
        Sachbearbeiter testSachbearbeiter = new Sachbearbeiter(null, "Frau", "Manogaran", "Shajangarya", "w", LocalDate.now(),"A1");
        em.persist(testSachbearbeiter);

        Antrag testAntrag = new Antrag(null,"kl","daten","test","g");
        testAntrag.setSachbearbeiter(testSachbearbeiter);
        em.persist(testAntrag);

        boolean actual = classUnderTest.setAntragBearbeitet(testAntrag.getAtid(),testSachbearbeiter.getSid());
        assertFalse(actual);
        assertEquals("g",testAntrag.getStatus());

    }

    /**@Test: setAntragBearbeitet_02()
     * WENN ein Testantrag bereits in der DB existiert,
     * UND ein TestSachbearbeiter bereits in der DB existiert,
     * UND der Testantrag den Typ "Sachbearbeiter-Bearbeiten-Antrag" besitzt,
     * UND der Testantrag den Status "genehmigt" besitzt,
     * UND die Methode setAntragBearbeitet mit der Id des Testantrags und der ID des
     * TestSachbearbeiters aufgerufen wird,
     * DANN sollte sie FALSE zurückliefern,
     * UND der Testantrag wurde nicht verändert.
     */
    @Test
    public void setAntragBearbeitet_02(){
        Sachbearbeiter testSachbearbeiter = new Sachbearbeiter(null, "Frau", "Manogaran", "Shajangarya", "w", LocalDate.now(),"A1");
        em.persist(testSachbearbeiter);

        Antrag testAntrag = new Antrag(null,"sb","daten","test","g");
        testAntrag.setSachbearbeiter(testSachbearbeiter);
        em.persist(testAntrag);

        boolean actual = classUnderTest.setAntragBearbeitet(testAntrag.getAtid(),testSachbearbeiter.getSid());
        assertFalse(actual);
        assertEquals("g",testAntrag.getStatus());
    }

    /**@Test: setAntragBearbeitet_03()
     * WENN ein Testantrag bereits in der DB existiert,
     * UND ein TestSachbearbeiter bereits in der DB existiert,
     * UND der Testantrag den Typ "Kontoschließungs-Antrag" besitzt,
     * UND der Testantrag den Status "genehmigt" besitzt,
     * UND der Testantrag vom TestSachbearbeiter erstellt wurde,
     * UND die Methode setAntragBearbeitet mit der Id des Testantrags und der ID des
     * TestSachbearbeiters aufgerufen wird,
     * DANN sollte sie FALSE zurückliefern,
     * UND der Testantrag wurde nicht verändert.
     */
    @Test
    public void setAntragBearbeitet_03(){
        Sachbearbeiter testSachbearbeiter = new Sachbearbeiter(null, "Frau", "Manogaran", "Shajangarya", "w", LocalDate.now(),"A1");
        em.persist(testSachbearbeiter);

        Antrag testAntrag = new Antrag(null,"ks","daten","test","g");
        testAntrag.setSachbearbeiter(testSachbearbeiter);
        em.persist(testAntrag);

        boolean actual = classUnderTest.setAntragBearbeitet(testAntrag.getAtid(),testSachbearbeiter.getSid());
        assertFalse(actual);
        assertEquals("g",testAntrag.getStatus());
    }

    /**@Test: setAntragBearbeitet_04()
     * WENN ein Testantrag bereits in der DB existiert,
     * UND ein TestSachbearbeiter bereits in der DB existiert,
     * UND der Testantrag den Typ "Sachbearbeiter-Löschen-Antrag" besitzt,
     * UND die Methode setAntragBearbeitet mit der Id des Testantrags und der ID des
     * TestSachbearbeiters aufgerufen wird,
     * DANN sollte sie FALSE zurückliefern,
     * UND der Testantrag wurde nicht verändert.
     */
    @Test
    public void setAntragBearbeitet_04(){
        Sachbearbeiter testSachbearbeiter = new Sachbearbeiter(null, "Frau", "Manogaran", "Shajangarya", "w", LocalDate.now(),"A1");
        em.persist(testSachbearbeiter);

        Antrag testAntrag = new Antrag(null,"sd","daten","test","g");
        testAntrag.setSachbearbeiter(testSachbearbeiter);
        em.persist(testAntrag);

        boolean actual = classUnderTest.setAntragBearbeitet(testAntrag.getAtid(),testSachbearbeiter.getSid());
        assertFalse(actual);
        assertEquals("g",testAntrag.getStatus());
    }

    /**@Test: setAntragBearbeitet_05()
     * WENN ein Testantrag bereits in der DB existiert,
     * UND ein TestSachbearbeiter bereits in der DB existiert,
     * UND der Testantrag den Typ "Sachbearbeiter-Anlegen-Antrag" besitzt,
     * UND die Methode setAntragBearbeitet mit der Id des Testantrags und der ID des
     * TestSachbearbeiters aufgerufen wird,
     * DANN sollte sie FALSE zurückliefern,
     * UND der Testantrag wurde nicht verändert.
     */
    @Test
    public void setAntragBearbeitet_05(){
        Sachbearbeiter testSachbearbeiter = new Sachbearbeiter(null, "Frau", "Manogaran", "Shajangarya", "w", LocalDate.now(),"A1");
        em.persist(testSachbearbeiter);

        Antrag testAntrag = new Antrag(null,"sb","daten","test","g");
        testAntrag.setSachbearbeiter(testSachbearbeiter);
        em.persist(testAntrag);

        boolean actual = classUnderTest.setAntragBearbeitet(testAntrag.getAtid(),testSachbearbeiter.getSid());
        assertFalse(actual);
        assertEquals("g",testAntrag.getStatus());
    }

    /**@Test: setAntragBearbeitet_06()
     * WENN ein Testantrag bereits in der DB existiert,
     * UND ein TestSachbearbeiter bereits in der DB existiert,
     * UND der Testantrag den Typ "Kontoeröffnungs-Antrag" besitzt,
     * UND der Testantrag den Status "neu" besitzt,
     * UND der Testantrag vom TestSachbearbeiter erstellt wurde,
     * UND die Methode setAntragBearbeitet mit der Id des Testantrags und der ID des
     * TestSachbearbeiters aufgerufen wird,
     * DANN sollte sie FALSE zurückliefern,
     * UND der Testantrag wurde nicht verändert
     */
    @Test
    public void setAntragBearbeitet_06(){
        Sachbearbeiter testSachbearbeiter = new Sachbearbeiter(null, "Frau", "Manogaran", "Shajangarya", "w", LocalDate.now(),"A1");
        em.persist(testSachbearbeiter);

        Antrag testAntrag = new Antrag(null,"ko","daten","test","n");
        testAntrag.setSachbearbeiter(testSachbearbeiter);
        em.persist(testAntrag);

        boolean actual = classUnderTest.setAntragBearbeitet(testAntrag.getAtid(),testSachbearbeiter.getSid());
        assertFalse(actual);
        assertEquals("n",testAntrag.getStatus());
    }

    /**@Test: setAntragBearbeitet_07()
     * WENN ein Testantrag nicht in der DB existiert,
     * UND ein TestSachbearbeiter bereits in der DB existiert,
     * UND die Methode setAntragBearbeitet mit der Id des Testantrags und der ID des
     * TestSachbearbeiters aufgerufen wird,
     * DANN sollte sie FALSE zurückliefern.
     */
    @Test
    public void setAntragBearbeitet_07(){
        Sachbearbeiter testSachbearbeiter = new Sachbearbeiter(null, "Frau", "Manogaran", "Shajangarya", "w", LocalDate.now(),"A1");
        em.persist(testSachbearbeiter);

        Antrag testAntrag = new Antrag(null,"ko","daten","test","n");
        testAntrag.setSachbearbeiter(testSachbearbeiter);
        em.persist(testAntrag);
        em.remove(testAntrag);

        boolean actual = classUnderTest.setAntragBearbeitet(testAntrag.getAtid(),testSachbearbeiter.getSid());
        assertFalse(actual);
    }

    /**@Test: antragStornieren_00()
     * WENN ein TestAntrag bereits in der Datenbank existiert,
     * UND ein TestSachbearbeiter bereits in der DB existiert,
     * UND der Status des TestAntrags "neu" ist,
     * UND der Typ des TestAntrags "Kontoeröffnungs-Antrag" ist,
     * UND der Testantrag vom TestSachbearbeiter erstellt wurde,
     * UND die Methode antragStornieren mit der Id des TestAntrags und der ID des
     * TestSachbearbeiters aufgerufen wird,
     * DANN sollte sie TRUE zurückliefern,
     * UND der TestAntrag sollte im Persistennce Context den Status "storniert" besitzen.
     */
    @Test
    public void antragStornieren_00(){
        Sachbearbeiter testSachbearbeiter = new Sachbearbeiter(null, "Frau", "Manogaran", "Shajangarya", "w", LocalDate.now(),"A1");
        em.persist(testSachbearbeiter);

        Antrag testAntrag = new Antrag(null,"ko","daten","test","n");
        testAntrag.setSachbearbeiter(testSachbearbeiter);
        em.persist(testAntrag);

        boolean actual = classUnderTest.antragStornieren(testAntrag.getAtid(),testSachbearbeiter.getSid());
        assertTrue(actual);
        assertEquals("s",em.find(Antrag.class,testAntrag.getAtid()).getStatus());
    }

    /**@Test: antragStornieren_01()
     * WENN ein TestAntrag bereits in der Datenbank existiert,
     * UND ein TestSachbearbeiter bereits in der DB existiert,
     * UND der Status des TestAntrags "genehmigt" ist,
     * UND der Typ des TestAntrags "Kontoeröffnungs-Antrag" ist,
     * UND der Testantrag vom TestSachbearbeiter erstellt wurde,
     * UND die Methode antragStornieren mit der Id des TestAntrags und der ID des
     * TestSachbearbeiters aufgerufen wird,
     * DANN sollte sie TRUE zurückliefern,
     * UND der TestAntrag sollte im Persistennce Context den Status "storniert" besitzen.
     */
    @Test
    public void antragStornieren_01(){
        Sachbearbeiter testSachbearbeiter = new Sachbearbeiter(null, "Frau", "Manogaran", "Shajangarya", "w", LocalDate.now(),"A1");
        em.persist(testSachbearbeiter);

        Antrag testAntrag = new Antrag(null,"ko","daten","test","g");
        testAntrag.setSachbearbeiter(testSachbearbeiter);
        em.persist(testAntrag);

        boolean actual = classUnderTest.antragStornieren(testAntrag.getAtid(),testSachbearbeiter.getSid());
        assertTrue(actual);
        assertEquals("s",em.find(Antrag.class,testAntrag.getAtid()).getStatus());
    }

    /**@Test: antragStornieren_02()
     * WENN ein TestAntrag bereits in der Datenbank existiert,
     * UND ein TestSachbearbeiter bereits in der DB existiert,
     * UND der Status des TestAntrags "neu" ist,
     * UND der Typ des TestAntrags "Kontoschließungs-Antrag" ist,
     * UND der Testantrag vom TestSachbearbeiter erstellt wurde,
     * UND die Methode antragStornieren mit der Id des TestAntrags und der ID des
     * TestSachbearbeiters aufgerufen wird,
     * DANN sollte sie TRUE zurückliefern,
     * UND der TestAntrag sollte im Persistennce Context den Status "storniert" besitzen.
     */
    @Test
    public void antragStornieren_02(){
        Sachbearbeiter testSachbearbeiter = new Sachbearbeiter(null, "Frau", "Manogaran", "Shajangarya", "w", LocalDate.now(),"A1");
        em.persist(testSachbearbeiter);

        Antrag testAntrag = new Antrag(null,"ks","daten","test","n");
        testAntrag.setSachbearbeiter(testSachbearbeiter);
        em.persist(testAntrag);

        boolean actual = classUnderTest.antragStornieren(testAntrag.getAtid(),testSachbearbeiter.getSid());
        assertTrue(actual);
        assertEquals("s",em.find(Antrag.class,testAntrag.getAtid()).getStatus());
    }

    /**@Test: antragStornieren_03()
     * WENN ein TestAntrag bereits in der Datenbank existiert,
     * UND ein TestSachbearbeiter bereits in der DB existiert,
     * UND der Status des TestAntrags "genehmigt" ist,
     * UND der Typ des TestAntrags "Kontoschließungs-Antrag" ist,
     * UND der Testantrag vom TestSachbearbeiter erstellt wurde,
     * UND die Methode antragStornieren mit der Id des TestAntrags und der ID des
     * TestSachbearbeiters aufgerufen wird,
     * DANN sollte sie TRUE zurückliefern,
     * UND der TestAntrag sollte im Persistennce Context den Status "storniert" besitzen.
     */
    @Test
    public void antragStornieren_03(){
        Sachbearbeiter testSachbearbeiter = new Sachbearbeiter(null, "Frau", "Manogaran", "Shajangarya", "w", LocalDate.now(),"A1");
        em.persist(testSachbearbeiter);

        Antrag testAntrag = new Antrag(null,"ks","daten","test","g");
        testAntrag.setSachbearbeiter(testSachbearbeiter);
        em.persist(testAntrag);

        boolean actual = classUnderTest.antragStornieren(testAntrag.getAtid(),testSachbearbeiter.getSid());
        assertTrue(actual);
        assertEquals("s",em.find(Antrag.class,testAntrag.getAtid()).getStatus());
    }

    /**@Test: antragStornieren_04()
     * WENN ein TestAntrag bereits in der Datenbank existiert,
     * UND ein TestSachbearbeiter bereits in der DB existiert,
     * UND der Status des TestAntrags "neu" ist,
     * UND der Typ des TestAntrags "Kontolimit-Antrag" ist,
     * UND der Testantrag vom TestSachbearbeiter erstellt wurde,
     * UND die Methode antragStornieren mit der Id des TestAntrags und der ID des
     * TestSachbearbeiters aufgerufen wird,
     * DANN sollte sie TRUE zurückliefern,
     * UND der TestAntrag sollte im Persistennce Context den Status "storniert" besitzen.
     */
    @Test
    public void antragStornieren_04(){
        Sachbearbeiter testSachbearbeiter = new Sachbearbeiter(null, "Frau", "Manogaran", "Shajangarya", "w", LocalDate.now(),"A1");
        em.persist(testSachbearbeiter);

        Antrag testAntrag = new Antrag(null,"kl","daten","test","n");
        testAntrag.setSachbearbeiter(testSachbearbeiter);
        em.persist(testAntrag);

        boolean actual = classUnderTest.antragStornieren(testAntrag.getAtid(),testSachbearbeiter.getSid());
        assertTrue(actual);
        assertEquals("s",em.find(Antrag.class,testAntrag.getAtid()).getStatus());
    }

    /**@Test: antragStornieren_05()
     * WENN ein TestAntrag nicht in der Datenbank existiert,
     * UND ein TestSachbearbeiter bereits in der DB existiert,
     * UND die Methode antragStornieren mit der Id des TestAntrags und der ID des
     * TestSachbearbeiters aufgerufen wird,
     * DANN sollte sie FALSE zurückliefern,
     * UND der Persistence Context wurde nicht verändert.
     */
    @Test
    public void antragStornieren_05(){
        Sachbearbeiter testSachbearbeiter = new Sachbearbeiter(null, "Frau", "Manogaran", "Shajangarya", "w", LocalDate.now(),"A1");
        em.persist(testSachbearbeiter);

        Antrag testAntrag = new Antrag(null,"ko","daten","test","g");
        testAntrag.setSachbearbeiter(testSachbearbeiter);
        em.persist(testAntrag);
        em.remove(testAntrag);

        boolean actual = classUnderTest.antragStornieren(testAntrag.getAtid(),testSachbearbeiter.getSid());
        assertFalse(actual);
        assertFalse(em.contains(testAntrag));
    }

    /**@Test: antragStornieren_06()
     * WENN ein TestAntrag bereits in der Datenbank existiert,
     * UND ein TestSachbearbeiter bereits in der DB existiert,
     * UND der Typ des TestAntrags "Kontoeröffnungs-Antrag" ist
     * UND der Status des TestAntrags "abgelehnt" ist,
     * UND der Testantrag vom TestSachbearbeiter erstellt wurde,
     * UND die Methode antragStornieren mit der Id des TestAntrags und der ID des
     * TestSachbearbeiters aufgerufen wird,
     * DANN sollte sie FALSE zurückliefern,
     * UND der Persistence Context wurde nicht verändert.
     */
    @Test
    public void antragStornieren_06(){
        Sachbearbeiter testSachbearbeiter = new Sachbearbeiter(null, "Frau", "Manogaran", "Shajangarya", "w", LocalDate.now(),"A1");
        em.persist(testSachbearbeiter);

        Antrag testAntrag = new Antrag(null,"ko","daten","test","a");
        testAntrag.setSachbearbeiter(testSachbearbeiter);
        em.persist(testAntrag);

        boolean actual = classUnderTest.antragStornieren(testAntrag.getAtid(),testSachbearbeiter.getSid());
        assertFalse(actual);
        assertEquals("a",em.find(Antrag.class,testAntrag.getAtid()).getStatus());
    }

    /**@Test: antragStornieren_07()
     * WENN ein TestAntrag bereits in der Datenbank existiert,
     * UND ein TestSachbearbeiter bereits in der DB existiert,
     * UND der Typ des TestAntrags "Sachbearbeiter-Anlegen-Antrag" ist,
     * UND die Methode antragStornieren mit der Id des TestAntrags und der ID des
     * TestSachbearbeiters aufgerufen wird,
     * DANN sollte sie FALSE zurückliefern,
     * UND der Persistence Context wurde nicht verändert
     */
    @Test
    public void antragStornieren_07(){
        Sachbearbeiter testSachbearbeiter = new Sachbearbeiter(null, "Frau", "Manogaran", "Shajangarya", "w", LocalDate.now(),"A1");
        em.persist(testSachbearbeiter);

        Antrag testAntrag = new Antrag(null,"so","daten","test","n");
        testAntrag.setSachbearbeiter(testSachbearbeiter);
        em.persist(testAntrag);


        boolean actual = classUnderTest.antragStornieren(testAntrag.getAtid(),testSachbearbeiter.getSid());
        assertFalse(actual);
        assertEquals("n",em.find(Antrag.class,testAntrag.getAtid()).getStatus());
    }

    /**@Test: getNeueAntraege_00()
     * WENN ein TestSachbearbeiter bereits in der Datenbank existiert,
     * UND x (x>0) Anträge mit Status "neu" in der DB existieren, die dieser TestSachbearbeiter
     * erstellt hat,
     * UND die Methode getNeueAntraege mit der ID des TestSachbearbeiters aufgerufen wird,
     * DANN sollte sie die Liste mit diesen x Anträgen zurückliefern.
     */
    @Test
    public void getNeueAntraege_00(){
        Sachbearbeiter testSachbearbeiter = new Sachbearbeiter(null, "Frau", "Manogaran", "Shajangarya", "w", LocalDate.now(),"A1");
        em.persist(testSachbearbeiter);

        Antrag testAntrag = new Antrag(null,"sb","daten","test","n");
        testAntrag.setSachbearbeiter(testSachbearbeiter);
        em.persist(testAntrag);

        Antrag testAntrag1 = new Antrag(null,"sb","daten","test","n");
        testAntrag1.setSachbearbeiter(testSachbearbeiter);
        em.persist(testAntrag1);

        List<Antrag> antragList = classUnderTest.getAntragListe(testSachbearbeiter.getSid());
        assertEquals(2,antragList.size());
    }

    /**@Test: getNeueAntraege_01()
     * WENN ein TestSachbearbeiter bereits in der Datenbank existiert,
     * UND keine Anträge mit Status "neu" in der DB existieren, die dieser TestSachbearbeiter
     * erstellt hat,
     * UND die Methode getNeueAntraege mit der ID des TestSachbearbeiters aufgerufen wird,
     * DANN sollte sie eine leere Liste zurückliefern.
     */
    @Test
    public void getNeueAntraege_01(){
        Sachbearbeiter testSachbearbeiter = new Sachbearbeiter(null, "Frau", "Manogaran", "Shajangarya", "w", LocalDate.now(),"A1");
        em.persist(testSachbearbeiter);

        Antrag testAntrag = new Antrag(null,"sb","daten","test","n");
        testAntrag.setSachbearbeiter(testSachbearbeiter);
        em.persist(testAntrag);
        em.remove(testAntrag);

        List<Antrag> antragList = classUnderTest.getAntragListe(testSachbearbeiter.getSid());
        assertTrue(antragList.isEmpty());
    }

    /**@Test: getNeueAntraege_02()
     * WENN ein TestSachbearbeiter nicht in der Datenbank existiert,
     * UND die Methode getNeueAntraege mit der ID des TestSachbearbeiters aufgerufen wird,
     * DANN sollte sie eine leere Liste zurückliefern.
     */
    @Test
    public void getNeueAntraege_02(){
        Sachbearbeiter testSachbearbeiter = new Sachbearbeiter(null, "Frau", "Manogaran", "Shajangarya", "w", LocalDate.now(),"A1");
        em.persist(testSachbearbeiter);
        em.remove(testSachbearbeiter);

        List<Antrag> antragList = classUnderTest.getAntragListe(testSachbearbeiter.getSid());
        assertTrue(antragList.isEmpty());

    }

    /**@Test: getAlleAntraege_00()
     * WENN x (x>0) Anträge in der DB existieren,
     * UND die Methode getAlleAntraege aufgerufen wird,
     * DANN sollte sie die Liste mit diesen x Anträgen zurückliefern.
     */
    @Test
    public void getAlleAntraege_00(){
        Sachbearbeiter testSachbearbeiter1 = new Sachbearbeiter(null, "Frau", "Manogaran", "Shajangarya", "w", LocalDate.now(),"A1");
        em.persist(testSachbearbeiter1);


        Antrag testAntrag1 = new Antrag(null, "so", "Daten", "test", "a");
        testAntrag1.setSachbearbeiter(testSachbearbeiter1);
        em.persist(testAntrag1);

        Antrag testAntrag2 = new Antrag(null, "so", "Daten", "test", "a");
        testAntrag2.setSachbearbeiter(testSachbearbeiter1);
        em.persist(testAntrag2);

        List<Antrag> testliste = classUnderTest.getAlleAntraege();
        assertEquals(2,testliste.size());
    }

    /**@Test: getAlleAntraege01()
     * WENN keine Anträge in der DB existieren,
     * UND die Methode getAlleAntraege aufgerufen wird,
     * DANN sollte sie eine leere Liste zurückliefern.
     */
    @Test
    public void getAlleAntraege_01(){
        Sachbearbeiter testSachbearbeiter1 = new Sachbearbeiter(null, "Frau", "Manogaran", "Shajangarya", "w", LocalDate.now(),"A1");
        em.persist(testSachbearbeiter1);

        Antrag testAntrag1 = new Antrag(null, "so", "Daten", "test", "a");
        testAntrag1.setSachbearbeiter(testSachbearbeiter1);
        em.persist(testAntrag1);
        em.remove(testAntrag1);

        List<Antrag> antragList = classUnderTest.getAntragListe(testSachbearbeiter1.getSid());
        assertTrue(antragList.isEmpty());

    }

    /**@Test: deleteAntrag_00()
    WENN ein TestSachbearbeiter bereits in der Datenbank existiert
    UND ein TestAntrag mit Status "abgelehnt" und Typ "Kontolimit-Antrag" bereits der DB
    existiert
    UND der TestSachbearbeiter den TestAntrag erstellt hat,
    UND die Methode deleteAntrag mit der ID des Testantrags und der ID des
    TestSachbearbeiters aufgerufen wird,
    DANN sollte sie TRUE zurückliefern,
    UND der TestAntrag sollte aus der DB entfernt sein.
     */
    @Test
    public void deleteAntrag_00() {
        Sachbearbeiter testSachbearbeiter = new Sachbearbeiter(null, "Frau", "Imbrescu", "Raluca", "w",
                LocalDate.of(1999, 03, 17), "A1");
        em.persist(testSachbearbeiter);
        Antrag testAntrag = new Antrag(null, "kl", "Daten", "test", "a");
        testAntrag.setSachbearbeiter(testSachbearbeiter);
        em.persist(testAntrag);
        assertTrue(classUnderTest.deleteAntrag(testAntrag.getAtid(), testSachbearbeiter.getSid()));
        Antrag deleteAntrag = em.find(Antrag.class, testAntrag.getAtid());
        assertNull(deleteAntrag);
    }

    /**@Test: deleteAntrag_01()
    WENN ein TestSachbearbeiter bereits in der Datenbank existiert,
    UND ein TestAntrag mit Status "abgelehnt" und Typ "Kontoschließungs-Antrag" bereits
    der DB existiert,
    UND der TestSachbearbeiter den TestAntrag erstellt hat,
    UND die Methode deleteAntrag mit der ID des Testantrags und der ID des
    TestSachbearbeiters aufgerufen wird,
    DANN sollte sie TRUE zurückliefern,
    UND der TestAntrag sollte aus der DB entfernt sei
     */
    @Test
    public void deleteAntrag_01() {
        Sachbearbeiter testSachbearbeiter = new Sachbearbeiter(null, "Frau", "Imbrescu", "Raluca", "w",
                LocalDate.of(1999, 03, 17), "A1");
        em.persist(testSachbearbeiter);
        Antrag testAntrag = new Antrag(null, "ks", "Daten", "test", "a");
        testAntrag.setSachbearbeiter(testSachbearbeiter);
        em.persist(testAntrag);
        assertTrue(classUnderTest.deleteAntrag(testAntrag.getAtid(), testSachbearbeiter.getSid()));
        Antrag deleteAntrag = em.find(Antrag.class, testAntrag.getAtid());
        assertNull(deleteAntrag);
    }
    /** @Test: deleteAntrag_02()
    WENN ein TestSachbearbeiter bereits in der Datenbank existiert,
    UND ein TestAntrag mit Status "abgelehnt" und Typ "Kontoeröffnungs-Antrag" bereits
    der DB existiert,
    UND der TestSachbearbeiter den TestAntrag erstellt hat,
    UND die Methode deleteAntrag mit der ID des Testantrags und der ID des
    TestSachbearbeiters aufgerufen wird,
    DANN sollte sie TRUE zurückliefern,
    UND der TestAntrag sollte aus der DB entfernt sein.
     */
    @Test
    public void deleteAntrag_02() {
        Sachbearbeiter testSachbearbeiter = new Sachbearbeiter(null, "Frau", "Imbrescu", "Raluca", "w",
                LocalDate.of(1999, 03, 17), "A1");
        em.persist(testSachbearbeiter);
        Antrag testAntrag = new Antrag(null, "ko", "Daten", "test", "a");
        testAntrag.setSachbearbeiter(testSachbearbeiter);
        em.persist(testAntrag);
        assertTrue(classUnderTest.deleteAntrag(testAntrag.getAtid(), testSachbearbeiter.getSid()));
        Antrag deleteAntrag = em.find(Antrag.class, testAntrag.getAtid());
        assertNull(deleteAntrag);
    }

    /**@Test: deleteAntrag_03()
    WENN ein TestSachbearbeiter bereits in der Datenbank existiert,
    UND ein TestAntrag mit Status "genehmigt" und Typ "Kontoeröffnungs-Antrag" bereits
    der DB existiert,
    UND der TestSachbearbeiter den TestAntrag erstellt hat,
    UND die Methode deleteAntrag mit der ID des Testantrags und der ID des
    TestSachbearbeiters aufgerufen wird,
    DANN sollte sie FALSE zurückliefern,
    UND der TestAntrag sollte nicht aus der DB entfernt sein.
     */
    @Test
    public void deleteAntrag_03() {
        Sachbearbeiter testSachbearbeiter = new Sachbearbeiter(null, "Frau", "Imbrescu", "Raluca", "w",
                LocalDate.of(1999, 03, 17), "A1");
        em.persist(testSachbearbeiter);
        Antrag testAntrag = new Antrag(null, "ko", "Daten", "test", "g");
        testAntrag.setSachbearbeiter(testSachbearbeiter);
        em.persist(testAntrag);
        assertFalse(classUnderTest.deleteAntrag(testAntrag.getAtid(), testSachbearbeiter.getSid()));
        Antrag deleteAntrag = em.find(Antrag.class, testAntrag.getAtid());
        assertNotNull(deleteAntrag);
    }

    /** @Test: deleteAntrag_04()
    WENN ein TestSachbearbeiter bereits in der Datenbank existiert,
    UND ein TestAntrag mit Status "abgelehnt" und Typ "Kontoschließungs-Antrag" bereits
    der DB existiert,
    UND der TestSachbearbeiter den TestAntrag nicht erstellt hat,
    UND die Methode deleteAntrag mit der ID des Testantrags und der ID des
    TestSachbearbeiters aufgerufen wird,
    DANN sollte sie FALSE zurückliefern,
    UND der TestAntrag sollte nicht aus der DB entfernt sein.
     */
    @Test
    public void deleteAntrag_04() {
        Sachbearbeiter testSachbearbeiter = new Sachbearbeiter(null, "Frau", "Imbrescu", "Diana", "w",
                LocalDate.of(1999, 03, 17), "A1");
        em.persist(testSachbearbeiter);
        Antrag testAntrag = new Antrag(null, "ks", "Daten", "test", "a");
        em.persist(testAntrag);
        assertFalse(classUnderTest.deleteAntrag(testAntrag.getAtid(), testSachbearbeiter.getSid()));
        Antrag deleteAntrag = em.find(Antrag.class, testAntrag.getAtid());
        assertNotNull(deleteAntrag);
    }

    /** @Test: deleteAntrag_05()
    WENN ein TestSachbearbeiter bereits in der Datenbank existiert,
    UND ein TestAntrag mit Status "abgelehnt" und Typ "Sachbearbeiter-Anlegen" bereits
    der DB existiert,
    UND die Methode deleteAntrag mit der ID des Testantrags und der ID des
    TestSachbearbeiters aufgerufen wird,
    DANN sollte sie FALSE zurückliefern,
    UND der TestAntrag sollte nicht aus der DB entfernt sein.

     */
    @Test
    public void deleteAntrag_05() {
        Sachbearbeiter testSachbearbeiter = new Sachbearbeiter(null, "Frau", "Imbrescu", "Raluca", "w",
                LocalDate.of(1999, 03, 17), "A1");
        em.persist(testSachbearbeiter);
        Antrag testAntrag = new Antrag(null, "so", "Daten", "test", "a");
        em.persist(testAntrag);
        assertFalse(classUnderTest.deleteAntrag(testAntrag.getAtid(), testSachbearbeiter.getSid()));
        Antrag deleteAntrag = em.find(Antrag.class, testAntrag.getAtid());
        assertNotNull(deleteAntrag);
    }

    /** @Test: getAbgelehnteAntraege_00()
    WENN ein TestSachbearbeiter bereits in der Datenbank existiert,
    UND x (x>0) Anträge mit Status "abgelehnt" in der DB existieren, die dieser
    TestSachbearbeiter erstellt hat,
    UND die Methode getAbgelehnteAntraege mit der ID des TestSachbearbeiters aufgerufen
    wird,
    DANN sollte sie die Liste mit diesen x Anträgen zurückliefern.
     */
    @Test
    public void getAbgelehnteAntraege_00() {
        Sachbearbeiter testSachbearbeiter = new Sachbearbeiter(null, "Frau", "Imbrescu", "Raluca", "w",
                LocalDate.of(1999, 03, 17), "A1");
        em.persist(testSachbearbeiter);
        Antrag testAntrag1 = new Antrag(null, "so", "Daten", "test", "a");
        testAntrag1.setSachbearbeiter(testSachbearbeiter);
        em.persist(testAntrag1);
        Antrag testAntrag2 = new Antrag(null, "ks", "Daten", "test", "a");
        testAntrag2.setSachbearbeiter(testSachbearbeiter);
        em.persist(testAntrag2);
        // zusätzliche Objekt zum testen
        Antrag testAntrag3 = new Antrag(null, "ks", "Daten", "test", "b");
        testAntrag3.setSachbearbeiter(testSachbearbeiter);
        em.persist(testAntrag3);
        //
        List<Antrag> antragList = classUnderTest.getAbgelehnteAntraege(testSachbearbeiter.getSid());
        assertEquals(2, antragList.size());
        assertTrue(antragList.contains(testAntrag1));
        assertTrue(antragList.contains(testAntrag2));
    }

    /** @Test: getAbgelehnteAntraege_01()
    WENN ein TestSachbearbeiter bereits in der Datenbank existiert,
    UND keine Anträge mit Status "abgelehnt" in der DB existieren, die dieser
    TestSachbearbeiter erstellt hat,
    UND die Methode getAbgelehnteAntraege mit der ID des TestSachbearbeiters aufgerufen
    wird,
    DANN sollte sie eine leere Liste zurückliefern.
     */
    @Test
    public void getAbgelehnteAntraege_01() {
        Sachbearbeiter testSachbearbeiter = new Sachbearbeiter(null, "Frau", "Imbrescu", "Raluca", "w",
                LocalDate.of(1999, 03, 17), "A1");
        em.persist(testSachbearbeiter);
        Antrag testAntrag1 = new Antrag(null, "so", "Daten", "test", "g");
        testAntrag1.setSachbearbeiter(testSachbearbeiter);
        em.persist(testAntrag1);
        Antrag testAntrag2 = new Antrag(null, "ks", "Daten", "test", "g");
        testAntrag2.setSachbearbeiter(testSachbearbeiter);
        em.persist(testAntrag2);
        // zusätzliche Objekte zum testen
        Sachbearbeiter testSachbearbeiter3 = new Sachbearbeiter(null, "Frau", "Imbrescu", "Raluca", "w",
                LocalDate.of(1999, 03, 17), "A1");
        em.persist(testSachbearbeiter3);
        Antrag testAntrag3 = new Antrag(null, "ks", "Daten", "test", "a");
        testAntrag3.setSachbearbeiter(testSachbearbeiter3);
        em.persist(testAntrag3);
        //
        List<Antrag> antragList = classUnderTest.getAbgelehnteAntraege(testSachbearbeiter.getSid());
        assertTrue(antragList.isEmpty());

    }

    /** @Test: getAbgelehnteAntraege_02()
    WENN ein TestSachbearbeiter nicht in der Datenbank existiert,
    UND die Methode getAbgelehnteAntraege mit der ID des TestSachbearbeiters aufgerufen
    wird,
    DANN sollte sie eine leere Liste zurückliefern.
     */
    @Test
    public void getAbgelehnteAntraege_02() {
        Sachbearbeiter testSachbearbeiter = new Sachbearbeiter(null, "Frau", "Imbrescu", "Raluca", "w",
                LocalDate.of(1999, 03, 17), "A1");

        int sid = ThreadLocalRandom.current().nextInt(1, 100000);
        Sachbearbeiter existingSachbearbeiter = em.find(Sachbearbeiter.class,sid);
        if (existingSachbearbeiter != null) {
            em.remove(existingSachbearbeiter);
        }
        testSachbearbeiter.setSid(sid);
        // zusätzliche Objekt zum testen
        Sachbearbeiter testSachbearbeiter2 = new Sachbearbeiter(null, "Frau", "Imbrescu", "Diana", "w",
                LocalDate.of(1999, 03, 17), "A1");
        em.persist(testSachbearbeiter2);

        Antrag testAntrag1 = new Antrag(null, "so", "Daten", "test", "a");
        testAntrag1.setSachbearbeiter(testSachbearbeiter2);
        em.persist(testAntrag1);
        //
        List<Antrag> antragList = classUnderTest.getAbgelehnteAntraege(testSachbearbeiter.getSid());
        assertTrue(antragList.isEmpty());

    }

    /** @Test: getBearbeiteteAntraege_00()
    WENN ein TestSachbearbeiter bereits in der Datenbank existiert,
    UND x (x>0) Anträge mit Status "bearbeitet" in der DB existieren, die dieser
    TestSachbearbeiter erstellt hat,
    UND die Methode getBearbeiteteAntraege mit der ID des TestSachbearbeiters aufgerufen
    wird,
    DANN sollte sie die Liste mit diesen x Anträgen zurückliefern.
     */
    @Test
    public void getBearbeiteteAntraege_00() {
        Sachbearbeiter testSachbearbeiter = new Sachbearbeiter(null, "Frau", "Imbrescu", "Raluca", "w",
                LocalDate.of(1999, 03, 17), "A1");
        em.persist(testSachbearbeiter);
        Antrag testAntrag1 = new Antrag(null, "so", "Daten", "test", "b");
        testAntrag1.setSachbearbeiter(testSachbearbeiter);
        em.persist(testAntrag1);
        Antrag testAntrag2 = new Antrag(null, "ks", "Daten", "test", "b");
        testAntrag2.setSachbearbeiter(testSachbearbeiter);
        em.persist(testAntrag2);
        // zusätzliche Objekt zum testen
        Antrag testAntrag3 = new Antrag(null, "ks", "Daten", "test", "a");
        testAntrag3.setSachbearbeiter(testSachbearbeiter);
        em.persist(testAntrag3);
        //
        List<Antrag> antragList = classUnderTest.getBearbeiteteAntraege(testSachbearbeiter.getSid());

        assertEquals(2, antragList.size());
        assertTrue(antragList.contains(testAntrag1));
        assertTrue(antragList.contains(testAntrag2));

    }

    /** @Test: getBearbeiteteAntraege_01()
     * WENN ein TestSachbearbeiter bereits in der Datenbank existiert,
     * UND keine Anträge mit Status "bearbeitet" in der DB existieren, die dieser
     * TestSachbearbeiter erstellt hat,
     * UND die Methode getBearbeiteteAntraege mit der ID des TestSachbearbeiters aufgerufen
     * wird,
     * DANN sollte sie eine leere Liste zurückliefern.
     */
    @Test
    public void getBearbeiteteAntraege_01() {
        Sachbearbeiter testSachbearbeiter = new Sachbearbeiter(null, "Frau", "Imbrescu", "Raluca", "w",
                LocalDate.of(1999, 03, 17), "A1");
        em.persist(testSachbearbeiter);
        Antrag testAntrag1 = new Antrag(null, "so", "Daten", "test", "b");
        em.persist(testAntrag1);
        Antrag testAntrag2 = new Antrag(null, "ks", "Daten", "test", "b");
        em.persist(testAntrag2);
        // zusätzliche Objekt zum testen
        Antrag testAntrag3 = new Antrag(null, "ks", "Daten", "test", "a");
        testAntrag3.setSachbearbeiter(testSachbearbeiter);
        em.persist(testAntrag3);
        //
        List<Antrag> antragList = classUnderTest.getBearbeiteteAntraege(testSachbearbeiter.getSid());
        assertTrue(antragList.isEmpty());
    }

    /** @Test: getBearbeiteteAntraege_02()
    WENN ein TestSachbearbeiter nicht in der Datenbank existiert,
    UND die Methode getBearbeiteteAntraege mit der ID des TestSachbearbeiters aufgerufen wird,
    DANN sollte sie eine leere Liste zurückliefern
     */
    @Test
    public void getBearbeiteteAntraege_02() {
        Sachbearbeiter testSachbearbeiter = new Sachbearbeiter(null, "Frau", "Imbrescu", "Raluca", "w",
                LocalDate.of(1999, 03, 17), "A1");
        int sid = ThreadLocalRandom.current().nextInt(1, 100000);
        Sachbearbeiter existingSachbearbeiter = em.find(Sachbearbeiter.class,sid);
        if (existingSachbearbeiter != null) {
            em.remove(existingSachbearbeiter);
        }
        testSachbearbeiter.setSid(sid);
        // zusätzliche Objekt zum testen
        Sachbearbeiter testSachbearbeiter2 = new Sachbearbeiter(null, "Frau", "Imbrescu", "Diana", "w",
                LocalDate.of(1999, 03, 18), "A1");
        em.persist(testSachbearbeiter2);

        Antrag testAntrag1 = new Antrag(null, "so", "Daten", "test", "b");
        testAntrag1.setSachbearbeiter(testSachbearbeiter2);
        em.persist(testAntrag1);
        //
        List<Antrag> antragList = classUnderTest.getBearbeiteteAntraege(testSachbearbeiter.getSid());
        assertTrue(antragList.isEmpty());
    }

    /** @Test: getStornierteAntraege_00()
    WENN ein TestSachbearbeiter bereits in der Datenbank existiert,
    UND x (x>0) Anträge mit Status "storniert" in der DB existieren, die dieser
    TestSachbearbeiter erstellt hat,
    UND die Methode getStornierteAntraege mit der ID des TestSachbearbeiters aufgerufen
    wird,
    DANN sollte sie die Liste mit diesen x Anträgen zurückliefern.
     */
    @Test
    public void getStornierteAntraege_00() {
        Sachbearbeiter testSachbearbeiter = new Sachbearbeiter(null, "Frau", "Imbrescu", "Raluca", "w",
                LocalDate.of(1999, 03, 17), "A1");
        em.persist(testSachbearbeiter);
        Antrag testAntrag1 = new Antrag(null, "so", "Daten", "test", "s");
        testAntrag1.setSachbearbeiter(testSachbearbeiter);
        em.persist(testAntrag1);
        Antrag testAntrag2 = new Antrag(null, "ks", "Daten", "test", "s");
        testAntrag2.setSachbearbeiter(testSachbearbeiter);
        em.persist(testAntrag2);
        //Zusäzlicher Objekt zum testen
        Antrag testAntrag3 = new Antrag(null, "ks", "Daten", "test", "a");
        testAntrag3.setSachbearbeiter(testSachbearbeiter);
        em.persist(testAntrag3);
        //
        List<Antrag> antragList = classUnderTest.getStornierteAntraege(testSachbearbeiter.getSid());
        assertEquals(2, antragList.size());
        assertTrue(antragList.contains(testAntrag1));
        assertTrue(antragList.contains(testAntrag2));
    }

    /** @Test: getStornierteAntraege_01()
    WENN ein TestSachbearbeiter bereits in der Datenbank existiert,
    UND keine Anträge mit Status "storniert" in der DB existieren, die dieser
    TestSachbearbeiter erstellt hat,
    UND die Methode getStornierteAntraege mit der ID des TestSachbearbeiters aufgerufen
    wird,
    DANN sollte sie eine leere Liste zurückliefern.
     */
    @Test
    public void getStornierteAntraege_01() {
        Sachbearbeiter testSachbearbeiter = new Sachbearbeiter(null, "Frau", "Imbrescu", "Raluca", "w",
                LocalDate.of(1999, 03, 17), "A1");
        em.persist(testSachbearbeiter);
        Antrag testAntrag1 = new Antrag(null, "so", "Daten", "test", "s");
        em.persist(testAntrag1);
        Antrag testAntrag2 = new Antrag(null, "ks", "Daten", "test", "s");
        em.persist(testAntrag2);
        //Zusäzliche Objekt zum testen
        Antrag testAntrag3 = new Antrag(null, "ks", "Daten", "test", "a");
        em.persist(testAntrag3);
        testAntrag3.setSachbearbeiter(testSachbearbeiter);
        //
        List<Antrag> antragList = classUnderTest.getStornierteAntraege(testSachbearbeiter.getSid());
        assertTrue(antragList.isEmpty());
    }

    /**@Test: getStornierteAntraege_02()
    WENN ein TestSachbearbeiter nicht in der Datenbank existiert,
    UND die Methode getStornierteAntraege mit der ID des TestSachbearbeiters aufgerufen wird,
    DANN sollte sie eine leere Liste zurückliefern.
     */
    @Test
    public void getStornierteAntraege_02() {
        Sachbearbeiter testSachbearbeiter = new Sachbearbeiter(null, "Frau", "Imbrescu", "Raluca", "w",
                LocalDate.of(1999, 03, 17), "A1");
        int sid = ThreadLocalRandom.current().nextInt(1, 100000);
        Sachbearbeiter existingSachbearbeiter = em.find(Sachbearbeiter.class,sid);
        if (existingSachbearbeiter != null) {
            em.remove(existingSachbearbeiter);
        }
        testSachbearbeiter.setSid(sid);
        // zusätzliche Objekt zum testen
        Sachbearbeiter testSachbearbeiter2 = new Sachbearbeiter(null, "Frau", "Imbrescu", "Raluca", "w",
                LocalDate.of(2000, 12, 18), "A1");
        em.persist(testSachbearbeiter2);

        Antrag testAntrag1 = new Antrag(null, "so", "Daten", "test", "s");
        testAntrag1.setSachbearbeiter(testSachbearbeiter2);
        em.persist(testAntrag1);
        //
        List<Antrag> antragList = classUnderTest.getStornierteAntraege(testSachbearbeiter.getSid());
        assertTrue(antragList.isEmpty());
    }

    /** @Test: getGenehmigteAntraege_00()
    WENN ein TestSachbearbeiter bereits in der Datenbank existiert,
    UND x (x>0) Anträge mit Status "genehmigt" in der DB existieren, die dieser
    TestSachbearbeiter erstellt hat,
    UND die Methode getGenehmigteAntraege mit der ID des TestSachbearbeiters aufgerufen, wird
    DANN sollte sie die Liste mit diesen x Anträgen zurückliefern
     */
    @Test
    public void getGenehmigteAntraege_00() {
        Sachbearbeiter testSachbearbeiter = new Sachbearbeiter(null, "Frau", "Imbrescu", "Raluca", "w",
                LocalDate.of(1999, 03, 17), "A1");
        em.persist(testSachbearbeiter);
        Antrag testAntrag1 = new Antrag(null, "so", "Daten", "test", "g");
        testAntrag1.setSachbearbeiter(testSachbearbeiter);
        em.persist(testAntrag1);
        Antrag testAntrag2 = new Antrag(null, "ks", "Daten", "test", "g");
        testAntrag2.setSachbearbeiter(testSachbearbeiter);
        em.persist(testAntrag2);
        //Zusätliche Objekte zum testen
        Antrag testAntrag3 = new Antrag(null, "ks", "Daten", "test", "g");
        testAntrag3.setSachbearbeiter(testSachbearbeiter);
        //
        List<Antrag> antragList = classUnderTest.getGenehmigteAntraege(testSachbearbeiter.getSid());
        assertEquals(2, antragList.size());
        assertTrue(antragList.contains(testAntrag1));
        assertTrue(antragList.contains(testAntrag2));
    }

    /** @Test: getGenehmigteAntraege_01()
    WENN ein TestSachbearbeiter bereits in der Datenbank existiert,
    UND keine Anträge mit Status "genehmigt" in der DB existieren, die dieser
    TestSachbearbeiter erstellt hat,
    UND die Methode getGenehmigteAntraege mit der ID des TestSachbearbeiters aufgerufen
    wird,
    DANN sollte sie eine leere Liste zurückliefern
     */
    @Test
    public void getGenehmigteAntraege_01() {
        Sachbearbeiter testSachbearbeiter = new Sachbearbeiter(null, "Frau", "Imbrescu", "Raluca", "w",
                LocalDate.of(1999, 03, 17), "A1");
        em.persist(testSachbearbeiter);
        Antrag testAntrag1 = new Antrag(null, "so", "Daten", "test", "g");
        em.persist(testAntrag1);
        Antrag testAntrag2 = new Antrag(null, "ks", "Daten", "test", "g");
        em.persist(testAntrag2);
        //Zusätliche Objekt zum testen
        Antrag testAntrag3 = new Antrag(null, "ks", "Daten", "test", "a");
        em.persist(testAntrag3);
        //
        testAntrag3.setSachbearbeiter(testSachbearbeiter);
        List<Antrag> antragList = classUnderTest.getGenehmigteAntraege(testSachbearbeiter.getSid());
        assertTrue(antragList.isEmpty());
    }

    /** @Test: getGenehmigteAntraege_02()
    WENN ein TestSachbearbeiter nicht in der Datenbank existiert,
    UND die Methode getGenehmigteAntraege mit der ID des TestSachbearbeiters aufgerufen
    wird,
    DANN sollte sie eine leere Liste zurückliefern
     */
    @Test
    public void getGenehmigteAntraege_02() {
        Sachbearbeiter testSachbearbeiter = new Sachbearbeiter(null, "Frau", "Imbrescu", "Raluca", "w",
                LocalDate.of(1999, 03, 17), "A1");
        int sid = ThreadLocalRandom.current().nextInt(1, 100000);
        Sachbearbeiter existingSachbearbeiter = em.find(Sachbearbeiter.class,sid);
        if (existingSachbearbeiter != null) {
            em.remove(existingSachbearbeiter);
        }
        testSachbearbeiter.setSid(sid);
        // zusätzliche Objekt zum testen
        Sachbearbeiter testSachbearbeiter2 = new Sachbearbeiter(null, "Frau", "Imbrescu", "Diana", "w",
                LocalDate.of(2000, 12, 18), "A1");
        em.persist(testSachbearbeiter2);

        Antrag testAntrag1 = new Antrag(null, "so", "Daten", "test", "g");
        testAntrag1.setSachbearbeiter(testSachbearbeiter2);
        em.persist(testAntrag1);
        //
        List<Antrag> antragList = classUnderTest.getGenehmigteAntraege(testSachbearbeiter.getSid());
        assertTrue(antragList.isEmpty());
    }

}

