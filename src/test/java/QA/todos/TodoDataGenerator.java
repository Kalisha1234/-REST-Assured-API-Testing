package QA.todos;

import java.util.HashMap;
import java.util.Map;

public final class TodoDataGenerator {

    private TodoDataGenerator() {}

    public static Map<String, Object> createTodo(int userId, String title, boolean completed) {
        Map<String, Object> todo = new HashMap<>();
        todo.put("userId", userId);
        todo.put("title", title);
        todo.put("completed", completed);
        return todo;
    }
}
