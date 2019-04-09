/********************************************************************egg***m******a**************n************
 * File: Address.java
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
 *
 */
package com.algonquincollege.cst8277.models;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
/**
 * Simple Address class - it uses a generated Id.
 * 
 * date (modified) 2019 03 30
 * 
 * @author mwnorman, Bo Zhu,  040-684-747
 * 
 */
@Entity
@Table(name = "ADDRESS")
@NamedQueries({
    @NamedQuery(
     name = "findAllAddresses",
     query= "SELECT a FROM Address a"),
    @NamedQuery(
            name = "findAllEmployees",
            query= "SELECT e FROM Employee e"),
    @NamedQuery(
            name = "countEmployeesWhoSalaryGreaterThan",
            query= "SELECT count(e) FROM Employee e WHERE e.salary > :salary"),
    @NamedQuery(
            name = "countEmployeesWhoSalaryLessThan",
            query= "SELECT count(e) FROM Employee e WHERE e.salary < :salary"),
    @NamedQuery(
            name = "countEmployeesWhoSalaryEqualTo",
            query= "SELECT count(e) FROM Employee e WHERE e.salary = :salary")
})
public class Address extends ModelBase implements Serializable {
    /** explicit set serialVersionUID */
    private static final long serialVersionUID = 1L;

    /**
     * String city, address's city
     * @author Bo Zhu
     */    
    protected String city;
    /**
     * String country, address's country
     * @author Bo Zhu
     */
    protected String country;
    /**
     * String postal, address's postal
     * @author Bo Zhu
     */
    protected String postal;
    /**
     * String state, address' state
     * @author Bo Zhu
     */
    protected String state;
    /**
     * String street, address's street
     * @author Bo Zhu
     */
    protected String street;
    /**
     * Employee employee, address's owner
     * @author Bo Zhu
     */
    private Employee employee;
    
    /**
     * defalut constructor, JPA requires each @Entity class have a default constructor
     */
    public Address() {
        super();
    }
    
    /**getter for employee
     * @return employee 
     */ 
    @OneToOne(fetch = FetchType.LAZY,
            cascade =  CascadeType.ALL,
            mappedBy = "address")
    public Employee getEmployee() {
        return employee;
    }
    
    /**
     * setter for employee
     * @param employee, employee
     */
    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
    
    /**getter for Address's city
     * @return city 
     */ 
    @Column(name = "CITY")
    public String getCity() {
        return city;
    }
    
    /**setter for Address's city
     * @param city, city
     */
    public void setCity(String city) {
        this.city = city;
    }
    
    /**getter for Address's country
     * @return country 
     */
    @Column(name = "COUNTRY")
    public String getCountry() {
        return country;
    }
    
    /**setter for Address's country
     * @param country, country
     */
    public void setCountry(String country) {
        this.country = country;
    }
    
    /**getter for Address's postal
     * @return postal 
     */
    @Column(name = "POSTAL")
    public String getPostal() {
        return postal;
    }
    
    /**setter for Address's postal
     * @param postal, postal
     */
    public void setPostal(String postal) {
        this.postal = postal;
    }
    
    /**getter for Address's state
     * @return state 
     */
    @Column(name = "STATE")
    public String getState() {
        return state;
    }
    
    /**setter for Address's state
     * @param state, state
     */
    public void setState(String state) {
        this.state = state;
    }
    
    /**getter for Address's street
     * @return street 
     */
    @Column(name = "STREET")
    public String getStreet() {
        return street;
    }
    /**setter for Address's street
     * @param street, street
     */
    public void setStreet(String street) {
        this.street = street;
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
        if (!(obj instanceof Address)) {
            return false;
        }
        Address other = (Address)obj;
        if (id != other.id) {
            return false;
        }
        return true;
    }

}