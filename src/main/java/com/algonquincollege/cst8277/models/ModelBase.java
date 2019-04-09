/********************************************************************egg***m******a**************n************
 * File: ModelBase.java
 * Course materials (19W) CST 8277
 * @author Mike Norman
 *
 * @date 2019 03
 */
package com.algonquincollege.cst8277.models;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

/**
 * Abstract class that is base of (class) hierarchy for all com.algonquincollege.cst8277.models @Entity classes
 * @author mwnorman
 */
@MappedSuperclass
public abstract class ModelBase {
    
    /**
     * int id, Primary Key, increment generate automatically
     */
    protected int id;
    /**
     * int version, version number
     */
    protected int version;
    
    /**
     * getter for Id
     * @return id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }
    /**
     * setter for Id
     * @param id, the value of primary key
     */
    public void setId(int id) {
        this.id = id;
    }
    /**
     * getter for version
     * @return version
     */
    @Version
    public int getVersion() {
        return version;
    }
    
    /**
     * setter for version
     * @param version, the value of version number
     */
    public void setVersion(int version) {
        this.version = version;
    }
}