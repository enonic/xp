package com.enonic.xp.server.impl.status;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import com.enonic.xp.status.StatusReporter;
import com.enonic.xp.util.Exceptions;

@Component(immediate = true)
public final class OsgiServiceReporter
    implements StatusReporter
{
    private BundleContext context;

    @Override
    public String getName()
    {
        return "osgi.service";
    }

    @Activate
    public void activate( final BundleContext context )
    {
        this.context = context;
    }

    @Override
    public ObjectNode getReport()
    {
        final ServiceReference[] list = getServices();

        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put( "count", list.length );

        final ArrayNode array = json.putArray( "services" );
        for ( final ServiceReference ref : list )
        {
            array.add( buildInfo( ref ) );
        }

        return json;
    }

    private ObjectNode buildInfo( final ServiceReference ref )
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put( "id", ref.getProperty( Constants.SERVICE_ID ).toString() );
        json.set( "iface", toArray( (String[]) ref.getProperty( Constants.OBJECTCLASS ) ) );
        json.put( "bundle", ref.getBundle().getSymbolicName() );
        return json;
    }

    private ServiceReference[] getServices()
    {
        try
        {
            return this.context.getAllServiceReferences( null, null );
        }
        catch ( final Exception e )
        {
            throw Exceptions.unchecked( e );
        }
    }

    private JsonNode toArray( final String[] values )
    {
        if ( ( values == null ) || ( values.length == 0 ) )
        {
            return NullNode.instance;
        }

        if ( values.length == 1 )
        {
            return TextNode.valueOf( values[0] );
        }

        final ArrayNode json = JsonNodeFactory.instance.arrayNode();
        for ( final String value : values )
        {
            json.add( value );
        }

        return json;
    }
}
