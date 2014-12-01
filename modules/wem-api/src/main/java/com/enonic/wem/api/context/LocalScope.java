package com.enonic.wem.api.context;

import com.enonic.wem.api.session.Session;

public interface LocalScope
    extends MutableAttributes
{
    public String id();

    public Session getSession();

    public void setSession( Session session );
}
