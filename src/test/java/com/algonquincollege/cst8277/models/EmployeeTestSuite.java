/**********************************************************************egg*m******a******n********************
 * File: EmployeeTestSuite.java
 * Course materials (19W) CST 8277
 * @author (original) Mike Norman
 *
 *
 */
package com.algonquincollege.cst8277.models;

import static com.algonquincollege.cst8277.models.TestSuiteConstants.attachListAppender;
import static com.algonquincollege.cst8277.models.TestSuiteConstants.buildEntityManagerFactory;
import static com.algonquincollege.cst8277.models.TestSuiteConstants.detachListAppender;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
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
 * This class is use to do JUnit test on Model(Employee.java)
 * 
 * date (modified) 2019 03 27
 * 
 * @author mwnorman, Bo Zhu,  040-684-747
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EmployeeTestSuite implements TestSuiteConstants {
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
     * String SELECT_EMPLOYEE_1, used to run query
     */
    private static final String SELECT_EMPLOYEE_1 =
            "SELECT ID, FIRSTNAME, LASTNAME, SALARY, VERSION, ADDRESS_ID FROM EMPLOYEE WHERE (ID = ?)";
    
    /**
     * find null when there is no employee at start
     */
    @Test
    public void _01_test_no_Employees_at_start() {
        EntityManager em = emf.createEntityManager();

        ListAppender<ILoggingEvent> listAppender = attachListAppender(eclipselinkSqlLogger, ECLIPSELINK_LOGGING_SQL);
        //find Employee with PK=1
        Employee emp1 = em.find(Employee.class, Integer.valueOf(1));
        detachListAppender(eclipselinkSqlLogger, listAppender);

        assertNull(emp1);
        List<ILoggingEvent> loggingEvents = listAppender.list;
        assertEquals(1, loggingEvents.size());
        assertThat(loggingEvents.get(0).getMessage(),
                startsWith(SELECT_EMPLOYEE_1));

        em.close();
    }

    /**
     * create several employees, JPA- CriteriaBuilder 
     */
    @Test
    //@Ignore 
    public void _02_test_create_employee() {
        //Create EntityManager     
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        //create employee with firstName, lastName, salary
        Employee employee1 = new Employee("Linda", "Lee", 60000.00 );
        Employee employee2 = new Employee("Bo", "Zhu", 80000.00);
        Employee employee3 = new Employee("Elena", "An",100000.00);
        //trandaction begin, persist, commit to DB
       
        em.persist(employee1);
        em.persist(employee2);
        em.persist(employee3);
        em.getTransaction().commit();
        //use CriteriaBuilder Query
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);
        Root<Employee> rootEmpQuery = cq.from(Employee.class);
        cq.where(cb.and(cb.equal(rootEmpQuery.get(Employee_.firstName), "Linda")));
        List<Employee> employeeFromDB = em.createQuery(cq).getResultList(); 
        assertEquals(employeeFromDB.get(0).getFirstName(), "Linda");
        em.close();

    }

    /**
     * read employee by employee id, JPA- CriteriaBuilder 
     */
    @Test
    public void _03_test_read_employee_by_id() {
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);
        Root<Employee> rootEmpQuery = cq.from(Employee.class);
        cq.where(cb.and(cb.equal(rootEmpQuery.get(Employee_.id), 1)));
        List<Employee> employeeFromDB = em.createQuery(cq).getResultList(); 
        assertEquals(1, employeeFromDB.get(0).getId());
        em.close(); 
    }

    /**
     * read employees' highest salary, Build JPQL Query from EntityManager
     */
    @Test
    public void _04_test_find_employee_by_highest_salary() {
        EntityManager em = emf.createEntityManager();     
        Query query = em.createQuery("Select MAX(e.salary) FROM Employee e");
        double max = (double)query.getSingleResult();
        assertEquals(100000.00, max,0);
        em.close();
    }
  
    /**
     * read employees' lowest salary, Build JPQL Query from EntityManager
     */
    @Test
    public void _05_test_find_employee_by_lowest_salary() {
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("Select MIN(e.salary) FROM Employee e");
        double min = (double)query.getSingleResult();
        assertEquals(60000.00, min, 0);
        em.close();     

    }
    
    /**
     * read average emplpoyee's salary, Build JPQL Query from EntityManager
     */
    @Test
    public void _06_test_employee_avg_salary() {
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("Select AVG(e.salary) FROM Employee e");
        double avg = (double)query.getSingleResult();
        assertEquals(80000.00, avg, 0);
        em.close();     

    }
 
    /**
     * read sum employees' salary, Build JPQL Query from EntityManager
     */
    @Test
    public void _07_test_employee_sum_salary() {
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("Select SUM(e.salary) FROM Employee e");
        double sum = (double)query.getSingleResult();
        assertEquals(240000.00, sum, 0);
        em.close();     

    }
    /**
     * count employees, Build JPQL Query from EntityManager
     */
    // queries - count employees
    @Test
    public void _08_test_count_employees() {
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("Select COUNT(e) FROM Employee e");
        long count = (long)query.getSingleResult();
        assertEquals(3, count);
        em.close();     
    }
  

    /**
     * count employees whose salary greater than 40000, Build JPQL Query from EntityManager
     */
    @Test
    public void _09_test_count_employees_salary_greater_than_amount() {
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("Select COUNT(e) FROM Employee e WHERE e.salary > 40000");
        long count = (long)query.getSingleResult();
        assertEquals(3, count);
        em.close();     
    }
    
    /**
     * count employees whose salary less than 80000, Build JPQL Query from EntityManager
     */
    @Test
    public void _10_test_employee_salary_greater_than_amount() {
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("Select e FROM Employee e WHERE e.salary < 80000");
        List<Employee> list= query.getResultList();
        //for loop to get employee list
        for(Employee e : list) {
            System.out.println("Employee's salary less than 80000 :" + e.getFirstName());
        }
        assertTrue(list.size() == 1);

    }
   
    /**
     * find employee's fist name start with spcific letter, Build JPQL Query from EntityManager
     */
    @Test
    public void _11_test_employee_name_start_with_letter() {
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("Select e FROM Employee e WHERE e.firstName LIKE 'B%'");
        List<Employee> list= query.getResultList();
        //for loop to get employee list
        for(Employee e : list) {
            System.out.println("Employee's  first name start with letter B :" + e.getFirstName());
            assertTrue(e.getFirstName() == "Bo");
        }
       
    }
    /**
     * count employees whose salary euqal to 80000, Build JPQL Query from EntityManager
     */
    @Test
    public void _12_test_count_employees_whose_salary_equalTo_amount() {
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("Select count(e) FROM Employee e WHERE e.salary = 80000");
        long count = (long)query.getSingleResult();
        System.out.println("COUNT EMPLOYEE WHOSE SALARY EQUAL TO 80000 :" + count);
        assertTrue(count == 1 );

    }
    
    /**
     * update employee's name
     */
    @Test
    public void _13_test_update_employee_name() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Employee employee1 = em.find(Employee.class,1);
        System.out.println("test 13: " + employee1.getFirstName());
        //assertEquals("Linda", employee1.getFirstName());
        employee1.setFirstName("Christ");
        em.persist(employee1);
        em.getTransaction().commit();
        assertEquals("Christ", employee1.getFirstName());
        em.close();   

    }

    /**
     * delete employee 
     */
    @Test
    public void _14_test_delete_employee() {

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Employee employee3 = em.find(Employee.class, 3);
        em.remove(employee3);
        em.getTransaction().commit();
        Employee employee3_retrieved = em.find(Employee.class, 3);
        //null for employee3 after delete
        assertNull(employee3_retrieved);
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