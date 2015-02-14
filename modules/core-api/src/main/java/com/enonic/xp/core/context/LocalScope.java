package com.enonic.xp.core.context;

import com.enonic.xp.core.session.Session;

public interface LocalScope
    extends MutableAttributes
{
    public String id();

    public Session getSession();

    public void setSession( Session session );
}
