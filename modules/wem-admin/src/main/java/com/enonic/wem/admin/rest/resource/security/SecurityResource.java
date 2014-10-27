package com.enonic.wem.admin.rest.resource.security;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.rest.exception.IllegalArgumentWebException;
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
        PrincipalType principalType = null;
        UserStoreKey storeKey = new UserStoreKey( userStoreKey );
        if ( !getEnumItems( PrincipalType.class ).contains( type.toUpperCase() ) )
        {
            IllegalArgumentWebException ill =
                new IllegalArgumentWebException( String.format( "wrong principal type: %s", type.toString() ) );
        }
        else
        {
            principalType = PrincipalType.valueOf( type.toUpperCase() );
        }
        Principals principals = securityService.getPrincipals( storeKey, principalType );
        return new PrincipalsJson( principals );
    }

    public void setSecurityService( final SecurityService securityService )
    {
        this.securityService = securityService;
    }


    public static <E extends Enum<E>> List<String> getEnumItems( final Class<E> en )
    {
        List<String> results = new ArrayList<String>();
        for ( E t : en.getEnumConstants() )
        {
            results.add( t.toString() );
            System.out.println( t );
        }

        return results;
    }
}
