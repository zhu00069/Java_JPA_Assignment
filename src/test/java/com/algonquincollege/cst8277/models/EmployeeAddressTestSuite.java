/**********************************************************************egg*m******a******n********************
 * File: EmployeeAddressTestSuite.java
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
import java.util.List;
import java.lang.invoke.MethodHandles;

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
 * This class is use to do JUnit test on Model Employee, Address, 1:1 relationship
 * date (modified) 2019 03 27
 * @author mwnorman, Bo Zhu,  040-684-747
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EmployeeAddressTestSuite implements TestSuiteConstants {
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
     * String SELECT_EMPLOYEE_ADDRESS_1, used to run query
     */
    private static final String SELECT_EMPLOYEE_ADDRESS_1 =
            "SELECT ID, FIRSTNAME, LASTNAME, SALARY, VERSION, ADDRESS_ID FROM EMPLOYEE WHERE (ADDRESS_ID =?)";

    
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
     * create employee with no address at start
     */
    @Test
    public void _01_test_create_employee_without_address_at_start() {
        //Create EntityManager     
        EntityManager em = emf.createEntityManager();
        //create employee with firstName, lastName, salary, version
        Employee employee0 = new Employee("Alice", "Yang", 60000.00);

        em.getTransaction().begin();
        em.persist(employee0);
        em.getTransaction().commit();
        Employee employee = em.find(Employee.class, 1);
        assertNull(employee.getAddress());
        em.close();

    }
    
    /**
     * create employee with an address, JPA- CriteriaBuilder 
     */
    @Test
    public void _02_test_create_employee_with_address() {
        //Create EntityManager     
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        //find address with pk=1,2,3
        Address address1 = em.find(Address.class, 1);
        Address address2 = em.find(Address.class, 2);
        Address address3 = em.find(Address.class, 3);
        //create employee with firstName, lastName, salary, address
        Employee employee1 = new Employee("Linda", "Lee", 60000.00, address1);
        Employee employee2 = new Employee("Tom", "Liki", 60000.00, address2);
        Employee employee3 = new Employee("Mandy", "Song", 80000.00, address3);
        
        System.out.println("Employee1's address is : " + employee1.getAddress());
        System.out.println("Employee1's firstname is : " + employee1.getFirstName());
        em.persist(employee1);
        em.persist(employee2);
        em.persist(employee3);
        em.getTransaction().commit();


        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);
        Root<Employee> rootEmpQuery = cq.from(Employee.class);
        cq.where(cb.and(cb.equal(rootEmpQuery.get(Employee_.address), address1)));
        List<Employee> empFromDB = em.createQuery(cq).getResultList(); 
        assertEquals(empFromDB.get(0).getAddress(), address1);
        em.close();

    }
   
    /**
     * read, count all addresses, JPA- NamedQuery
     */
    @Test
    public void _03_test_count_all_address() {
        //Create EntityManager   
        EntityManager em = emf.createEntityManager();
        //use NamedQuery to get all addresses
        List<Address> addresses = (List<Address>)em.createNamedQuery("findAllAddresses").getResultList();
        //addresses' size
        assertTrue(addresses.size() == 3);
        assertEquals("CA", addresses.get(0).getCountry());
        assertEquals("CA", addresses.get(1).getCountry());
        assertEquals("CA", addresses.get(2).getCountry());
        System.out.println("EMPLOYEE_ADDRESS_TEST, COUNT ADDRESSES SIZE =  " + addresses.size());
        em.close();

    }
    
    /**
     * read, count all employees, JPA- NamedQuery
     */
    @Test
    public void _04_test_count_all_employees() {
        //Create EntityManager   
        EntityManager em = emf.createEntityManager();
        List<Employee> employees = (List<Employee>)em.createNamedQuery("findAllEmployees").getResultList();
        assertEquals(employees.get(0).getFirstName(), "Alice");
        //employees' size
        assertTrue(employees.size() == 4);
        em.close();  
        System.out.println("EMPLOYEE_ADDRESS_TEST, COUNT EMPLOYEES SIZE =  " + employees.size());
    }
    
    /**
     * read, count employees whose salary greater than specific amount, used JPA NamedQuery
     */
    @Test
    public void _05_test_count_employee_salary_greater_than_amount() {
        EntityManager em = emf.createEntityManager();
        long count = (long)em.createNamedQuery("countEmployeesWhoSalaryGreaterThan")
                .setParameter("salary", 60000)
                .getSingleResult();
        //only one employee's salary greater than 60000
        assertTrue(count == 1);
        em.close();  
        System.out.println("COUNT EMPLOYEE WHOSE SALARY GREATER THAN 60000: " + count);
    }
    
    /**
     * read, count employees whose salary less than specific amount, used JPA NamedQuery
     */
    @Test
    public void _06_test_count_employee_salary_less_than_amount() {
        EntityManager em = emf.createEntityManager();
        long count = (long)em.createNamedQuery("countEmployeesWhoSalaryLessThan")
                .setParameter("salary", 60000)
                .getSingleResult();
        //none employee's salary less than 60000
        assertTrue(count == 0);
        em.close();  
        System.out.println("COUNT EMPLOYEE WHOSE SALARY LESS THAN 80000: " + count);
    }
    
    /**
     * read, count employees whose salary equalTo specific amount, used JPA NamedQuery
     */
    @Test
    public void _07_test_count_employee_salary_equalTo_amount() {
        EntityManager em = emf.createEntityManager();
        long count = (long)em.createNamedQuery("countEmployeesWhoSalaryEqualTo")
                .setParameter("salary", 60000)
                .getSingleResult();
        //3 employees's salary equal to 60000
        assertTrue(count == 3);
        em.close();  
        System.out.println("COUNT EMPLOYEE WHOSE SALARY EQYUAL TO 60000: " + count);
    }
    
    @Test
    public void _08_test_count_address_country_is_CA() {
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("Select a FROM Address a WHERE a.country LIKE 'CA%'");
        List<Address> list= query.getResultList();
        //for loop to get employee list
        for(Address a : list) {
            System.out.println("Address's state == :" + a.getCountry());
            assertEquals("CA", a.getCountry());
        }
        assertTrue(list.size() == 3); //count should be 3
        em.close();
    }
   
    /**
     * read emplyee by address(attached relationship), JPA- CriteriaBuilder 
     */
    @Test
    public void _09_test_find_employee_by_epecific_address_attached() {

        EntityManager em = emf.createEntityManager();
        Address address2 = em.find(Address.class, Integer.valueOf(2));
        //System.out.println("Address2 belong to :" + address2.getEmployee());

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);
        Root<Employee> rootEmpQuery = cq.from(Employee.class);
        cq.where(cb.and(cb.equal(rootEmpQuery.get(Employee_.address), address2))); //find employee by address2
        List<Employee> employeeFromDB = em.createQuery(cq).getResultList(); 
        System.out.println("EMPLOYEE_ADDRESS_TEST, COUNT EMPLOYEE WHO HAS ADDRESS2 = " + employeeFromDB.size());
        assertEquals("Tom", employeeFromDB.get(0).getFirstName());
        em.close();

    }
  
    /**
     * update employee's address (attached relationship)
     */
    @Test
    public void _10_test_update_employee_address() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Employee employee3 = em.find(Employee.class, 3);
        Address address3 = em.find(Address.class, 3);

        employee3.setAddress(address3);
        em.getTransaction().commit();
        assertEquals(address3, employee3.getAddress());
        System.out.println("UPDATE ADDRESS3 ON EMPLOYEE4, GET EMPLOYEE4'S ADDRESS ID = "+ employee3.getAddress().getId());
        em.close();

    }
    /**
     * update address' street, get employee's address uodated (attached relationship)
     */
    @Test
    public void _11_test_update_employee_address_street() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Employee employee3 = em.find(Employee.class, 3);
        Address address3 = em.find(Address.class, 3);
        //Address3 was 10 Elgin Street before update
        address3.setStreet("11 Kanata Ave");
        em.getTransaction().commit();
        assertEquals("11 Kanata Ave", employee3.getAddress().getStreet());
        System.out.println("UPDATE ADDRESS3's STREET, GET EMPLOYEE3 ADDRESS'STREET = "+ employee3.getAddress().getStreet());
        em.close();

    }
    
    /**
     * update address' state, get employee's address updated (attached relationship)
     */
    @Test
    public void _12_test_update_employee_address_state() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Employee employee3 = em.find(Employee.class, 3);
        Address address3 = em.find(Address.class, 3);
        address3.setState("BC"); //Address3's state was ON before update
        em.getTransaction().commit();
        assertEquals("BC", employee3.getAddress().getState());
        System.out.println("UPDATE ADDRESS3's STATE, GET EMPLOYEE3 ADDRESS'STATE= "+ employee3.getAddress().getState());
        em.close();

    }

    /**
     * update address's postal, get employee's address updated (attached relationship)
     */
    @Test
    public void _13_test_update_employee_address_postal() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Employee employee3 = em.find(Employee.class, 3);
        Address address3 = em.find(Address.class, 3);
        address3.setPostal("A1B C2D");; //set address2's postal B2P 5S2 to A1B C2D
        em.getTransaction().commit();
        assertEquals("A1B C2D", employee3.getAddress().getPostal()); //employee's address version should be A1B C2D
        System.out.println("UPDATE ADDRESS3's POSTAL FROM B2P 5S2 TO A1B C2D, NEW EMPLOYEE3 ADDRESS'VERSION = "+ employee3.getAddress().getPostal());
        em.close();

    }

    /**
     * delete employee without address (detached relationship)
     */
    @Test
    public void _14_test_delete_employee_without_address() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Employee employee0 = em.find(Employee.class, 1);
        em.remove(employee0);
        em.getTransaction().commit();
        Employee employee_retrieved = em.find(Employee.class, 1);
        assertNull(employee_retrieved); // should null after delete
        em.close();

    }

    /**
     * delete employee with address (attached relationship)
     */
    @Test
    public void _15_test_delete_employee_with_address() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Employee employee2 = em.find(Employee.class, 2);
        Address address1 = em.find(Address.class, 1);
        em.remove(employee2);
        em.getTransaction().commit();
        assertNull(address1.getEmployee());
        System.out.println("DELETE EMPLOYEE2 WHICH HAS ADDRESS1, IT IS NULL OWNER ON ADDRESS1 ID = "+ address1.getId());
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