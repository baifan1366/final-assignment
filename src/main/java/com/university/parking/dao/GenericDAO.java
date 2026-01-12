package com.university.parking.dao;

import java.util.List;

/**
 * Generic Data Access Object interface defining standard CRUD operations.
 * All entity-specific DAOs should extend this interface.
 * Requirements: 9.2
 * 
 * @param <T> the entity type
 * @param <ID> the type of the entity's identifier
 */
public interface GenericDAO<T, ID> {
    
    /**
     * Finds an entity by its unique identifier.
     * 
     * @param id the unique identifier
     * @return the entity if found, null otherwise
     */
    T findById(ID id);
    
    /**
     * Retrieves all entities of this type.
     * 
     * @return a list of all entities, empty list if none found
     */
    List<T> findAll();
    
    /**
     * Saves a new entity to the database.
     * 
     * @param entity the entity to save
     */
    void save(T entity);
    
    /**
     * Updates an existing entity in the database.
     * 
     * @param entity the entity to update
     */
    void update(T entity);
    
    /**
     * Deletes an entity by its unique identifier.
     * 
     * @param id the unique identifier of the entity to delete
     */
    void delete(ID id);
}
