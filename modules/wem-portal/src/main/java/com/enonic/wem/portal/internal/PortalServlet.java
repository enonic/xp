package com.enonic.wem.portal.internal;

import java.util.List;

import com.enonic.wem.servlet.jaxrs.JaxRsDispatcher2;

public final class PortalServlet
    extends JaxRsDispatcher2
{
    public void setResources( final List<Object> list )
    {
        list.forEach( this::addComponent );
    }
}
