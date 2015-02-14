package com.enonic.xp.core.context;

import java.util.Map;

public interface ScopeAttributes
{
    public Object getAttribute( String key );

    public <T> T getAttribute( Class<T> type );

    public Map<String, Object> getAttributes();
}
