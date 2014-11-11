package com.enonic.wem.api.context;

import com.enonic.wem.api.session.Session;

public interface MutableContext
    extends Context
{
    public <T> void setAttribute( T value );

    public void setAttribute( String key, Object value );

    public void setSession( Session session );
}
