package com.enonic.wem.admin.rest.resource.security;

import java.util.stream.Stream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.rest.resource.security.json.PrincipalsJson;
import com.enonic.wem.admin.rest.resource.security.json.UserStoresJson;
import com.enonic.wem.api.security.PrincipalType;
import com.enonic.wem.api.security.Principals;
import com.enonic.wem.api.security.SecurityService;
import com.enonic.wem.api.security.UserStoreKey;
import com.enonic.wem.api.security.UserStores;


@SuppressWarnings("UnusedDeclaration")
@Path("userstore")
@Produces(MediaType.APPLICATION_JSON)
public class SecurityResource
{

    private SecurityService securityService;

    @GET
    @Path("list")
    public UserStoresJson getUserStores()
    {
        UserStores userStores = securityService.getUserStores();
        return new UserStoresJson( userStores );
    }

    @GET
    @Path("principals")
    public PrincipalsJson getPrincipals( @QueryParam("userStoreKey") final String userStoreKey, @QueryParam("type") final String type )

    {
        UserStoreKey storeKey = new UserStoreKey( userStoreKey );
        PrincipalType principalType = Stream.of( PrincipalType.values() ).
            filter( val -> val.name().equalsIgnoreCase( type ) ).
            findFirst().orElseGet( null );

        if ( principalType == null )
        {
            throw new WebApplicationException( String.format( "wrong principal type: %s", type ) );
        }

        Principals principals = securityService.getPrincipals( storeKey, principalType );
        return new PrincipalsJson( principals );
    }

    public void setSecurityService( final SecurityService securityService )
    {
        this.securityService = securityService;
    }

}
