package com.enonic.wem.admin.app;

import java.util.List;

import com.google.common.collect.Sets;

import com.enonic.wem.servlet.jaxrs.JaxRsDispatcher;

public final class MainServlet
    extends JaxRsDispatcher
{
    public void setResources( final List<Object> list )
    {
        setContributor( () -> Sets.newHashSet( list ) );
    }
}
