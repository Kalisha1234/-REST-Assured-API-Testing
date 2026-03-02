package QA.comments;

import java.util.HashMap;
import java.util.Map;

public final class CommentDataGenerator {

    private CommentDataGenerator() {}

    public static Map<String, Object> createComment(int postId, String name, String email, String body) {
        Map<String, Object> comment = new HashMap<>();
        comment.put("postId", postId);
        comment.put("name", name);
        comment.put("email", email);
        comment.put("body", body);
        return comment;
    }
}
