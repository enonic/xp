package com.enonic.xp.admin.impl.rest.resource.status;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.admin.impl.AdminResource;
import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.server.BuildInfo;
import com.enonic.xp.server.ServerInfo;
import com.enonic.xp.server.VersionInfo;

@Path(ResourceConstants.REST_ROOT + "status")
@Component(immediate = true)
public final class StatusResource
    implements AdminResource
{
    private ServerInfo serverInfo;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ObjectNode getStatus()
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put( "version", VersionInfo.get().getVersion() );
        json.set( "build", createBuildJson() );
        json.put( "installation", this.serverInfo.getName() );
        json.set( "context", createContextJson() );
        return json;
    }

    private ObjectNode createBuildJson()
    {
        final BuildInfo info = this.serverInfo.getBuildInfo();

        final ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put( "hash", info.getHash() );
        node.put( "shortHash", info.getShortHash() );
        node.put( "branch", info.getBranch() );
        node.put( "timestamp", info.getTimestamp() );

        return node;
    }

    private ObjectNode createContextJson()
    {
        final Context context = ContextAccessor.current();
        final AuthenticationInfo authInfo = context.getAuthInfo();

        final ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put( "authenticated", ( authInfo != null ) && authInfo.isAuthenticated() );
        final ArrayNode principals = node.putArray( "principals" );

        if ( authInfo != null )
        {
            for ( final PrincipalKey principal : authInfo.getPrincipals() )
            {
                principals.add( principal.toString() );
            }
        }

        return node;
    }

    @Reference
    public void setServerInfo( final ServerInfo serverInfo )
    {
        this.serverInfo = serverInfo;
    }
}
