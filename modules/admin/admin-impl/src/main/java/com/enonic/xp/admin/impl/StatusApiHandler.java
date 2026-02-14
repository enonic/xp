package com.enonic.xp.admin.impl;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.net.MediaType;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.handler.WebHandlerHelper;
import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.server.BuildInfo;
import com.enonic.xp.server.ServerInfo;
import com.enonic.xp.server.VersionInfo;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

@Component(immediate = true, property = {"key=" + StatusApiHandler.STATUS_API,
    "displayName=Status API", "allowedPrincipals=role:system.everyone"})
public class StatusApiHandler
    implements UniversalApiHandler
{
    static final String STATUS_API = "admin:status";

    private final ServerInfo info;

    @Activate
    public StatusApiHandler()
    {
        this.info = ServerInfo.get();
    }

    @Override
    public WebResponse handle( final WebRequest request )
    {
        final String apiPath = WebHandlerHelper.findApiPath( request, STATUS_API );
        if ( !apiPath.isEmpty() && !apiPath.equals( "/" ) )
        {
            return PortalResponse.create().status( HttpStatus.NOT_FOUND ).build();
        }

        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put( "version", VersionInfo.get().getVersion() );
        json.set( "build", buildBuildInfo() );
        json.put( "installation", this.info.getName() );

        json.set( "context", createContextJson() );

        return PortalResponse.create().status( HttpStatus.OK ).body( json.toString() ).contentType( MediaType.JSON_UTF_8 ).build();
    }

    private ObjectNode buildBuildInfo()
    {
        final BuildInfo info = this.info.getBuildInfo();

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
        node.put( "authenticated", authInfo != null && authInfo.isAuthenticated() );
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
