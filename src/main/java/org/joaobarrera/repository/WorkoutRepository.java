package org.joaobarrera.repository;

import org.joaobarrera.entity.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
 * Joao Barrera
 * CEN 3024 - Software Development 1
 * November 11, 2025
 * WorkoutRepository.java
 */

/**
 * Extends JpaRepository to provide standard CRUD operations for the Workout object.
 */
@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Integer> {
    /**
     * Finds all workouts whose names contain the given search term, ignoring case.
     * <p>
     * Useful for search functionality in the Workout Logger website.
     *
     * @param searchTerm the string to search for within workout names
     * @return a list of matching Workout objects
     */
    List<Workout> findByNameContainingIgnoreCase(String searchTerm);
}