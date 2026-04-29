package com.sit707.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TodoServiceTest {

    @Autowired
    private TodoService todoService;

    @Test
    void testAddTodo() {
        int id = todoService.add("Buy groceries");
        assertNotNull(todoService.getById(id));
    }

    @Test
    void testGetAll() {
        todoService.add("Task 1");
        assertFalse(todoService.getAll().isEmpty());
    }

    @Test
    void testUpdateTodo() {
        int id = todoService.add("Old task");
        boolean result = todoService.update(id, "New task");
        assertTrue(result);
        assertEquals("New task", todoService.getById(id));
    }

    @Test
    void testDeleteTodo() {
        int id = todoService.add("Delete me");
        boolean result = todoService.delete(id);
        assertTrue(result);
        assertNull(todoService.getById(id));
    }

    @Test
    void testEmptyTaskThrows() {
        assertThrows(IllegalArgumentException.class, () -> todoService.add(""));
    }

    @Test
    void testDeleteNonExistent() {
        assertFalse(todoService.delete(99999));
    }
}
