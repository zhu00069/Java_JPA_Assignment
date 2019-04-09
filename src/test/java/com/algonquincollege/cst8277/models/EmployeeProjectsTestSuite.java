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
 * This class is use to do JUnit test on Model Employee, Project, M:N relationship
 * date (modified) 2019 03 27
 * @author mwnorman, Bo Zhu,  040-684-747
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EmployeeProjectsTestSuite implements TestSuiteConstants {
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

    private static final String SELECT_EMPLOYEE_PROJECT_1 =
            "SELECT EMP_ID, PROJ_ID FROM EMP_PROJ WHERE (ID = ?)";
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
     * test at the begining (detached relationship) 
     * create, get emplty project from employee and empty employee from project
     */
    @Test
    public void _01_test_emp_proj_without_association() {
        //Create EntityManager     
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Employee employee1 = new Employee("Linda", "Lee", 60000.00);
        Project project1 = new Project("Project1", "This is Project1");
        em.persist(employee1);
        em.persist(project1);
        em.getTransaction().commit();
        
        assertTrue(employee1.getProjects().isEmpty());
        assertTrue(project1.getEmployees().isEmpty());
        em.close();

    }
    
    /**
     * count employees who work on project2 by project's id
     */
    @Test
    public void _02_test_count_employees_work_on_project_byId_attached() {
        //Create EntityManager     
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Employee employee2 = new Employee("Tom", "Mahamod", 60000.00);
        Employee employee3 = new Employee("Bo", "Zhu", 80000.00);

        Project project2 = new Project("Project2", "This is Project2");
//        //add employee to project
//        project2.getEmployees().add(employee2);
//        project2.getEmployees().add(employee3);
//        //add project to employee
//        employee2.getProjects().add(project2);
//        employee3.getProjects().add(project2);
        employee2.addProject(project2);
        employee3.addProject(project2);

        em.persist(employee2);
        em.persist(employee3);
        em.persist(project2);

        em.getTransaction().commit();

        Query query = em.createQuery("select count(e) FROM Project p JOIN p.employees e WHERE p.id = :id");
        query.setParameter("id", project2.getId());
        long count = (long)query.getSingleResult();
        assertEquals(count, 2);
        em.close();
    }

    /**
     * count employees who work on project3 by project's name
     */
    @Test
    public void _03_test_count_employees_work_on_project_byName_attached() {
        //Create EntityManager     
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Employee employee4 = new Employee("Lina", "Rio", 80000.00);
        Employee employee5 = new Employee("Reaza", "Tink", 80000.00);

        Project project3 = new Project("Project3", "This is Project3");
//        //add employee to project
//        project3.getEmployees().add(employee4);
//        project3.getEmployees().add(employee5);
//        //add project to employee
//        employee4.getProjects().add(project3);
//        employee5.getProjects().add(project3);
        employee4.addProject(project3);
        employee5.addProject(project3);
        
        Employee employee2 = em.find(Employee.class, 2);
        employee2.addProject(project3);
        
        em.persist(employee4);
        em.persist(employee5);
        em.persist(employee2);
        em.persist(project3);

        em.getTransaction().commit();

        Query query = em.createQuery("select count(e) FROM Project p JOIN p.employees e WHERE p.name = :projectName");
        query.setParameter("projectName", project3.getName());
        long count = (long)query.getSingleResult();
        assertEquals(count, 3);
        em.close();
    }
    
    /**
     * count employees 
     */
    @Test
    public void _04_test_count_employees() {
        
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("Select count(e) FROM Employee e");
        long count = (long)query.getSingleResult();
        assertTrue(count == 5);
        em.close();   
      
    }
    /**
     * count projects 
     */
    @Test
    public void _05_test_count_projects() {
        
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("Select count(p) FROM Project p");
        long count = (long)query.getSingleResult();
        assertTrue(count == 3);
        em.close();   
      
    }
    
    /**
     * find project information by project's id
     */
    @Test
    public void _06_test_find_project_by_id() {
        
        EntityManager em = emf.createEntityManager();
        Project project1 = em.find(Project.class, 2);
        Query query = em.createQuery("Select p FROM Project p WHERE p.id = :projectId");
        query.setParameter("projectId", project1.getId());
        List<Project> list = query.getResultList();
        for (Project project : list) {
            System.out.println("Project list : " + project);
        }
        assertEquals(list.get(0).getName(), "Project2");
        assertEquals(list.get(0).getDescription(), "This is Project2");
        em.close();   
      
    }

    
    /**
     * update project which asscocaited with employee(attached relationship)
     */
    @Test
    public void _07_test_update_employee_project_attached() {
        
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Project project3 = em.find(Project.class, 3);
        Employee employee4 = em.find(Employee.class, 4);
        Employee employee5 = em.find(Employee.class, 5);
        
        Query query = em.createQuery("Update Project p set p.name = :name WHERE p.id = :id");
        query.setParameter("name", "Update Project3");
        query.setParameter("id", project3.getId());
        query.executeUpdate(); 
        //em persist, refresh project3 after update
        em.persist(project3);
        em.refresh(project3);
        em.getTransaction().commit();
        System.out.println("Update project3's name from Project3 to : " + project3.getName());
        
        //check employee's project name whether updating 
        assertEquals(employee4.getProjects().get(0).getName(), "Update Project3");
        assertEquals(employee5.getProjects().get(0).getName(), "Update Project3");
        em.close();
    }
    
    /**
     * delete 1 project from owner
     */
    @Test
    public void _08_test_delete_project_from_owner() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        
        Employee employee2 = em.find(Employee.class, 2);
        System.out.println("Employee2 has " + employee2.getProjects().size()+ " projects before delete"); 
        //employee1 has 2 projects
        assertEquals(2, employee2.getProjects().size());
        Project project2 = employee2.getProjects().get(0);
        
        employee2.removeProject(project2);
        em.persist(employee2);
        em.remove(project2);
        em.getTransaction().commit();
        
        Employee employee2_retrieved = em.find(Employee.class, 2);
        assertEquals(1, employee2_retrieved.getProjects().size());//employee2 has 1 project after delete 1 project
        assertEquals("Project2", project2.getName());
        em.close();
    }
    
    /**
     * delete employee from project
     */
    @Test
    public void _09_test_delete_employee_from_project() {

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        TypedQuery<Project> query = em.createNamedQuery("findAllProjects", Project.class);
        List<Project> project_list = query.getResultList();
        //project list size is 2
        assertEquals(2,  project_list.size()); 
        Employee employee2 = em.find(Employee.class, 2);
        //employee3 should have 1 project
        assertEquals(1, employee2.getProjects().size()); 
        
        em.remove(employee2);
        em.getTransaction().commit();
        Employee employee2_retrieved = em.find(Employee.class, 2);
        //employee3 is deleted, so no employee3
        assertNull(employee2_retrieved); 
        List<Project> project_list_after_remove = query.getResultList();
        for(Project p :project_list_after_remove) {
            System.out.println("left project is : " + p.getName());
        }
        //project left project1, project3
        //assertEquals(1, project_list_after_remove.size()); 
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
;