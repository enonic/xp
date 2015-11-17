package com.enonic.xp.server.impl.status;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.status.StatusReporter;

@Component(immediate = true)
public final class OsgiBundleReporter
    implements StatusReporter
{
    private BundleContext context;

    @Override
    public String getName()
    {
        return "osgi.bundle";
    }

    @Activate
    public void activate( final BundleContext context )
    {
        this.context = context;
    }

    @Override
    public ObjectNode getReport()
    {
        final Bundle[] bundles = this.context.getBundles();

        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put( "count", bundles.length );

        final ArrayNode array = json.putArray( "bundles" );
        for ( final Bundle bundle : bundles )
        {
            array.add( buildBundleInfo( bundle ) );
        }

        return json;
    }

    private ObjectNode buildBundleInfo( final Bundle bundle )
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put( "id", bundle.getBundleId() );
        json.put( "name", bundle.getSymbolicName() );
        json.put( "state", stateAsString( bundle.getState() ) );
        return json;
    }

    private String stateAsString( final int state )
    {
        if ( state == Bundle.ACTIVE )
        {
            return "ACTIVE";
        }

        if ( state == Bundle.INSTALLED )
        {
            return "INSTALLED";
        }

        if ( state == Bundle.UNINSTALLED )
        {
            return "UNINSTALLED";
        }

        if ( state == Bundle.RESOLVED )
        {
            return "RESOLVED";
        }

        if ( state == Bundle.STARTING )
        {
            return "STARTING";
        }

        if ( state == Bundle.STOPPING )
        {
            return "STOPPING";
        }

        return "UNKNOWN";
    }
}
