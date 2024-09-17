package com.enonic.xp.admin.impl.rest.resource.status;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.server.ServerInfo;

@Deprecated
@Path(ResourceConstants.REST_ROOT + "status")
@Component(immediate = true, property = "group=admin")
public final class StatusResource
    implements JaxRsComponent
{
    ServerInfo info;

    public StatusResource()
    {
        this.info = ServerInfo.get();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ObjectNode getStatus()
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        new ProductInfoBuilder( this.info ).build( json );

        json.set( "context", createContextJson() );
        json.set( "readonly", JsonNodeFactory.instance.booleanNode( false ) ); // https://github.com/enonic/xp/issues/8150
        return json;
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
}
