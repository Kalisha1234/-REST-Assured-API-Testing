package QA.albums;

import java.util.HashMap;
import java.util.Map;

public final class AlbumDataGenerator {

    private AlbumDataGenerator() {}

    public static Map<String, Object> createAlbum(int userId, String title) {
        Map<String, Object> album = new HashMap<>();
        album.put("userId", userId);
        album.put("title", title);
        return album;
    }
}
