package com.enonic.wem.admin.rest.resource.status;

import java.util.Collections;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

import com.enonic.wem.admin.AdminResource;
import com.enonic.wem.admin.status.GCStatusInfoBuilder;
import com.enonic.wem.admin.status.JVMStatusInfoBuilder;
import com.enonic.wem.admin.status.MemoryStatusInfoBuilder;
import com.enonic.wem.admin.status.OSStatusInfoBuilder;
import com.enonic.wem.admin.status.PropertiesStatusInfoBuilder;
import com.enonic.wem.admin.status.StatusInfoBuilder;

@Path("status")
public final class InfoResource
    implements AdminResource
{
    private final List<StatusInfoBuilder> infoBuilders;

    public InfoResource()
    {
        this.infoBuilders = Lists.newArrayList();
        this.infoBuilders.add( new OSStatusInfoBuilder() );
        this.infoBuilders.add( new JVMStatusInfoBuilder() );
        this.infoBuilders.add( new MemoryStatusInfoBuilder() );
        this.infoBuilders.add( new GCStatusInfoBuilder() );
        this.infoBuilders.add( new PropertiesStatusInfoBuilder() );
        Collections.sort( this.infoBuilders );
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getStatus()
    {
        return createStatus().toString();
    }

    private ObjectNode createStatus()
    {
        final ObjectNode node = JsonNodeFactory.instance.objectNode();
        for ( final StatusInfoBuilder builder : this.infoBuilders )
        {
            builder.addInfo( node );
        }
        return node;
    }
}
