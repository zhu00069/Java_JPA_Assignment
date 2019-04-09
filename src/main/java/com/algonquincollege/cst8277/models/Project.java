/********************************************************************egg***m******a**************n************
 * File: Project.java
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
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * The Project class demonstrates:
 * <ul>
 * <li>Generated Id
 * <li>Version locking
 * <li>ManyToMany mapping
 * </ul>
 * date (modified) 2019 03 30
 * 
 * @author mwnorman, Bo Zhu,  040-684-747
 * 
 */
@Entity
@Table(name="PROJECT")
@NamedQueries({
    @NamedQuery(
     name = "findAllProjects",
     query= "SELECT p FROM Project p")
})
public class Project extends ModelBase implements Serializable {
    /** explicit set serialVersionUID */
    private static final long serialVersionUID = 1L;
    /**
     * String name, name of project
     * @author Bo Zhu
     */
    protected String name;

    /**
     * String description, description of project
     * @author Bo Zhu
     */
    protected String description;
   
    /**
     * List employees, list of employees
     * @author Bo Zhu
     */
    private List<Employee> employees = new ArrayList<>();

    /**
     * default constructor, JPA requires each @Entity class have a default constructor
     */
    public Project() {
        super();
    }
    /**
     * constructor, overload
     * @param name, name of project
     * @param description, description of project
     * @author Bo Zhu
     */
    public Project(String name, String description) {
        this.name = name;
        this.description = description;
        
    }
    
    /**getter for Project's Employee List
     * @return employees 
     */
    @ManyToMany(mappedBy = "projects",cascade = CascadeType.ALL)
    public List<Employee> getEmployees() {
        return employees;
    }

    /**setter for Project's Employee List
     * @param employees, list of employees 
     */
    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    /**getter for Project's description
     * @return description 
     */
    @Column(name = "DESCRIPTION")
    public String getDescription() {
        return description;
    }
    
    /**setter for Project's description
     * @param description, description of project
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**getter for Project's name
     * @return name 
     */
    @Column(name = "NAME")
    public String getName() {
        return name;
    }
    
    /**setter for Project's name
     * @param name, name of project
     */
    public void setName(String name) {
        this.name = name;
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
        if (!(obj instanceof Project)) {
            return false;
        }
        Project other = (Project)obj;
        if (id != other.id) {
            return false;
        }
        return true;
    }

}