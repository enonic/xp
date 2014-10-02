package com.enonic.wem.portal.internal;

import java.util.List;

import com.google.common.collect.Sets;

import com.enonic.wem.servlet.jaxrs.JaxRsDispatcher;

public final class PortalServlet
    extends JaxRsDispatcher
{
    public void setResources( final List<Object> list )
    {
        setContributor( () -> Sets.newHashSet( list ) );
    }
}


