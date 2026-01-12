# Coding Standards

## Language Requirements

### Comments
- All code comments MUST be written in English
- Use simple, easy-to-understand English
- Avoid Chinese or other non-English characters in comments
- Keep comments concise and clear

### Examples

Good:
```java
// Primary color - dark blue
public static final Color PRIMARY = new Color(25, 55, 109);

// Check if session has available slots
public boolean hasAvailableSlots() {
    return registered < capacity;
}

/**
 * Saves a user to the database.
 * @param user The user to save
 * @return The saved user with generated ID
 */
public User save(User user) { ... }
```

Bad:
```java
// 主色调 - 深蓝
public static final Color PRIMARY = new Color(25, 55, 109);

// 检查是否有空位
public boolean hasAvailableSlots() { ... }
```

## Code Documentation

### Javadoc
- Write Javadoc in English for all public classes and methods
- Include @param, @return, and @throws tags where applicable
- Keep descriptions brief but informative

### Inline Comments
- Use inline comments sparingly
- Explain "why" not "what" when possible
- Remove commented-out code before committing
