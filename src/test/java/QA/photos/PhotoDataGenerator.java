package QA.photos;

import java.util.HashMap;
import java.util.Map;

public final class PhotoDataGenerator {

    private PhotoDataGenerator() {}

    public static Map<String, Object> createPhoto(int albumId, String title, String url, String thumbnailUrl) {
        Map<String, Object> photo = new HashMap<>();
        photo.put("albumId", albumId);
        photo.put("title", title);
        photo.put("url", url);
        photo.put("thumbnailUrl", thumbnailUrl);
        return photo;
    }
}
