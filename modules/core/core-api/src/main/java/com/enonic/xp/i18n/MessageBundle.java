package com.enonic.xp.i18n;

import java.util.Map;
import java.util.Set;

public interface MessageBundle
{
    static final String MISSING_VALUE_MESSAGE = "NOT_TRANSLATED";

    Set<String> getKeys();

    String localize( String key, Object... args );

    Map<String, String> asMap();
}
