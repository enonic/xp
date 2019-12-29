package com.enonic.xp.server.impl.status;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.runtime.ServiceComponentRuntime;
import org.osgi.service.component.runtime.dto.ComponentConfigurationDTO;
import org.osgi.service.component.runtime.dto.ComponentDescriptionDTO;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.status.JsonStatusReporter;
import com.enonic.xp.status.StatusReporter;

@Component(immediate = true, service = StatusReporter.class)
public final class OsgiComponentReporter
    extends JsonStatusReporter
{
    private ServiceComponentRuntime runtime;

    @Override
    public String getName()
    {
        return "osgi.component";
    }

    @Reference
    public void setRuntime( final ServiceComponentRuntime runtime )
    {
        this.runtime = runtime;
    }

    @Override
    public JsonNode getReport()
    {
        final List<ComponentDescriptionDTO> list = new ArrayList<>( this.runtime.getComponentDescriptionDTOs() );
        list.sort( this::compareDescription );

        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put( "count", list.size() );

        final ArrayNode array = json.putArray( "components" );
        for ( final ComponentDescriptionDTO component : list )
        {
            array.add( buildDesc( component ) );
        }

        return json;
    }

    private ObjectNode buildDesc( final ComponentDescriptionDTO desc )
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put( "name", desc.name );
        json.put( "enabled", desc.defaultEnabled );
        json.put( "bundle", desc.bundle.id );

        final ArrayNode configJson = json.putArray( "instances" );

        final List<ComponentConfigurationDTO> instances = new ArrayList<>( this.runtime.getComponentConfigurationDTOs( desc ) );
        instances.sort( this::compareConfig );

        for ( final ComponentConfigurationDTO instance : instances )
        {
            configJson.add( buildInstance( instance ) );
        }

        return json;
    }

    private ObjectNode buildInstance( final ComponentConfigurationDTO instance )
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put( "id", instance.id );
        json.put( "state", toStateString( instance.state ) );

        final Object pid = instance.properties.get( Constants.SERVICE_PID );
        if ( pid != null )
        {
            json.put( "pid", pid.toString() );
        }

        return json;
    }

    protected String toStateString( final int state )
    {
        switch ( state )
        {
            case ( ComponentConfigurationDTO.UNSATISFIED_REFERENCE ):
                return "UNSATISFIED_REFERENCE";
            case ( ComponentConfigurationDTO.ACTIVE ):
                return "ACTIVE";
            case ( ComponentConfigurationDTO.SATISFIED ):
                return "SATISFIED";
            case ( ComponentConfigurationDTO.UNSATISFIED_CONFIGURATION ):
                return "UNSATISFIED_CONFIGURATION";
            default:
                return "UNKNOWN";
        }
    }

    private int compareDescription( final ComponentDescriptionDTO o1, final ComponentDescriptionDTO o2 )
    {
        final long bundleId1 = o1.bundle.id;
        final long bundleId2 = o2.bundle.id;
        int result = Long.signum( bundleId1 - bundleId2 );
        if ( result == 0 )
        {
            if ( o1.name == null )
            {
                result = ( o2.name == null ? 0 : -1 );
            }
            else if ( o2.name == null )
            {
                result = 1;
            }
            else
            {
                result = o1.name.compareTo( o2.name );
            }
        }
        return result;
    }

    private int compareConfig( final ComponentConfigurationDTO o1, final ComponentConfigurationDTO o2 )
    {
        return Long.signum( o1.id - o2.id );
    }
}
