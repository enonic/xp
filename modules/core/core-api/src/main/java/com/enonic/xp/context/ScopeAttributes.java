package com.enonic.xp.context;

import java.util.Map;


public interface ScopeAttributes
{
    Object getAttribute( String key );

    <T> T getAttribute( Class<T> type );

    Map<String, Object> getAttributes();
}
