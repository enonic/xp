package com.enonic.xp.context;

import com.google.common.annotations.Beta;

import com.enonic.xp.session.Session;

@Beta
public interface LocalScope
    extends MutableAttributes
{
    String id();

    Session getSession();

    void setSession( Session session );
}
