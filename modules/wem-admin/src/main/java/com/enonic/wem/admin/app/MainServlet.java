package com.enonic.wem.admin.app;

import java.util.List;

import com.google.common.collect.Sets;

import com.enonic.wem.servlet.jaxrs.JaxRsDispatcher2;

public final class MainServlet
    extends JaxRsDispatcher2
{
    public void setResources( final List<Object> list )
    {
        setContributor( () -> Sets.newHashSet( list ) );
    }
}
