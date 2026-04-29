package com.sit707.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/todos")
public class TodoController {

    @Autowired
    private TodoService todoService;

    @GetMapping
    public Map<Integer, String> getAll() {
        return todoService.getAll();
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody Map<String, String> body) {
        try {
            int id = todoService.add(body.get("task"));
            return ResponseEntity.ok(Map.of("id", id, "message", "Todo added"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody Map<String, String> body) {
        try {
            boolean updated = todoService.update(id, body.get("task"));
            if (!updated) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(Map.of("message", "Todo updated"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        boolean deleted = todoService.delete(id);
        if (!deleted) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(Map.of("message", "Todo deleted"));
    }
}
