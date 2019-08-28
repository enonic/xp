package com.enonic.xp.i18n;

import java.util.Map;
import java.util.Set;

public interface MessageBundle
{
    Set<String> getKeys();

    String localize( String key, Object... args );

    String getMessage( String key );

    Map<String, String> asMap();
}
