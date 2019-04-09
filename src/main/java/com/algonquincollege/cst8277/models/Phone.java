/********************************************************************egg***m******a**************n************
 * File: Phone.java
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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * The Phone class demonstrates:
 * <ul>
 * <li>Generated Id
 * <li>Version locking
 * <li>ManyToOne mapping
 * </ul>
 * Address class 
 * date (modified) 2019 03 30
 * 
 * @author mwnorman, Bo Zhu,  040-684-747
 * 
 */
@Entity
@Table(name="PHONE")
@NamedQueries({
    @NamedQuery(
     name = "findAllPhones",
     query= "SELECT p FROM Phone p"),
    @NamedQuery(
            name = "updatePhoneNumber",
            query= "UPDATE Phone p SET p.phoneNumber = :phoneNumber WHERE p.id = :id"),
    @NamedQuery(
            name = "countPhonesHasSameType",
            query= "SELECT count(p) FROM Phone p WHERE p.type = :type"),
    @NamedQuery(
            name = "findAllPhonesOrderedByType",
            query = "SELECT p FROM Phone p ORDER BY p.type")

})
public class Phone extends ModelBase implements Serializable {
    /** explicit set serialVersionUID */
    private static final long serialVersionUID = 1L;

    /**
     * String areaCode, phone's area code
     * @author Bo Zhu
     */
    protected String areaCode;
    
    /**
     * String phoneNumber, phone's phone number
     * @author Bo Zhu
     */
    protected String phoneNumber;
    /**
     * String type, phone' type
     * @author Bo Zhu
     */
    protected String type;
    /**
     * Employee owner, phone's owner
     * @author Bo Zhu
     */
    private Employee owner;
    //private int emp_owner_id;
    
    /**
     * default constructor, JPA requires each @Entity class have a default constructor
     */
    public Phone() {
        super();
    }
    /**
     * constructor, overload method
     * @param areaCode, phone's areaCode
     * @param phoneNumber, phone's phoneNumber
     * @param type, phone's type
     * @author Bo Zhu
     */
    public Phone(String areaCode, String phoneNumber, String type) {
        this.areaCode = areaCode;
        this.phoneNumber = phoneNumber;
        this.type = type;
    }
    /**
     * constructor, overload method
     * @param areaCode, phone's areaCode
     * @param phoneNumber, phone's phoneNumber
     * @param type, phone's type
     * @param employee
     * @author Bo Zhu
     */
    public Phone(String areaCode, String phoneNumber, String type, Employee owner) {
        this.areaCode = areaCode;
        this.phoneNumber = phoneNumber;
        this.type = type;
        this.owner = owner;
    }

    /**getter for Phone's owner
     * @return owner 
     */
    @ManyToOne
    @JoinColumn(name = "OWNING_EMP_ID")
    public Employee getOwner() {
        return owner;
    }
    
    /**setter for Phone's owner
     * @param owner, employee
     */
    public void setOwner(Employee owner) {
        this.owner = owner;
    }
  
    /**getter for Phone's areaCode
     * @return areaCode 
     */
    @Column(name = "AREACODE")
    public String getAreaCode() {
        return areaCode;
    }
    
    /**setter for Phone's areaCode
     * @param areaCode, the value of area code
     */
    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }
    
    /**getter for Phone's phoneNumber
     * @return phoneNumber
     */
    @Column(name = "PHONENUMBER")
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    /**setter for Phone's phoneNumber
     * @param phoneNumber, the value of phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    /**getter for Phone's type
     * @return type 
     */
    @Column(name = "TYPE")
    public String getType() {
        return type;
    }
    
    /**setter for Phone's type
     * @param type, the value of phone's type
     */
    public void setType(String type) {
        this.type = type;
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
        if (!(obj instanceof Phone)) {
            return false;
        }
        Phone other = (Phone)obj;
        if (id != other.id) {
            return false;
        }
        return true;
    }
}