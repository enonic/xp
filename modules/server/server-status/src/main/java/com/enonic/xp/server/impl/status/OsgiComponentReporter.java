package com.enonic.xp.server.impl.status;

import java.util.Collection;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.runtime.ServiceComponentRuntime;
import org.osgi.service.component.runtime.dto.ComponentDescriptionDTO;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.status.StatusReporter;

@Component(immediate = true)
public final class OsgiComponentReporter
    implements StatusReporter
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
    public ObjectNode getReport()
    {
        final Collection<ComponentDescriptionDTO> list = this.runtime.getComponentDescriptionDTOs();

        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put( "count", list.size() );

        final ArrayNode array = json.putArray( "components" );
        for ( final ComponentDescriptionDTO component : list )
        {
            array.add( buildInfo( component ) );
        }

        return json;
    }

    private ObjectNode buildInfo( final ComponentDescriptionDTO component )
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put( "name", component.name );
        json.put( "enabled", this.runtime.isComponentEnabled( component ) );
        json.put( "bundle", component.bundle.symbolicName );
        return json;
    }
}
