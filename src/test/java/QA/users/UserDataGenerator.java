package QA.users;

import java.util.HashMap;
import java.util.Map;

public final class UserDataGenerator {

    private UserDataGenerator() {}

    public static Map<String, Object> createUser(String name, String username, String email,
                                                   String phone, String website) {
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("username", username);
        user.put("email", email);
        user.put("phone", phone);
        user.put("website", website);

        Map<String, Object> address = new HashMap<>();
        address.put("street", "Test Street");
        address.put("suite", "Suite 100");
        address.put("city", "Test City");
        address.put("zipcode", "12345-6789");
        Map<String, String> geo = new HashMap<>();
        geo.put("lat", "-37.3159");
        geo.put("lng", "81.1496");
        address.put("geo", geo);
        user.put("address", address);

        Map<String, String> company = new HashMap<>();
        company.put("name", "Test Company");
        company.put("catchPhrase", "Multi-layered client-server neural-net");
        company.put("bs", "harness real-time e-markets");
        user.put("company", company);

        return user;
    }
}
