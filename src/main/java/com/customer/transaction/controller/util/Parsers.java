package com.customer.transaction.controller.util;

import lombok.val;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

public class Parsers {
    public static Boolean tryParseBoolean(String value, String property) {
        val result = BooleanUtils.toBooleanObject(value);

        if (Objects.isNull(result)) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, property.concat(":").concat(value));
        }

        return result;
    }

    public static Integer tryParseInteger(String value, String property) {
        val result = NumberUtils.toInt(value, -1);

        if (result == -1) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, property.concat(":").concat(value));
        }

        return result;
    }

    public static Long tryParseLong(String value, String property) {
        val result = NumberUtils.toLong(value, -1);

        if (result == -1) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, property.concat(":").concat(value));
        }

        return result;
    }


    public static Double tryParseDouble(String value, String property) {
        val result = NumberUtils.toDouble(value, -1);

        if (result == -1) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, property.concat(":").concat(value));
        }
        return result;
    }
}
