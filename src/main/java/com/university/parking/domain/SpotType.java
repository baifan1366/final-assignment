package com.university.parking.domain;

/**
 * Enumeration representing the types of parking spots available.
 * Requirements: 1.4
 * Future-proof: Easy to add new spot types (e.g., ELECTRIC for EV charging)
 */
public enum SpotType {
    COMPACT,
    REGULAR,
    HANDICAPPED,
    RESERVED,
    ELECTRIC  // Future-proof: Electric vehicle charging spot at RM8/hour
}
