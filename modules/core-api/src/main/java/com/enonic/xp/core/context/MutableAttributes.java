package com.enonic.xp.core.context;

public interface MutableAttributes
    extends ScopeAttributes
{
    public void setAttribute( String key, Object value );

    public <T> void setAttribute( T value );

    public void removeAttribute( String key );

    public <T> void removeAttribute( Class<T> type );
}
