package com.sit707.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
public class TodoController {

    @Autowired
    private TodoService todoService;

    @GetMapping(value = "/", produces = "text/html")
    public String home() {
        return """
<!DOCTYPE html>
<html>
<head>
    <title>SIT707 Todo App</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: Arial, sans-serif; background: #f5f5f5; padding: 30px; }
        .container { max-width: 600px; margin: 0 auto; background: white; border-radius: 10px; padding: 30px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        h1 { color: #4285f4; margin-bottom: 5px; }
        .subtitle { color: #666; font-size: 13px; margin-bottom: 25px; }
        .input-row { display: flex; gap: 10px; margin-bottom: 20px; }
        input[type=text] { flex: 1; padding: 10px; border: 1px solid #ddd; border-radius: 6px; font-size: 15px; }
        button { padding: 10px 18px; border: none; border-radius: 6px; cursor: pointer; font-size: 14px; font-weight: bold; }
        .btn-add { background: #4285f4; color: white; }
        .btn-add:hover { background: #357abd; }
        .btn-delete { background: #ea4335; color: white; padding: 6px 12px; font-size: 12px; }
        .btn-edit { background: #fbbc04; color: #333; padding: 6px 12px; font-size: 12px; }
        .btn-save { background: #34a853; color: white; padding: 6px 12px; font-size: 12px; }
        .todo-item { display: flex; align-items: center; gap: 10px; padding: 12px; border: 1px solid #eee; border-radius: 6px; margin-bottom: 8px; background: #fafafa; }
        .todo-id { background: #4285f4; color: white; border-radius: 50%; width: 26px; height: 26px; display: flex; align-items: center; justify-content: center; font-size: 12px; font-weight: bold; flex-shrink: 0; }
        .todo-text { flex: 1; font-size: 15px; }
        .todo-input { flex: 1; padding: 5px 8px; border: 1px solid #4285f4; border-radius: 4px; font-size: 14px; }
        .btn-row { display: flex; gap: 6px; }
        #status { margin-top: 15px; padding: 10px; border-radius: 6px; font-size: 14px; display: none; }
        .success { background: #e6f4ea; color: #137333; }
        .error { background: #fce8e6; color: #c5221f; }
        #empty { text-align: center; color: #999; padding: 20px; display: none; }
    </style>
</head>
<body>
<div class="container">
    <h1>SIT707 Todo App</h1>
    <p class="subtitle">Deployed via GCP Cloud Build CI/CD Pipeline (Java Spring Boot)</p>

    <div class="input-row">
        <input type="text" id="newTask" placeholder="Enter a new task..." onkeypress="if(event.key==='Enter') addTodo()" />
        <button class="btn-add" onclick="addTodo()">+ Add</button>
    </div>

    <div id="todo-list"></div>
    <div id="empty">No todos yet. Add one above!</div>
    <div id="status"></div>
</div>

<script>
    const API = '/todos';

    async function loadTodos() {
        const res = await fetch(API);
        const data = await res.json();
        const list = document.getElementById('todo-list');
        const empty = document.getElementById('empty');
        list.innerHTML = '';
        const entries = Object.entries(data);
        if (entries.length === 0) { empty.style.display = 'block'; return; }
        empty.style.display = 'none';
        entries.forEach(([id, task]) => {
            list.innerHTML += `
            <div class="todo-item" id="item-${id}">
                <div class="todo-id">${id}</div>
                <span class="todo-text" id="text-${id}">${task}</span>
                <input class="todo-input" id="input-${id}" value="${task}" style="display:none" />
                <div class="btn-row">
                    <button class="btn-edit" id="edit-btn-${id}" onclick="editTodo(${id})">Edit</button>
                    <button class="btn-save" id="save-btn-${id}" onclick="saveTodo(${id})" style="display:none">Save</button>
                    <button class="btn-delete" onclick="deleteTodo(${id})">Delete</button>
                </div>
            </div>`;
        });
    }

    async function addTodo() {
        const input = document.getElementById('newTask');
        const task = input.value.trim();
        if (!task) { showStatus('Please enter a task!', false); return; }
        const res = await fetch(API, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ task })
        });
        const data = await res.json();
        if (res.ok) { input.value = ''; showStatus('Todo added!', true); loadTodos(); }
        else showStatus(data.error, false);
    }

    function editTodo(id) {
        document.getElementById('text-' + id).style.display = 'none';
        document.getElementById('input-' + id).style.display = 'block';
        document.getElementById('edit-btn-' + id).style.display = 'none';
        document.getElementById('save-btn-' + id).style.display = 'block';
    }

    async function saveTodo(id) {
        const task = document.getElementById('input-' + id).value.trim();
        if (!task) { showStatus('Task cannot be empty!', false); return; }
        const res = await fetch(`${API}/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ task })
        });
        if (res.ok) { showStatus('Todo updated!', true); loadTodos(); }
        else showStatus('Update failed!', false);
    }

    async function deleteTodo(id) {
        const res = await fetch(`${API}/${id}`, { method: 'DELETE' });
        if (res.ok) { showStatus('Todo deleted!', true); loadTodos(); }
        else showStatus('Delete failed!', false);
    }

    function showStatus(msg, success) {
        const s = document.getElementById('status');
        s.textContent = msg;
        s.className = success ? 'success' : 'error';
        s.style.display = 'block';
        setTimeout(() => s.style.display = 'none', 3000);
    }

    loadTodos();
</script>
</body>
</html>
""";
    }

    @GetMapping("/todos")
    public Map<Integer, String> getAll() {
        return todoService.getAll();
    }

    @PostMapping("/todos")
    public ResponseEntity<?> add(@RequestBody Map<String, String> body) {
        try {
            int id = todoService.add(body.get("task"));
            return ResponseEntity.ok(Map.of("id", id, "message", "Todo added"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/todos/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody Map<String, String> body) {
        try {
            boolean updated = todoService.update(id, body.get("task"));
            if (!updated) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(Map.of("message", "Todo updated"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/todos/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        boolean deleted = todoService.delete(id);
        if (!deleted) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(Map.of("message", "Todo deleted"));
    }
}
