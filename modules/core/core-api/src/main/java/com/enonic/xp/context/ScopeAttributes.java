package com.enonic.xp.context;

import java.util.Map;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;


@NullMarked
public interface ScopeAttributes
{
    @Nullable Object getAttribute( String key );

    <T extends @Nullable Object> @Nullable T getAttribute( Class<T> type );

    Map<String, Object> getAttributes();
}