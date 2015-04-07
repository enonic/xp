package com.enonic.xp.context;

import java.util.Map;

import com.google.common.annotations.Beta;

@Beta
public interface ScopeAttributes
{
    Object getAttribute( String key );

    <T> T getAttribute( Class<T> type );

    Map<String, Object> getAttributes();
}
