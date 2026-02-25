package com.enonic.xp.i18n;

import java.util.Map;
import java.util.Set;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface MessageBundle
{
    Set<String> getKeys();

    @Nullable String localize( String key, @Nullable Object... args );

    @Nullable String getMessage( String key );

    Map<String, String> asMap();
}
