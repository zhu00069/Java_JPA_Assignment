/********************************************************************egg***m******a**************n************
 * File: Employee.java
 * Course materials (19W) CST 8277
 * @author Mike Norman
 * (Modified) @date 2019 03
 *
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Original @authors dclarke, mbraeuer
 */
package com.algonquincollege.cst8277.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GenerationType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.OneToMany;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Version;
/**
 * The Employee class demonstrates several JPA features:
 * <ul>
 * <li>Generated Id
 * <li>Version locking
 * <li>OneToOne relationship
 * <li>OneToMany relationship
 * <li>ManyToMany relationship
 * </ul>
 * date (modified) 2019 03 30
 * 
 * @author mwnorman, Bo Zhu,  040-684-747
 * 
 */
@Entity
@Table(name="EMPLOYEE")
public class Employee extends ModelBase implements Serializable {
    /** explicit set serialVersionUID */
    private static final long serialVersionUID = 1L;

    /**
     * String firstName, first name pf employee
     * @author Bo Zhu
     */
   
    protected String firstName;
    /**
     * String lastName, last name of employee
     * @author Bo Zhu
     */
   
    protected String lastName;
    
    /**
     * double salary ,salary of employee
     * @author Bo Zhu
     */
    protected double salary;
    
    /**
     * Address address, employee's address 
     * @author Bo Zhu
     */
    private Address address;
    /**
     * List phone, list of employee's phones 
     * @author Bo Zhu
     */
    private List<Phone> phones = new ArrayList<>();
    /**
     * List project, list of employee's projects 
     * @author Bo Zhu
     */
    private List<Project> projects = new ArrayList<>();

    /**
     * default constructor, JPA requires each @Entity class have a default constructor
     */
    public Employee() {
        super();
    }
    /**
     * constructor, overload method
     * @param firstName, first name
     * @param lastName, last name
     * @param salary, salary
     * @author Bo Zhu
     */
    public Employee(String firstName, String lastName, double salary) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.salary = salary;
        
    }
    /**
     * constructor, overload method
     * @param firstName, first name
     * @param lastName, last name
     * @param salary, salary
     * @param address, address
     * @author Bo Zhu
     */
    public Employee(String firstName, String lastName, double salary, Address address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.salary = salary;
        this.address = address;
        
    }
    
    /**
     * getter for address
     * @return address
     */
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "ADDRESS_ID")
    public Address getAddress() {
        return address;
    }
    /**
     * setter for address
     * @param address, the value of address
     */
    public void setAddress(Address address) {
        this.address = address;
    } 
    
    /**
     * getter for phone list
     * @return phones
     */
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval=true)
    public List<Phone> getPhones() {
        return phones;
    }
    /**
     * setter for phone list
     * @param phones, phone list
     */
    public void setPhones(List<Phone> phones) {
        this.phones = phones;
    }
    
    /**
     * method for adding a phone
     * @param phone, add a phone
     * @return phone
     */
    public Phone addPhone(Phone phone) {
        getPhones().add(phone);
        return phone;
    }
    /**
     * method for removing a phone
     * @param phone, remove a phone
     * @return phone
     */
    public Phone removePhone(Phone phone) {
        getPhones().remove(phone);
        return phone;
    }
    
    /**
     * getter for projects
     * @return projects
     */
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "EMP_PROJ", 
               joinColumns = @JoinColumn(name = "EMP_ID",referencedColumnName="ID"), 
               inverseJoinColumns = @JoinColumn(name = "PROJ_ID",referencedColumnName="ID"))
    public List<Project> getProjects() {
        return projects;
    }
    
    /**
     * setter for projects
     * @param projects, project list
     */
    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }
    
    /**
     * method for adding a project
     * @param project, add a project
     * @return project
     */
    public Project addProject(Project project) {
        getProjects().add(project);
        return project;
    }
    /**
     * method for removing a project
     * @param project, removing a project
     * @return project
     */
    public Project removeProject(Project project) {
        getProjects().remove(project);
        return project;
    }
    
    /**getter for Employee's firstName
     * @return firstName 
     */
    @Column(name = "FIRSTNAME")
    public String getFirstName() {
        return firstName;
    }
    
    /**setter for Employee's firstName
     * @param firstName, the value of firstName
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    /**getter for Employee's lastName
     * @return lastName 
     */
    @Column(name = "LASTNAME")
    public String getLastName() {
        return lastName;
    }
    
    /**setter for Employee's lastName
     * @param lastName, the value of lastName
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    /**getter for Employee's salary
     * @return salary 
     */
    @Column(name = "SALARY")
    public double getSalary() {
        return salary;
    }
    
    /**setter for Employee's salary
     * @param salary, the value of salary
     */
    public void setSalary(double salary) {
        this.salary = salary;
    }
    
    // Strictly speaking, JPA does not require hashcode() and equals(),
    // but it is a good idea to have one that tests using the PK (@Id) field

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Employee)) {
            return false;
        }
        Employee other = (Employee)obj;
        if (id != other.id) {
            return false;
        }
        return true;
    }

}