package com.enonic.xp.context;

import com.enonic.xp.session.Session;

public interface LocalScope
    extends MutableAttributes
{
    public String id();

    public Session getSession();

    public void setSession( Session session );
}
