package de.thkoeln.swp.bks.sachbearbeiterdaten;

import de.thkoeln.swp.bks.bksdbmodel.entities.Konto;
import de.thkoeln.swp.bks.bksdbmodel.entities.Kunde;
import de.thkoeln.swp.bks.bksdbmodel.entities.Ueberweisung;
import de.thkoeln.swp.bks.bksdbmodel.entities.UeberweisungsVorlage;
import de.thkoeln.swp.bks.bksdbmodel.impl.IDatabaseImpl;
import de.thkoeln.swp.bks.bksdbmodel.services.IDatabase;
import de.thkoeln.swp.bks.datenhaltungapi.IUeberweisungsVorlage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

public class IUeberweisungsVorlageImplTest {
    public IUeberweisungsVorlage classUnderTest;
    public static EntityManager em;
    public static final IDatabase db = new IDatabaseImpl();

    Kunde testKunde;
    Konto testKonto;
    UeberweisungsVorlage testUeberweisungsVorlage;


    /**@Before: angenommen()
     * Angenommen der EntityManager wird korrekt geholt,
     * UND die Implementierung der IUeberweisungsVorlage Schnittstelle wird als classUnderTest instanziiert,
     * UND der EntityManager em wird per setEntityManager Methode der classUnderTest gesetzt,
     * UND die Transaktion von em wird gestartet,
     * UND die Daten der betreffenden Entitäten wurden im Persistence Context gelöscht.
    */

    @Before
    public void angenommen(){


        em = db.getEntityManager();
       

        classUnderTest = new IUeberweisungsVorlageImpl();

        classUnderTest.setEntityManager(em);
        em.getTransaction().begin();

        //Alle Daten aus der DB loeschen
        em.createQuery("DELETE FROM Ueberweisung").executeUpdate();
        em.createQuery("DELETE FROM UeberweisungsVorlage").executeUpdate();
        em.createQuery("DELETE FROM Zahlungsverkehr").executeUpdate();
        em.createQuery("DELETE FROM Konto").executeUpdate();
        em.createQuery("DELETE FROM Kunde").executeUpdate();

        em.getTransaction().setRollbackOnly();
    }

    /** @After: amEnde()
     * Am Ende wird die Transaktion zurückgesetzt.
    */

    @After
    public void amEnde(){
        em.getTransaction().rollback();
    }

    /** @Test: getUeberweisungsVorlageByID_00()
     * WENN eine TestUeberweisungsVorlage bereits in der DB existiert,
     * UND die Methode getUeberweisungsVorlageByID mit der Id der TestUeberweisungsVorlage aufgerufen wird,
     * DANN sollte sie die TestUeberweisungsVorlage zurückliefern
    */

    @Test
    public void getUeberweisungsVorlageByID_00(){
        testKunde = new Kunde(null, "Herr", "Soufian", "El", LocalDate.of(2022,5,4), "M", "l");
        em.persist(testKunde);

        testKonto = new Konto(null, "g", LocalDate.of(2022,5,4), 5000.0 , 1000, "h");
        em.persist(testKonto);
        testKonto.setKunde(testKunde);

        testUeberweisungsVorlage  = new UeberweisungsVorlage(null,LocalDate.now(),"Verwendugszweck0",5);
        testUeberweisungsVorlage.setKunde(testKunde);
        testUeberweisungsVorlage.setVonkonto(testKonto);
        em.persist(testUeberweisungsVorlage );

        UeberweisungsVorlage erwarteteListe = classUnderTest.getUeberweisungsVorlageByID(testUeberweisungsVorlage.getUvid());
        assertEquals(testUeberweisungsVorlage , erwarteteListe);
    }

    /** @Test: getUeberweisungsVorlageByID_01()
       WENN eine TestUeberweisungsVorlage nicht in der DB existiert,
       UND die Methode getUeberweisungsVorlageByID mit der Id der TestUeberweisungsVorlage aufgerufen wird,
       DANN sollte sie NULL zurückliefern.
    */

    @Test
    public void getUeberweisungsVorlageByID_01(){
        testKunde = new Kunde(null, "Herr", "Soufian", "El", LocalDate.of(2022,5,4), "M", "l");
        em.persist(testKunde);

        testKonto = new Konto(null, "g", LocalDate.of(2022,5,4), 5000.0 , 1000, "h");
        em.persist(testKonto);
        testKonto.setKunde(testKunde);

        testUeberweisungsVorlage = new UeberweisungsVorlage(null, LocalDate.now(), "Verwendungszweck0", 5);
        testUeberweisungsVorlage.setKunde(testKunde);
        testUeberweisungsVorlage.setVonkonto(testKonto);
        em.persist(testUeberweisungsVorlage);
        em.remove(testUeberweisungsVorlage);


        assertNull(classUnderTest.getUeberweisungsVorlageByID(testUeberweisungsVorlage.getUvid()));
    }

   /** @Test: getUeberweisungsVorlagenDesKunden_00()
      WENN ein TestKunde bereits in der DB existiert,
      UND x (x>0) ÜberweisungsVorlagen in der DB existieren, deren Attribut kunde auf den TestKunden verweisen,
      UND die Methode getUeberweisungsVorlagenDesKunden mit der ID des TestKunden aufgerufen wird,
      DANN sollte sie die Liste mit den x ÜberweisungsVorlagen zurückliefern.
   */

    @Test
    public void getUeberweisungsVorlagenDesKunden_00(){
        testKunde = new Kunde(null, "Herr", "Soufian", "El", LocalDate.of(2022,5,4), "M", "l");
        em.persist(testKunde);

        testKonto = new Konto(null, "g", LocalDate.of(2022,5,4), 5000.0 , 1000, "h");
        em.persist(testKonto);
        testKonto.setKunde(testKunde);

        int x=10; //Anzahl der ÜberweisungsVorlagen x(x>0)
        for(int i=0; i<x; i++){
            testUeberweisungsVorlage  = new UeberweisungsVorlage(null,LocalDate.of(202+i,5,4+i),"Verwendugszweck"+i,i*1000);
            testUeberweisungsVorlage.setKunde(testKunde);
            testUeberweisungsVorlage.setVonkonto(testKonto);
            em.persist(testUeberweisungsVorlage);
        }

        List<UeberweisungsVorlage> erwarteteListe = classUnderTest.getUeberweisungsVorlagenDesKunden(testKunde.getKid());
        // Liste mit den x ÜberweisungsVorlagen zurückliefern
        assertEquals(x, erwarteteListe.size());
    }

    /** @Test: getUeberweisungsVorlagenDesKunden_01()
       WENN ein TestKunde bereits in der DB existiert,
       UND keine ÜberweisungsVorlagen in der DB existieren, deren Attribut kunde auf den TestKunden verweisen,
       UND die Methode getUeberweisungsVorlagenDesKunden mit der ID des TestKunden aufgerufen wird,
       DANN sollte sie eine leere Liste zurückliefern.
   */

    @Test
    public void getUeberweisungsVorlageDesKunden_01(){
        testKunde = new Kunde(null, "Herr", "Soufian", "El", LocalDate.of(2022,5,4), "M", "l");
        em.persist(testKunde);
        testUeberweisungsVorlage  = new UeberweisungsVorlage(null,LocalDate.now(),"Verwendugszweck0",5);
        List<UeberweisungsVorlage> erwarteteList = classUnderTest.getUeberweisungsVorlagenDesKunden(testKunde.getKid());
        assertTrue(erwarteteList.isEmpty());
    }

    /** @Test: getUeberweisungsVorlagenDesKunden_02()
       WENN ein TestKunde nicht in der DB existiert,
       UND die Methode getUeberweisungsVorlagenDesKunden mit der ID des TestKunden aufgerufen wird,
       DANN sollte sie eine leere Liste zurückliefern.
   */

    @Test
    public void getUeberweisungsVorlagenDesKunden_02(){
        testKunde = new Kunde(1, "Herr", "Soufian", "El", LocalDate.of(2022,5,4), "M", "l");
        List<UeberweisungsVorlage> erwarteteList = classUnderTest.getUeberweisungsVorlagenDesKunden(testKunde.getKid());
        assertTrue(erwarteteList.isEmpty());
    }

    /** @Test: getUeberweisungsVorlagenDesKontos_00()
       WENN ein TestKonto bereits in der DB existiert,
       UND x (x>0) ÜberweisungsVorlagen in der DB existieren, deren Attribut vonkonto auf das TestKonto verweisen,
       UND die Methode getUeberweisungsVorlagenDesKontos mit der ID des TestKontos aufgerufen wird,
       DANN sollte sie die Liste mit den x ÜberweisungsVorlagen zurückliefern.
   */

    @Test
    public void getUeberweisungsVorlagenDesKontos_00(){
        testKunde = new Kunde(null, "Herr", "Soufian", "El", LocalDate.of(2022,5,4), "M", "l");
        em.persist(testKunde);

        testKonto = new Konto(null, "g", LocalDate.of(2022,5,4), 5000.0 , 1000, "h");
        em.persist(testKonto);
        testKonto.setKunde(testKunde);

        int x=10; //Anzahl der ÜberweisungsVorlagen x(x>0)

        for(int i=0; i<x; i++){
            testUeberweisungsVorlage = new UeberweisungsVorlage(null,LocalDate.of(202+i,5,4+i),"Verwendugszweck"+i,i*1000);
            testUeberweisungsVorlage.setKunde(testKunde);
            testUeberweisungsVorlage.setVonkonto(testKonto);
            em.persist(testUeberweisungsVorlage);
        }

        List<UeberweisungsVorlage> erwarteteListe = classUnderTest.getUeberweisungsVorlagenDesKontos(testKonto.getKtoid());
        assertEquals(x, erwarteteListe.size());
    }

    /** @Test: getUeberweisungsVorlagenDesKontos_01()
       WENN ein TestKonto bereits in der DB existiert,
       UND keine ÜberweisungsVorlagen in der DB existieren, deren Attribut vonkonto auf das TestKonto verweisen,
       UND die Methode getUeberweisungsVorlagenDesKontos mit der ID des TestKontos aufgerufen wird,
       DANN sollte sie eine leere Liste zurückliefern.
   */

    @Test
    public void getUeberweisungsVorlagenDesKontos_01(){
        testKunde = new Kunde(null, "Herr", "Soufian", "El", LocalDate.of(2022,5,4), "M", "l");
        em.persist(testKunde);

        testKonto = new Konto(null, "g", LocalDate.of(2022,5,4), 5000.0 , 1000, "h");
        em.persist(testKonto);
        testKonto.setKunde(testKunde);

        testUeberweisungsVorlage  = new UeberweisungsVorlage(null,LocalDate.now(),"Verwendugszweck0",5);

        List<UeberweisungsVorlage> erwarteteList = classUnderTest.getUeberweisungsVorlagenDesKontos(testKonto.getKtoid());
        assertTrue(erwarteteList.isEmpty());

    }

   /** @Test: getUeberweisungsVorlagenDesKontos_02()
      WENN ein TestKonto nicht in der DB existiert,
      UND die Methode getUeberweisungsVorlagenDesKontos mit der ID des TestKontos aufgerufen wird,
      DANN sollte sie eine leere Liste zurückliefern.
   */

    @Test
    public void getUeberweisungsVorlagenDesKontos_02(){
        testKunde = new Kunde(null, "Herr", "Soufian", "El", LocalDate.of(2022,5,4), "M", "l");
        em.persist(testKunde);

        testKonto = new Konto(1, "g", LocalDate.of(2022,5,4), 5000.0 , 1000, "h");
        testKonto.setKunde(testKunde);
        List<UeberweisungsVorlage> erwarteteList = classUnderTest.getUeberweisungsVorlagenDesKontos(testKonto.getKtoid());
        assertTrue(erwarteteList.isEmpty());
    }

    /** @Test: insertUeberweisungsVorlage_00()
       WENN die Methode insertUeberweisungsVorlage mit einer TestUeberweisungsVorlage aufgerufen wird,
       UND die ID der TestUeberweisungsVorlage gleich null ist,
       DANN sollte sie TRUE zurückliefern,
       UND die TestUeberweisungsVorlage sollte im Persistence Context existieren.
   */

    @Test
    public void insertUeberweisungsVorlage_00(){
        testKunde = new Kunde(null, "Herr", "Soufian", "El", LocalDate.of(2022,5,4), "M", "l");
        em.persist(testKunde);

        testKonto = new Konto(null, "g", LocalDate.of(2022,5,4), 5000.0 , 1000, "h");
        em.persist(testKonto);
        testKonto.setKunde(testKunde);

        testUeberweisungsVorlage  = new UeberweisungsVorlage(null,LocalDate.now(),"Verwendugszweck0",5);
        testUeberweisungsVorlage.setKunde(testKunde);
        testUeberweisungsVorlage.setVonkonto(testKonto);

        assertTrue(classUnderTest.insertUeberweisungsVorlage(testUeberweisungsVorlage));

        //TestUeberweisungsVorlage sollte im Persistence Context existieren.
        assertTrue(em.contains(testUeberweisungsVorlage));
    }

    /** @Test: insertUeberweisungsVorlage_01()
       WENN die Methode insertUeberweisungsVorlage mit einer TestUeberweisungsVorlage aufgerufen wird,
       UND die ID der TestUeberweisungsVorlage ungleich null ist,
       DANN sollte sie FALSE zurückliefern,
       UND der Persistence Context wurde nicht verändert.
   */

    @Test
    public void insertUeberweisungsVorlage_01(){
        testKunde = new Kunde(null, "Herr", "Soufian", "El", LocalDate.of(2022,5,4), "M", "l");
        em.persist(testKunde);

        testKonto = new Konto(null, "g", LocalDate.of(2022,5,4), 5000.0 , 1000, "h");
        em.persist(testKonto);
        testKonto.setKunde(testKunde);


        testUeberweisungsVorlage  = new UeberweisungsVorlage(null,LocalDate.now(),"Verwendugszweck0",5);
        testUeberweisungsVorlage.setUvid(1);
        testUeberweisungsVorlage.setKunde(testKunde);
        testUeberweisungsVorlage.setVonkonto(testKonto);

        assertFalse(classUnderTest.insertUeberweisungsVorlage(testUeberweisungsVorlage));
        assertFalse(em.contains(testUeberweisungsVorlage));

    }

    /** @Test: deleteUeberweisungsVorlage_00()
       WENN eine TestUeberweisungsVorlage in der DB existiert,
       UND die Methode deleteUeberweisungsVorlage mit der ID der TestUeberweisungsVorlage aufgerufen wird,
       DANN sollte sie TRUE zurückliefern,
       UND die TestUeberweisungsVorlage sollte nicht mehr im Persistence Context existieren.
   */

    @Test
    public void deleteUeberweisungsVorlage_00(){
        testKunde = new Kunde(null, "Herr", "Soufian", "El", LocalDate.of(2022,5,4), "M", "l");

        testKonto = new Konto(null, "g", LocalDate.of(2022,5,4), 5000.0 , 1000, "h");
        testKonto.setKunde(testKunde);

        testUeberweisungsVorlage  = new UeberweisungsVorlage(null,LocalDate.now(),"Verwendugszweck0",5);
        testUeberweisungsVorlage.setKunde(testKunde);
        testUeberweisungsVorlage.setVonkonto(testKonto);

        em.persist(testKunde);
        em.persist(testKonto);
        em.persist(testUeberweisungsVorlage);

        assertTrue(classUnderTest.deleteUeberweisungsVorlage(testUeberweisungsVorlage.getUvid()));
        assertFalse(em.contains(testUeberweisungsVorlage));
    }

    /** @Test: deleteUeberweisungsVorlage_01()
       WENN eine TestUeberweisungsVorlage nicht in der DB existiert,
       UND die Methode deleteUeberweisungsVorlage mit der ID der TestUeberweisungsVorlage aufgerufen wird,
       DANN sollte sie FALSE zurückliefern.
   */

    @Test
    public void deleteUeberweisungsVorlage_01(){
        testKunde = new Kunde(null, "Herr", "Soufian", "El", LocalDate.of(2022,5,4), "M", "l");
        testKonto = new Konto(null, "g", LocalDate.of(2022,5,4), 5000.0 , 1000, "h");
        testKonto.setKunde(testKunde);

        em.persist(testKunde);
        em.persist(testKonto);
        testUeberweisungsVorlage  = new UeberweisungsVorlage(1,LocalDate.now(),"Verwendugszweck0",5);
        testUeberweisungsVorlage.setKunde(testKunde);
        testUeberweisungsVorlage.setVonkonto(testKonto);

        assertFalse(classUnderTest.deleteUeberweisungsVorlage(testUeberweisungsVorlage.getUvid()));
    }
 /**@Test: editUeberweisungsVorlage_00()
    WENN eine TestUeberweisungsVorlage in der DB existiert,
    UND die Methode editUeberweisungsVorlage mit einer veränderten
    TestUeberweisungsVorlage (aber gleicher ID) aufgerufen wird,
    DANN sollte sie TRUE zurückliefern,
    UND die TestUeberweisungsVorlage sollte im Persistence Context verändert sein.*/
 @Test
 public void editUeberweisungsVorlage_00(){
     Konto testVonKonto = new Konto(null, "g", LocalDate.now(), 450.00, 1.00, "s");
     Kunde testKunde = new Kunde(null, "Herr", "Samig", "Omar", LocalDate.now(), "m", "l");
     em.persist(testKunde);
     em.persist(testVonKonto);

     UeberweisungsVorlage testUeberweisungsVorlage = new UeberweisungsVorlage(null, LocalDate.now(), "Miete", 222.00);
     testUeberweisungsVorlage.setVonkonto(testVonKonto);
     testUeberweisungsVorlage.setKunde(testKunde);

     em.persist(testUeberweisungsVorlage);
     int uvid = testUeberweisungsVorlage.getUvid();

     testUeberweisungsVorlage.setBetrag(192.00); // Betrag ändern

     // Methode wird ausgeführt und getestet
     assertTrue(classUnderTest.editUeberweisungsVorlage(testUeberweisungsVorlage));

     UeberweisungsVorlage geaenderteUeberweisungsVorlage = em.find(UeberweisungsVorlage.class, uvid);

     assertEquals(192.00, geaenderteUeberweisungsVorlage.getBetrag(), 0.001);
 }

    /**@Test: editUeberweisungsVorlage_01()
    WENN eine TestUeberweisungsVorlage nicht in der DB existiert,
    UND die Methode editUeberweisungsVorlage mit der TestUeberweisungsVorlage
    aufgerufen wird,
    DANN sollte sie FALSE zurückliefern,
    UND die TestUeberweisungsVorlage sollte nicht im Persistence Context existieren.*/
    @Test
    public void editUeberweisungsVorlage_01() {

        Konto testVonKonto = new Konto(null, "TestKonto", LocalDate.now(), 455.00, 1.00, "st");
        Kunde testKunde = new Kunde(null, "Frau", "Musterfrau", "Anna", LocalDate.now(), "w", "l");
        UeberweisungsVorlage testUeberweisungsVorlage = new UeberweisungsVorlage(null, LocalDate.now(), "Versicherung", 300.00);
        testUeberweisungsVorlage.setVonkonto(testVonKonto);
        testUeberweisungsVorlage.setKunde(testKunde);

        testUeberweisungsVorlage.setUvid(9999); // Diese ID existiert nicht in der DB

        assertFalse(classUnderTest.editUeberweisungsVorlage(testUeberweisungsVorlage));

        UeberweisungsVorlage nichtExistierendeUeberweisungsVorlage = em.find(UeberweisungsVorlage.class, testUeberweisungsVorlage.getUvid());
        // AssertNull prüft, ob tatsächlich kein Objekt in der Datenbank vorhanden ist
        assertNull(nichtExistierendeUeberweisungsVorlage);
    }
    /**@Test: getUeberweisungenZuKonto_00()
    WENN ein Testkonto bereits in der Datenbank existiert,
    UND x (x>0) Überweisungen in der DB existieren, die über das Attribut zukonto auf das
    Testkonto verweisen,
    UND die Methode getUeberweisungenZuKonto mit der ID des TestKontos aufgerufen wird,
    DANN sollte sie die Liste mit den x Überweisungen zurückliefern.
     */
    @Test
    public void getUeberweisungenZuKonto_00(){

        Konto testVonKonto = new Konto(null, "g", LocalDate.now(), 200.00, 2.00, "s");
        Kunde testKunde = new Kunde(null, "Herr", "Mustermann", "Max", LocalDate.now(), "m", "l");
        Konto testKonto = new Konto(null, "g", LocalDate.now(), 400.00, 1.00, "s");

        em.persist(testVonKonto);
        em.persist(testKunde);
        em.persist(testKonto);

        Ueberweisung testUeberweisung = new Ueberweisung(null, 122.00, LocalDate.now(), "wt");
        testUeberweisung.setZukonto(testKonto);
        testUeberweisung.setKunde(testKunde);
        testUeberweisung.setVonkonto(testVonKonto);

        Ueberweisung testUeberweisung1 = new Ueberweisung(null, 33.00, LocalDate.now(), "us");
        testUeberweisung1.setZukonto(testKonto);
        testUeberweisung1.setKunde(testKunde);
        testUeberweisung1.setVonkonto(testVonKonto);
        //testUeberweisungen in DB einfuegen
        em.persist(testUeberweisung);
        em.persist(testUeberweisung1);
        List<Ueberweisung> result = classUnderTest.getUeberweisungenZuKonto((testKonto.getKtoid()));
        assertEquals(2, result.size());
        assertTrue(result.contains(testUeberweisung));
        assertTrue(result.contains(testUeberweisung1));

    }

    /**@Test: getUeberweisungenZuKonto_01()
    WENN ein Testkonto bereits in der Datenbank existiert,
    UND keine Überweisungen in der DB existieren, die über das Attribut zukonto auf das
    Testkonto verweisen,
    UND die Methode getUeberweisungenZuKonto mit der ID des TestKontos aufgerufen wird,
    DANN sollte sie eine leere Liste zurückliefern.*/
    @Test
    public void getUeberweisungenZuKonto_01(){
        Konto testVonKonto = new Konto(null, "g", LocalDate.now(), 200.00, 2.00, "s");
        Kunde testKunde = new Kunde(null, "Herr", "Mustermann", "Max", LocalDate.now(), "m", "l");
        Konto testKonto = new Konto(null, "g", LocalDate.now(), 400.00, 1.00, "s");
        Konto testZuKonto = new Konto(null, "g", LocalDate.now(), 600.00, 3.00, "s");
        em.persist(testVonKonto);
        em.persist(testKunde);
        em.persist(testZuKonto);
        em.persist(testKonto);

        Ueberweisung testUeberweisung = new Ueberweisung(null, 122.00, LocalDate.now(), "wt");
        testUeberweisung.setZukonto(testZuKonto);
        testUeberweisung.setKunde(testKunde);
        testUeberweisung.setVonkonto(testVonKonto);
        Ueberweisung testUeberweisung1 = new Ueberweisung(null, 33.00, LocalDate.now(), "us");
        testUeberweisung1.setZukonto(testZuKonto);
        testUeberweisung1.setKunde(testKunde);
        testUeberweisung1.setVonkonto(testVonKonto);
        em.persist(testUeberweisung);
        em.persist(testUeberweisung1);

        if(testKonto.getKtoid() == null){
            testKonto.setKtoid(0);
        }

        if(testKonto.getUeberweisungList1() == null){
            List<Ueberweisung> newUeberweisungList1 = new ArrayList<>();
            testKonto.setUeberweisungList1(newUeberweisungList1);
        }
        //Methode ausfuehren und testen
        assertTrue(classUnderTest.getUeberweisungenZuKonto(testKonto.getKtoid()).isEmpty());

    }
    /**@Test: getUeberweisungenZuKonto_02()
    WENN ein Testkonto nicht in der Datenbank existiert,
    UND die Methode getUeberweisungenZuKonto mit der ID des TestKontos aufgerufen wird,
    DANN sollte sie eine leere Liste zurückliefern.*/
    @Test
    public void getUeberweisungenZuKonto_02(){

        Konto testVonKonto = new Konto(null, "g", LocalDate.now(), 200.00, 2.00, "s");
        Kunde testKunde = new Kunde(null, "Herr", "Mustermann", "Max", LocalDate.now(), "m", "l");
        Konto testKonto = new Konto(null, "g", LocalDate.now(), 400.00, 1.00, "s");
        Konto testZuKonto = new Konto(null, "g", LocalDate.now(), 600.00, 3.00, "s");

        em.persist(testVonKonto);
        em.persist(testKunde);
        em.persist(testZuKonto);

        Ueberweisung testUeberweisung = new Ueberweisung(null, 122.00, LocalDate.now(), "wt");
        testUeberweisung.setZukonto(testZuKonto);
        testUeberweisung.setKunde(testKunde);
        testUeberweisung.setVonkonto(testVonKonto);

        Ueberweisung testUeberweisung1 = new Ueberweisung(null, 33.00, LocalDate.now(), "us");
        testUeberweisung1.setZukonto(testZuKonto);
        testUeberweisung1.setKunde(testKunde);
        testUeberweisung1.setVonkonto(testVonKonto);

        if(testKonto.getKtoid() == null){
            testKonto.setKtoid(0);
        }
        assertTrue(classUnderTest.getUeberweisungenZuKonto(testKonto.getKtoid()).isEmpty());

    }
    /** @Test: getUeberweisungenVonKonto_00()
    WENN ein Testkonto bereits in der Datenbank existiert,
    UND x (x>0) Überweisungen in der DB existieren, die über das Attribut vonkonto auf das
    Testkonto verweisen,
    UND die Methode getUeberweisungenVonKonto mit der ID des TestKontos aufgerufen wird,
    DANN sollte sie die Liste mit den x Überweisungen zurückliefern.*/
    @Test
    public void getUeberweisungenVonKonto_00(){

        Konto testZuKonto = new Konto(null, "g", LocalDate.now(), 200.00, 2.00, "s");
        Kunde testKunde = new Kunde(null, "Herr", "Mustermann", "Max", LocalDate.now(), "m", "l");
        Konto testKonto = new Konto(null, "g", LocalDate.now(), 400.00, 1.00, "s");
        em.persist(testZuKonto);
        em.persist(testKunde);
        em.persist(testKonto);
        Ueberweisung testUeberweisung = new Ueberweisung(null, 122.00, LocalDate.now(), "wt");
        testUeberweisung.setZukonto(testZuKonto);
        testUeberweisung.setKunde(testKunde);
        testUeberweisung.setVonkonto(testKonto);
        Ueberweisung testUeberweisung1 = new Ueberweisung(null, 33.00, LocalDate.now(), "us");
        testUeberweisung1.setZukonto(testZuKonto);
        testUeberweisung1.setKunde(testKunde);
        testUeberweisung1.setVonkonto(testKonto);

        em.persist(testUeberweisung);
        em.persist(testUeberweisung1);

        List<Ueberweisung> result = classUnderTest.getUeberweisungenVonKonto(testKonto.getKtoid());
        assertEquals(2, result.size());
        assertTrue(result.contains(testUeberweisung));
        assertTrue(result.contains(testUeberweisung1));

    }

    /**@Test: getUeberweisungenVonKonto_01()
    WENN ein Testkonto bereits in der Datenbank existiert,
    UND keine Überweisungen in der DB existieren, die über das Attribut vonkonto auf das
    Testkonto verweisen,
    UND die Methode getUeberweisungenVonKonto mit der ID des TestKontos aufgerufen wird,
    DANN sollte sie eine leere Liste zurückliefern.*/
    @Test
    public void getUeberweisungenVonKonto_01(){
        Konto testVonKonto = new Konto(null, "g", LocalDate.now(), 200.00, 2.00, "s");
        Konto testZuKonto = new Konto(null, "g", LocalDate.now(), 200.00, 2.00, "s");
        Kunde testKunde = new Kunde(null, "Herr", "Mustermann", "Max", LocalDate.now(), "m", "l");
        Konto testKonto = new Konto(null, "g", LocalDate.now(), 400.00, 1.00, "s");

        em.persist(testZuKonto);
        em.persist(testVonKonto);
        em.persist(testKunde);
        em.persist(testKonto);

        Ueberweisung testUeberweisung = new Ueberweisung(null, 122.00, LocalDate.now(), "wt");
        testUeberweisung.setZukonto(testZuKonto);
        testUeberweisung.setKunde(testKunde);
        testUeberweisung.setVonkonto(testVonKonto);

        Ueberweisung testUeberweisung1 = new Ueberweisung(null, 33.00, LocalDate.now(), "us");
        testUeberweisung1.setZukonto(testZuKonto);
        testUeberweisung1.setKunde(testKunde);
        testUeberweisung1.setVonkonto(testVonKonto);
        //testUeberweisungen in DB einfuegen
        em.persist(testUeberweisung);
        em.persist(testUeberweisung1);
        //ID von testKonto ueberpruefen
        if(testKonto.getKtoid() == null){
            testKonto.setKtoid(0);
        }
        //Erzeugen der vonKontoListe vom testKonto falls noch keine vorhanden ist
        if(testKonto.getUeberweisungList() == null){
            List<Ueberweisung> newUeberweisungList = new ArrayList<>();
            testKonto.setUeberweisungList(newUeberweisungList);
        }
        //Methode ausfuehren und testen
        assertTrue(classUnderTest.getUeberweisungenVonKonto(testKonto.getKtoid()).isEmpty());

    }

    /**@Test: getUeberweisungenVonKonto_02()
    WENN ein Testkonto nicht in der Datenbank existiert,
    UND die Methode getUeberweisungenVonKonto mit der ID des TestKontos aufgerufen wird,
    DANN sollte sie eine leere Liste zurückliefern.
     */
    @Test
    public void getUeberweisungenVonKonto_02(){


        Konto testVonKonto = new Konto(null, "g", LocalDate.now(), 200.00, 2.00, "s");
        Konto testZuKonto = new Konto(null, "g", LocalDate.now(), 200.00, 2.00, "s");
        Kunde testKunde = new Kunde(null, "Herr", "Mustermann", "Max", LocalDate.now(), "m", "l");
        Konto testKonto = new Konto(null, "g", LocalDate.now(), 400.00, 1.00, "s");

        em.persist(testVonKonto);
        em.persist(testZuKonto);
        em.persist(testKunde);
        Ueberweisung testUeberweisung = new Ueberweisung(null, 122.00, LocalDate.now(), "wt");
        testUeberweisung.setZukonto(testZuKonto);
        testUeberweisung.setKunde(testKunde);
        testUeberweisung.setVonkonto(testVonKonto);
        Ueberweisung testUeberweisung1 = new Ueberweisung(null, 33.00, LocalDate.now(), "us");
        testUeberweisung1.setZukonto(testZuKonto);
        testUeberweisung1.setKunde(testKunde);
        testUeberweisung1.setVonkonto(testVonKonto);

        em.persist(testUeberweisung);
        em.persist(testUeberweisung1);

        if(testKonto.getKtoid() == null){
            testKonto.setKtoid(0);
        }

        assertTrue(classUnderTest.getUeberweisungenVonKonto(testKonto.getKtoid()).isEmpty());

    }
    /**@Test: getLoeschbareUeberweisungen_00()
    WENN x (x>0) Überweisungen mit Status "ueberwiesen" in der DB existieren,
    UND y (y>0) Überweisungen mit Status "storniert" in der DB existieren,
    UND z (z>0) Überweisungen mit Status "nicht ueberweisbar" in der DB existieren,
    UND die Methode getLoeschbareUeberweisungen aufgerufen wird,
    DANN sollte sie die Liste mit den x+y+z Überweisungen zurückliefern*/
    @Test
    public void getLoeschbareUeberweisungen_00(){


        Konto testZuKonto = new Konto(null, "g", LocalDate.now(), 200.00, 2.00, "s");
        Kunde testKunde = new Kunde(null, "Herr", "Mustermann", "Max", LocalDate.now(), "m", "l");
        Konto testVonKonto = new Konto(null, "g", LocalDate.now(), 400.00, 1.00, "s");


        em.persist(testZuKonto);
        em.persist(testKunde);
        em.persist(testVonKonto);

        Ueberweisung testUeberweisung = new Ueberweisung(null, 122.00, LocalDate.now(), "us");
        testUeberweisung.setZukonto(testZuKonto);
        testUeberweisung.setKunde(testKunde);
        testUeberweisung.setVonkonto(testVonKonto);
        testUeberweisung.setStatus("us");

        Ueberweisung testUeberweisung1 = new Ueberweisung(null, 33.00, LocalDate.now(), "st");
        testUeberweisung1.setZukonto(testZuKonto);
        testUeberweisung1.setKunde(testKunde);
        testUeberweisung1.setVonkonto(testVonKonto);
        testUeberweisung1.setStatus("st");
        Ueberweisung testUeberweisung2 = new Ueberweisung(null, 33.00, LocalDate.now(), "nu");
        testUeberweisung2.setZukonto(testZuKonto);
        testUeberweisung2.setKunde(testKunde);
        testUeberweisung2.setVonkonto(testVonKonto);
        testUeberweisung2.setStatus("nu");

        em.persist(testUeberweisung);
        em.persist(testUeberweisung1);
        em.persist(testUeberweisung2);


        assertEquals(3, classUnderTest.getLoeschbareUeberweisungen().size());


    }
    /**@Test: getLoeschbareUeberweisungen_01()
    WENN keine Überweisungen mit Status "ueberwiesen" in der DB existieren,
    UND keine Überweisungen mit Status "storniert" in der DB existieren,
    UND keine Überweisungen mit Status "nicht ueberweisbar" in der DB existieren,
    UND die Methode getLoeschbareUeberweisungen aufgerufen wird,
    DANN sollte sie eine leere Liste zurückliefern.
     */
    @Test
    public void getLoeschbareUeberweisungen_01(){

        Konto testZuKonto = new Konto(null, "g", LocalDate.now(), 200.00, 2.00, "s");
        Kunde testKunde = new Kunde(null, "Herr", "Mustermann", "Max", LocalDate.now(), "m", "l");
        Konto testVonKonto = new Konto(null, "g", LocalDate.now(), 400.00, 1.00, "s");

        em.persist(testZuKonto);
        em.persist(testKunde);
        em.persist(testVonKonto);

        Ueberweisung testUeberweisung = new Ueberweisung(null, 122.00, LocalDate.now(), "wt");
        testUeberweisung.setZukonto(testZuKonto);
        testUeberweisung.setKunde(testKunde);
        testUeberweisung.setVonkonto(testVonKonto);

        Ueberweisung testUeberweisung1 = new Ueberweisung(null, 33.00, LocalDate.now(), "ns");
        testUeberweisung1.setZukonto(testZuKonto);
        testUeberweisung1.setKunde(testKunde);
        testUeberweisung1.setVonkonto(testVonKonto);

        Ueberweisung testUeberweisung2 = new Ueberweisung(null, 33.00, LocalDate.now(), "ns");
        testUeberweisung2.setZukonto(testZuKonto);
        testUeberweisung2.setKunde(testKunde);
        testUeberweisung2.setVonkonto(testVonKonto);

        em.persist(testUeberweisung);
        em.persist(testUeberweisung1);
        em.persist(testUeberweisung2);
        assertTrue(classUnderTest.getLoeschbareUeberweisungen().isEmpty());

    }
    /** @Test: loescheUeberweisung_00()
    WENN eine TestUeberweisung bereits in der DB existiert,
    UND diese TestUeberweisung den Status "ueberwiesen" besitzt
    UND die Methode loescheUeberweisung mit der Id der TestUeberweisung aufgerufen
    wird,
    DANN sollte sie TRUE zurückliefern,
    UND die TestUeberweisung sollte nicht mehr im Persistence Context existieren.
     */
    @Test
    public void loescheUeberweisung_00(){

        Konto testZuKonto = new Konto(null, "g", LocalDate.now(), 200.00, 2.00, "s");
        Kunde testKunde = new Kunde(null, "Herr", "Mustermann", "Max", LocalDate.now(), "m", "l");
        Konto testVonKonto = new Konto(null, "g", LocalDate.now(), 400.00, 1.00, "s");

        em.persist(testZuKonto);
        em.persist(testKunde);
        em.persist(testVonKonto);

        Ueberweisung testUeberweisung = new Ueberweisung(null, 122.00, LocalDate.now(), "us");
        testUeberweisung.setZukonto(testZuKonto);
        testUeberweisung.setKunde(testKunde);
        testUeberweisung.setVonkonto(testVonKonto);

        em.persist(testUeberweisung);

        assertNotNull(testUeberweisung.getUbid());

        assertTrue(classUnderTest.loescheUeberweisung(testUeberweisung.getUbid()));

        Ueberweisung geloeschteUeberweisung = em.find(Ueberweisung.class, testUeberweisung.getUbid());
        assertNull(geloeschteUeberweisung);

    }
    /**@Test: loescheUeberweisung_01()
    WENN eine TestUeberweisung bereits in der DB existiert,
    UND diese TestUeberweisung den Status "storniert" besitzt
    UND die Methode loescheUeberweisung mit der Id der TestUeberweisung aufgerufen
    wird,
    DANN sollte sie TRUE zurückliefern,
    UND die TestUeberweisung sollte nicht mehr im Persistence Context existieren.*/
    @Test
    public void loescheUeberweisung_01(){

        Konto testZuKonto = new Konto(null, "g",  LocalDate.now(), 200.00, 2.00, "s");
        Kunde testKunde = new Kunde(null, "Herr", "Mustermann", "Max",  LocalDate.now(), "m", "l");
        Konto testVonKonto = new Konto(null, "g",  LocalDate.now(), 400.00, 1.00, "s");

        em.persist(testZuKonto);
        em.persist(testKunde);
        em.persist(testVonKonto);

        Ueberweisung testUeberweisung = new Ueberweisung(null, 122.00,  LocalDate.now(), "st");
        testUeberweisung.setZukonto(testZuKonto);
        testUeberweisung.setKunde(testKunde);
        testUeberweisung.setVonkonto(testVonKonto);

        em.persist(testUeberweisung);

        // ID von testUeberweisung überprüfen
        if(testUeberweisung.getUbid() == null){
            fail("Die ID der Testüberweisung sollte nach Persistierung nicht null sein.");
        }

        // Methode ausführen und testen
        assertTrue(classUnderTest.loescheUeberweisung(testUeberweisung.getUbid()));
        assertNull(em.find(Ueberweisung.class, testUeberweisung.getUbid()));

    }

    /**@Test: loescheUeberweisung_02()
    WENN eine TestUeberweisung bereits in der DB existiert,
    UND diese TestUeberweisung den Status "nicht ueberweisbar" besitzt
    UND die Methode loescheUeberweisung mit der Id der TestUeberweisung aufgerufen
    wird,
    DANN sollte sie TRUE zurückliefern,
    UND die TestUeberweisung sollte nicht mehr im Persistence Context existieren.
     */
    @Test
    public void loescheUeberweisung_02(){

        //Testdaten erstellen

        Konto testZuKonto = new Konto(null, "g", LocalDate.now(), 200.00, 2.00, "s");
        Kunde testKunde = new Kunde(null, "Herr", "Mustermann", "Max", LocalDate.now(), "m", "l");
        Konto testVonKonto = new Konto(null, "g", LocalDate.now(), 400.00, 1.00, "s");

        em.persist(testZuKonto);
        em.persist(testKunde);
        em.persist(testVonKonto);

        Ueberweisung testUeberweisung = new Ueberweisung(null, 122.00, LocalDate.now(), "nu");
        testUeberweisung.setZukonto(testZuKonto);
        testUeberweisung.setKunde(testKunde);
        testUeberweisung.setVonkonto(testVonKonto);

        em.persist(testUeberweisung);
        // Methode ausführen
        boolean loeschResultat = classUnderTest.loescheUeberweisung(testUeberweisung.getUbid());

        assertTrue(loeschResultat);
        assertFalse(em.contains(testUeberweisung)); // Diese Zeile prüft, ob testUeberweisung nicht mehr im Persistence Context ist

    }
    /** @Test: loescheUeberweisung_03()
    WENN eine TestUeberweisung bereits in der DB existiert,
    UND diese TestUeberweisung den Status "wartet" besitzt
    UND die Methode loescheUeberweisung mit der Id der TestUeberweisung aufgerufen
    wird,
    DANN sollte sie FALSE zurückliefern,
    UND die TestUeberweisung sollte noch in der DB existieren.*/
    @Test
    public void loescheUeberweisung_03(){


        Konto testZuKonto = new Konto(null, "g", LocalDate.now(), 200.00, 2.00, "s");
        Kunde testKunde = new Kunde(null, "Herr", "Mustermann", "Max", LocalDate.now(), "m", "l");
        Konto testVonKonto = new Konto(null, "g", LocalDate.now(), 400.00, 1.00, "s");

        em.persist(testZuKonto);
        em.persist(testKunde);
        em.persist(testVonKonto);

        Ueberweisung testUeberweisung = new Ueberweisung(null, 122.00, LocalDate.now(), "wt");
        testUeberweisung.setZukonto(testZuKonto);
        testUeberweisung.setKunde(testKunde);
        testUeberweisung.setVonkonto(testVonKonto);

        em.persist(testUeberweisung);

        assertFalse(classUnderTest.loescheUeberweisung(testUeberweisung.getUbid()));

        assertNotNull(em.find(Ueberweisung.class, testUeberweisung.getUbid()));

    }
    /**@Test: loescheUeberweisung_04()
    WENN eine TestUeberweisung nicht in der DB existiert,
    UND die Methode loescheUeberweisung mit der Id der TestUeberweisung aufgerufen
    wird,
    DANN sollte sie FALSE zurückliefern.*/
    @Test
    public void loescheUeberweisung_04(){


        Konto testZuKonto = new Konto(null, "g", LocalDate.now(), 200.00, 2.00, "s");
        Kunde testKunde = new Kunde(null, "Herr", "Mustermann", "Max", LocalDate.now(), "m", "l");
        Konto testVonKonto = new Konto(null, "g", LocalDate.now(), 400.00, 1.00, "s");

        em.persist(testZuKonto);
        em.persist(testKunde);
        em.persist(testVonKonto);

        Ueberweisung testUeberweisung = new Ueberweisung(null, 122.00, LocalDate.now(), "us");
        testUeberweisung.setZukonto(testZuKonto);
        testUeberweisung.setKunde(testKunde);
        testUeberweisung.setVonkonto(testVonKonto);

        em.persist(testUeberweisung);
        em.remove(testUeberweisung);

        assertFalse(classUnderTest.loescheUeberweisung(testUeberweisung.getUbid()));

    }
    /**@Test: getWartendeUeberweisungen_00()
    WENN x (x>0) Überweisungen mit Status "wartet" in der DB existieren,
    UND die Methode getWartendeUeberweisungen aufgerufen wird,
    DANN sollte sie die Liste mit den x Überweisungen zurückliefern.*/
    @Test
    public void getWartendeUeberweisungen_00(){
        // Testdaten vorbereiten
        Konto testZuKonto = new Konto(null, "g", LocalDate.now(), 200.00, 2.00, "s");
        Kunde testKunde = new Kunde(null, "Herr", "Mustermann", "Max", LocalDate.now(), "m", "l");
        Konto testVonKonto = new Konto(null, "g", LocalDate.now(), 400.00, 1.00, "s");
        List<Ueberweisung> testUeberweisungList;

        em.persist(testZuKonto);
        em.persist(testKunde);
        em.persist(testVonKonto);

        Ueberweisung testUeberweisung = new Ueberweisung(null, 122.00, LocalDate.now(), "wt");
        testUeberweisung.setZukonto(testZuKonto);
        testUeberweisung.setKunde(testKunde);
        testUeberweisung.setVonkonto(testVonKonto);
        Ueberweisung testUeberweisung1 = new Ueberweisung(null, 33.00, LocalDate.now(), "wt");
        testUeberweisung1.setZukonto(testZuKonto);
        testUeberweisung1.setKunde(testKunde);
        testUeberweisung1.setVonkonto(testVonKonto);
        Ueberweisung testUeberweisung2 = new Ueberweisung(null, 33.00, LocalDate.now(), "wt");
        testUeberweisung2.setZukonto(testZuKonto);
        testUeberweisung2.setKunde(testKunde);
        testUeberweisung2.setVonkonto(testVonKonto);

        em.persist(testUeberweisung);
        em.persist(testUeberweisung1);
        em.persist(testUeberweisung2);

        TypedQuery<Ueberweisung> query = em.createQuery("SELECT u FROM Ueberweisung u WHERE u.status = :status", Ueberweisung.class);
        String wartend = "wt";
        query.setParameter("status", wartend);
        testUeberweisungList = query.getResultList();


        List<Ueberweisung> resultUeberweisungList = classUnderTest.getWartendeUeberweisungen();

        // Prüfe, ob die Listen gleich lang sind.
        assertEquals(testUeberweisungList.size(), resultUeberweisungList.size());

        // Prüfe, ob alle Objekte in testUeberweisungList in resultUeberweisungList vorhanden sind.
        assertTrue(resultUeberweisungList.containsAll(testUeberweisungList));

        //  Prüfe, ob alle Objekte in resultUeberweisungList in testUeberweisungList vorhanden sind, um sicherzustellen, dass die Listen exakt dieselben Elemente enthalten.
        assertTrue(testUeberweisungList.containsAll(resultUeberweisungList));
    }


    /**@Test: getWartendeUeberweisungen_01()
    WENN keine Überweisungen mit Status ”wartet” in der DB existieren,
    UND die Methode getWartendeUeberweisungen aufgerufen wird,
    DANN sollte sie eine leere Liste zurückliefern.*/
    @Test
    public void getWartendeUeberweisungen_01(){
        Konto testZuKonto = new Konto(null, "g", LocalDate.now(), 200.00, 2.00, "s");
        Kunde testKunde = new Kunde(null, "Herr", "Mustermann", "Max", LocalDate.now(), "m", "l");
        Konto testVonKonto = new Konto(null, "g", LocalDate.now(), 400.00, 1.00, "s");
        em.persist(testZuKonto);
        em.persist(testKunde);
        em.persist(testVonKonto);

        Ueberweisung testUeberweisung = new Ueberweisung(null, 122.00, LocalDate.now(), "st");
        testUeberweisung.setZukonto(testZuKonto);
        testUeberweisung.setKunde(testKunde);
        testUeberweisung.setVonkonto(testVonKonto);

        Ueberweisung testUeberweisung1 = new Ueberweisung(null, 33.00, LocalDate.now(), "nu");
        testUeberweisung1.setZukonto(testZuKonto);
        testUeberweisung1.setKunde(testKunde);
        testUeberweisung1.setVonkonto(testVonKonto);

        Ueberweisung testUeberweisung2 = new Ueberweisung(null, 33.00, LocalDate.now(), "us");
        testUeberweisung2.setZukonto(testZuKonto);
        testUeberweisung2.setKunde(testKunde);
        testUeberweisung2.setVonkonto(testVonKonto);

        em.persist(testUeberweisung);
        em.persist(testUeberweisung1);
        em.persist(testUeberweisung2);


        assertTrue(classUnderTest.getWartendeUeberweisungen().isEmpty());

    }
}
