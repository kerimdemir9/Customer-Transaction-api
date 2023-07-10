package com.customer.transaction.validation.helper;
import java.util.Objects;
public class Parsers {
    public static boolean notValidUUID(String uuid) {
        if (Objects.isNull(uuid)) {
            return false;
        }

        return uuid.matches("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}");
    }
}
