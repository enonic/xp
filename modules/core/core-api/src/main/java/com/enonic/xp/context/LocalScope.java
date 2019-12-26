package com.enonic.xp.context;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.session.Session;

@PublicApi
public interface LocalScope
    extends MutableAttributes
{
    Session getSession();

    void setSession( Session session );
}
