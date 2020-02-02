package com.griddynamics.catalog.converter;

import com.univocity.parsers.conversions.Conversion;

public class StringWithCommaToLongConverter implements Conversion<String, Long> {

    @Override
    public Long execute(String s) {
        return (s == null) ? null : Long.parseLong(s.replaceAll(",", ""));
    }

    @Override
    public String revert(Long o) {
        return (o == null) ? null : String.valueOf(o);
    }
}
