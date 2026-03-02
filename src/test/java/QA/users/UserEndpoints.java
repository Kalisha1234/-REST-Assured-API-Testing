package QA.users;

public final class UserEndpoints {

    private UserEndpoints() {}

    public static final String USERS = "/users";
    public static final String USER_BY_ID = "/users/{id}";
    public static final String USER_POSTS = "/users/{id}/posts";
    public static final String USER_ALBUMS = "/users/{id}/albums";
    public static final String USER_TODOS = "/users/{id}/todos";
}
