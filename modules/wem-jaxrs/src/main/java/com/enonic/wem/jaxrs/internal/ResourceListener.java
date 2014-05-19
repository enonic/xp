package com.enonic.wem.jaxrs.internal;

import java.util.Map;

import javax.inject.Inject;

import com.enonic.wem.guice.listener.ServiceListener;

final class ResourceListener
    implements ServiceListener<Object>
{
    private final JaxRsServlet servlet;

    @Inject
    public ResourceListener( final JaxRsServlet servlet )
    {
        this.servlet = servlet;
    }

    @Override
    public String getFilter()
    {
        return "(jaxrs.export=true)";
    }

    @Override
    public Class<?> getFilterClass()
    {
        return null;
    }

    @Override
    public void serviceAdded( final Object service, final Map<String, Object> attributes )
    {
        if ( service != null )
        {
            this.servlet.addResource( service );
        }
    }

    @Override
    public void serviceModified( final Object service, final Map<String, Object> attributes )
    {
        if ( service != null )
        {
            this.servlet.addResource( service );
        }
    }

    @Override
    public void serviceRemoved( final Object service )
    {
        if ( service != null )
        {
            this.servlet.removeResource( service );
        }
    }
}
