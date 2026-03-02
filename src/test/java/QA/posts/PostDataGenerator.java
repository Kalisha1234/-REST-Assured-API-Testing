package QA.posts;

import java.util.HashMap;
import java.util.Map;

public final class PostDataGenerator {

    private PostDataGenerator() {}

    public static Map<String, Object> createPost(int userId, String title, String body) {
        Map<String, Object> post = new HashMap<>();
        post.put("userId", userId);
        post.put("title", title);
        post.put("body", body);
        return post;
    }
}
