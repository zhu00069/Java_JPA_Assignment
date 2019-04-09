/**********************************************************************egg*m******a******n********************
 * File: EmployeePhonesSuite.java
 * Course materials (19W) CST 8277
 * @author (original) Mike Norman
 *
 *
 */
package com.algonquincollege.cst8277.models;

import static com.algonquincollege.cst8277.models.TestSuiteConstants.attachListAppender;
import static com.algonquincollege.cst8277.models.TestSuiteConstants.buildEntityManagerFactory;
import static com.algonquincollege.cst8277.models.TestSuiteConstants.detachListAppender;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.invoke.MethodHandles;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.h2.tools.Server;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

/**
 * This class is use to do JUnit test on Model Employee, Phone, 1:M relationship
 * date (modified) 2019 03 27
 * @author mwnorman, Bo Zhu,  040-684-747
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EmployeePhonesTestSuite implements TestSuiteConstants {
    /**
     * Class<?> _thisClaz, a class object taht represent class and interface in a running java application
     */
    private static final Class<?> _thisClaz = MethodHandles.lookup().lookupClass();
    /**
     * Logger logger, a logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(_thisClaz);
    /**
     * ch.qos.logback.classic.Logger eclipselinkSqlLogger, a logger object for this class
     */
    private static final ch.qos.logback.classic.Logger eclipselinkSqlLogger =
            (ch.qos.logback.classic.Logger)LoggerFactory.getLogger(ECLIPSELINK_LOGGING_SQL);

 
    /**
     * EntityManagerFactory emf, EMF instance, use to create EntityManager for connecting to same database
     */
    public static EntityManagerFactory emf;
    /**
     * Server server, tcp server
     */
    public static Server server;
    /**
     * this method used to set up for testing 
     */
    @BeforeClass
    public static void oneTimeSetUp() {
        try {
            logger.debug("oneTimeSetUp");
            // create in-process H2 server so we can 'see' into database
            // use "jdbc:h2:tcp://localhost:9092/mem:assignment3-testing" in Db Perspective
            // (connection in .dbeaver-data-sources.xml so should be immediately useable
            server = Server.createTcpServer().start();
            emf = buildEntityManagerFactory(_thisClaz.getSimpleName());
        }
        catch (Exception e) {
            logger.error("something went wrong building EntityManagerFactory", e);
        }
    }

    /**
     * create phone without owner at start
     */
    @Test
    public void _01_test_phone_not_belong_to_employee_at_start() {
        //Create EntityManager     
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        //create phone woth areaCode, phoneNumber, type, version
        Phone phone1 = new Phone("D8H", "6131101111", "BELL");
        em.persist(phone1);
        em.getTransaction().commit();
        assertNull(phone1.getOwner());
        em.close();

    }

    /**
     * create phones with owner
     */
    @Test
    public void _02_test_create_two_phones_belong_to_employee1() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Employee employee1 = new Employee("Linda", "Lee", 60000.00);

        Phone phone2 = new Phone("D8H", "6131102222", "BELL", employee1);
        Phone phone3 = new Phone("S7L", "6131103333", "FIDO", employee1);
        
        employee1.addPhone(phone2);
        employee1.addPhone(phone3);
        em.persist(employee1);
        em.persist(phone2);
        em.persist(phone3);
        em.getTransaction().commit();

        assertNotNull(phone2.getOwner());
        assertNotNull(phone3.getOwner());
        Phone phone2_retrieved = em.find(Phone.class, 2);
        Employee emp1 = em.find(Employee.class, 1);
        assertEquals(2, emp1.getPhones().size());
        assertEquals("Linda", phone2_retrieved.getOwner().getFirstName());
        System.out.println("phone2's owner is : " + phone2.getOwner().getFirstName());
        System.out.println("phone3's owner is : " + phone3.getOwner().getFirstName());

        em.close();

    }

    /**
     * read, test phone list is not null, use JPA- NamedQuery
     */
    @Test
    public void _03_test_read_phones_list_not_null() {
        EntityManager em = emf.createEntityManager();
        List<Phone> phones = (List<Phone>)em.createNamedQuery("findAllPhones").getResultList();
        for(Phone p : phones) {
            System.out.println("Phone :" + p.getPhoneNumber());
        }
        assertNotNull(phones);

    }

    /**
     * count phones, use JPA- NamedQuery
     */
    @Test
    public void _04_test_count_phones() {
        EntityManager em = emf.createEntityManager();
        List<Phone> phones = (List<Phone>)em.createNamedQuery("findAllPhones").getResultList();
        assertTrue(phones.size() == 3);  

    }

    /**
     * count phones has same type (BELL), use JPA- NamedQuery
     */
    @Test
    public void _05_test_count_phones_has_same_type() {
        EntityManager em = emf.createEntityManager();
        long count = (long)em.createNamedQuery("countPhonesHasSameType")
                .setParameter("type", "BELL")
                .getSingleResult();
        assertTrue(count == 2);  

    }

    /**
     * update phone's phoneNumber without owner (detached relationship), use NamedQuery 
     */
    @Test
    public void _06_test_update_phone_number_detached() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.createNamedQuery("updatePhoneNumber")
        .setParameter("phoneNumber", "6131101112")
        .setParameter("id", 1)
        .executeUpdate();
        
        em.getTransaction().commit();
        Phone phone1 = em.find(Phone.class, 1);
        
        System.out.println("Phone1's phoneNumber (before update) :" + phone1.getPhoneNumber());
        System.out.println("update Phone1's phoneNumber from 6131101111  to :" + phone1.getPhoneNumber());
        assertEquals("6131101112", phone1.getPhoneNumber());

    }

    /**
     * update employee's phoneNumber with owner (attached relationship), use NamedQuery 
     */
    @Test
    public void _07_test_update_phone_with_owner_attached() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Employee employee1 = em.find(Employee.class, 1);
        
        Phone phone2 = em.find(Phone.class, 2);
        assertEquals("6131102222", employee1.getPhones().get(0).getPhoneNumber());
        
        Query query = em.createQuery("Update Phone p set p.phoneNumber = :phoneNumber WHERE p.id = :id");
        query.setParameter("phoneNumber", "6139990000");
        query.setParameter("id", 2);
        query.executeUpdate();
        
        // em persist phone2, employee1
        em.persist(phone2);
        em.persist(employee1);
        // em refresh phone2, employee1
        em.refresh(phone2);
        em.refresh(employee1);
        
        em.getTransaction().commit();
        assertEquals(2, employee1.getPhones().size());

        System.out.println("update phone2 id: " + phone2.getId());
        System.out.println("update phone2, from 6131102222 to "+ employee1.getPhones().get(0).getPhoneNumber());
        assertEquals("6139990000", employee1.getPhones().get(0).getPhoneNumber());
        
        em.close();
    }


    /**
     * get phones order by type, use NamedQuery 
     */
    @Test
    public void _08_test_phones_order_by_type() {

        EntityManager em = emf.createEntityManager();
        List<Phone> phones = (List<Phone>)em.createNamedQuery("findAllPhonesOrderedByType").getResultList();
        for(Phone p : phones) {
            System.out.println("phone version order:" + p.getVersion());
        }

        //test phones type
        Phone phone1 = em.find(Phone.class, 1);
        Phone phone2 = em.find(Phone.class, 2);
        Phone phone3 = em.find(Phone.class, 3);
        System.out.println("display Phone1's type :" + phone1.getType());
        System.out.println("display Phone2's type :" + phone2.getType());
        System.out.println("display Phone3's type :" + phone3.getType());

        assertEquals("BELL", phones.get(0).getType());
        assertEquals("BELL", phones.get(1).getType());
        assertEquals("FIDO", phones.get(2).getType());

    }
    
    /**
     * count phones of employee1, it should be 2. CriteriaBuilder
     */
    @Test
    public void _09_test_count_phones_of_owner() {
        EntityManager em = emf.createEntityManager();
        Employee employee1 = em.find(Employee.class, 1);
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Phone> cq = cb.createQuery(Phone.class);
        Root<Phone> rootEmpQuery = cq.from(Phone.class);
        cq.where(cb.and(cb.equal(rootEmpQuery.get(Phone_.owner), employee1)));
        //get count of phones belong to employee1
        List<Phone> list = (List<Phone>)em.createQuery(cq).getResultList(); 
        for(Phone p : list) {
            System.out.println("Emolyee1 has phone's number id: " + p.getId() + ", phone number :" + p.getPhoneNumber());
        }
        assertEquals(2, list.size());
        em.close();
        
    }

    /**
     * remove phone1 without owner
     */
    @Test
    public void _10_test_remove_phone1_without_owner() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Phone phone1 = em.find(Phone.class, 1);
        em.remove(phone1);
        em.getTransaction().commit();
        System.out.println("test 10, phone1's number is :" + phone1.getPhoneNumber());
        phone1 = em.find(Phone.class, 1);
        assertNull(phone1);     
        em.close();

    }

    /**
     * delete phone with owner, employee1 has phone2, phone3, now delete phone2, left 1
     */
    @Test
    public void _11_test_delete_phone_from_owner() {
        
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Employee employee1 = em.find(Employee.class, 1);
        assertEquals(2, employee1.getPhones().size());
        Phone phone2 = employee1.getPhones().get(0);
        
        //delete phone2 from employee1
        employee1.removePhone(phone2);
        em.persist(employee1);
        //delete phone2 from em 
        em.remove(phone2);
        
        em.getTransaction().commit();
        Employee employee1_retrieved = em.find(Employee.class, 1);
        assertNotNull(employee1_retrieved);
        assertEquals(1, employee1_retrieved.getPhones().size());
        em.close();
        
    }
    
    /**
     * delete delete owner from phone
     */
    @Test
    public void _12_test_delete_owner_from_phone() {

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        TypedQuery<Phone> query = em.createNamedQuery("findAllPhones", Phone.class);
        List<Phone> phone_list = query.getResultList();
        assertEquals(1,  phone_list.size()); // phone list size is 1
        
        Employee employee1 = em.find(Employee.class, 1);
        assertEquals(1, employee1.getPhones().size()); //employee1 should have 1 phone
        
        em.remove(employee1);
        em.getTransaction().commit();
        Employee employee1_retrieved = em.find(Employee.class, 1);
        assertNull(employee1_retrieved); //employee1 is deleted, so no employee1
        List<Phone> phone_list_after_remove = query.getResultList();
        assertEquals(0, phone_list_after_remove.size()); // phone list size decrease , left 0
        em.close();

    }

    /**
     * this method used to tear down for testing 
     */
    @AfterClass
    public static void oneTimeTearDown() {
        logger.debug("oneTimeTearDown");
        if (emf != null) {
            emf.close();
        }
        if (server != null) {
            server.stop();
        }
    }

}
