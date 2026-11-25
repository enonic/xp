package com.enonic.xp.impl.server.rest.api;

import java.util.LinkedHashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AccessTokenInfo;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.server.ServerInfo;
import com.enonic.xp.server.VersionInfo;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

/**
 * {@code server:whoami} - who the request is authenticated as and which XP answers: the user, the roles the request
 * holds, the access token it came with, if any, and the server's version, with the build's commit for a snapshot. A client calls it to check a login and to
 * learn what its credentials are good for.
 * <p>
 * Every authenticated user may call it, and no vhost can switch it off: it has no verb for a mapping to leave out.
 */
@Component(service = UniversalApiHandler.class, property = {"key=server:whoami", "title=Who Am I API", "mount=management",
    "allowedPrincipals=role:system.authenticated"})
public class WhoAmIApiHandler
    extends ManagementApiHandler
{
    static final String KEY = "server:whoami";

    @Activate
    public WhoAmIApiHandler()
    {
        super( KEY );

        route( HttpMethod.GET, "/", this::whoAmI );
    }

    private WebResponse whoAmI( final WebRequest request, final Map<String, String> params )
        throws JsonProcessingException
    {
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        final User user = authInfo.getUser();
        if ( !authInfo.isAuthenticated() || user == null )
        {
            return error( HttpStatus.UNAUTHORIZED, null );
        }

        final Map<String, Object> json = new LinkedHashMap<>();
        json.put( "user", map( user ) );
        json.put( "roles", authInfo.getPrincipals().stream().filter( PrincipalKey::isRole ).map( PrincipalKey::toString ).sorted().toList() );
        if ( authInfo.getActor() != null )
        {
            json.put( "actor", authInfo.getActor().toString() );
        }
        final AccessTokenInfo accessToken = authInfo.getAccessToken();
        if ( accessToken != null )
        {
            json.put( "accessToken", map( accessToken ) );
        }
        json.put( "server", Map.of( "version", version() ) );
        return json( json );
    }

    /**
     * The server's version. A snapshot version says little of what is running, so it is followed by the short hash of
     * the commit it was built from, when the build recorded one: {@code 8.2.0-SNAPSHOT+b741000}.
     */
    private static String version()
    {
        final String version = VersionInfo.get().getVersion();
        if ( !version.endsWith( "-SNAPSHOT" ) )
        {
            return version;
        }
        final String shortHash = ServerInfo.get().getBuildInfo().getShortHash();
        return shortHash.isBlank() || "N/A".equals( shortHash ) ? version : version + "+" + shortHash;
    }

    private static Map<String, Object> map( final User user )
    {
        final Map<String, Object> json = new LinkedHashMap<>();
        json.put( "key", user.getKey().toString() );
        json.put( "login", user.getLogin() );
        json.put( "displayName", user.getDisplayName() );
        if ( user.getEmail() != null )
        {
            json.put( "email", user.getEmail() );
        }
        json.put( "idProvider", user.getKey().getIdProviderKey().toString() );
        return json;
    }

    private static Map<String, Object> map( final AccessTokenInfo accessToken )
    {
        final Map<String, Object> json = new LinkedHashMap<>();
        json.put( "issuer", accessToken.getIssuer() );
        if ( accessToken.getClientId() != null )
        {
            json.put( "clientId", accessToken.getClientId() );
        }
        json.put( "scope", accessToken.getScope().stream().sorted().toList() );
        json.put( "audience", accessToken.getAudience().stream().sorted().toList() );
        return json;
    }
}
