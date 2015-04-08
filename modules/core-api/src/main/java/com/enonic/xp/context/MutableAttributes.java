package com.enonic.xp.context;

import com.google.common.annotations.Beta;

@Beta
public interface MutableAttributes
    extends ScopeAttributes
{
    void setAttribute( String key, Object value );

    <T> void setAttribute( T value );

    void removeAttribute( String key );

    <T> void removeAttribute( Class<T> type );
}
