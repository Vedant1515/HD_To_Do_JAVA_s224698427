package com.sit707.app;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class TodoService {

    private final Map<Integer, String> todos = new HashMap<>();
    private int counter = 1;

    public Map<Integer, String> getAll() {
        return Collections.unmodifiableMap(todos);
    }

    public int add(String task) {
        if (task == null || task.trim().isEmpty())
            throw new IllegalArgumentException("Task cannot be empty");
        todos.put(counter, task.trim());
        return counter++;
    }

    public boolean update(int id, String task) {
        if (!todos.containsKey(id)) return false;
        if (task == null || task.trim().isEmpty())
            throw new IllegalArgumentException("Task cannot be empty");
        todos.put(id, task.trim());
        return true;
    }

    public boolean delete(int id) {
        return todos.remove(id) != null;
    }

    public String getById(int id) {
        return todos.get(id);
    }

    public int size() {
        return todos.size();
    }
}
