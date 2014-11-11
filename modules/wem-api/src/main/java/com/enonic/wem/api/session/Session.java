package com.enonic.wem.api.session;

import java.util.Map;

public interface Session
{
    public SessionKey getKey();

    public Object getAttribute( String key );

    public <T> T getAttribute( Class<T> type );

    public void setAttribute( String key, Object value );

    public <T> void setAttribute( T value );

    public void removeAttribute( String key );

    public <T> void removeAttribute( Class<T> type );

    public Map<String, Object> getAttributes();
}
