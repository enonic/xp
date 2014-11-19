package com.enonic.wem.admin.rest.resource.security;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.enonic.wem.admin.rest.exception.NotFoundWebException;
import com.enonic.wem.admin.rest.resource.ResourceConstants;
import com.enonic.wem.admin.rest.resource.security.json.GroupJson;
import com.enonic.wem.admin.rest.resource.security.json.PrincipalJson;
import com.enonic.wem.admin.rest.resource.security.json.PrincipalsJson;
import com.enonic.wem.admin.rest.resource.security.json.RoleJson;
import com.enonic.wem.admin.rest.resource.security.json.UserJson;
import com.enonic.wem.admin.rest.resource.security.json.UserStoresJson;
import com.enonic.wem.api.security.Group;
import com.enonic.wem.api.security.Principal;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.PrincipalKeys;
import com.enonic.wem.api.security.PrincipalRelationship;
import com.enonic.wem.api.security.PrincipalRelationships;
import com.enonic.wem.api.security.PrincipalType;
import com.enonic.wem.api.security.Principals;
import com.enonic.wem.api.security.Role;
import com.enonic.wem.api.security.SecurityService;
import com.enonic.wem.api.security.User;
import com.enonic.wem.api.security.UserStoreKey;
import com.enonic.wem.api.security.UserStores;
import com.enonic.wem.servlet.jaxrs.JaxRsComponent;

import static java.util.stream.Collectors.toList;


@SuppressWarnings("UnusedDeclaration")
@Path(ResourceConstants.REST_ROOT + "security")
@Produces(MediaType.APPLICATION_JSON)
public final class SecurityResource
    implements JaxRsComponent
{
    private SecurityService securityService;

    @GET
    @Path("userstore/list")
    public UserStoresJson getUserStores()
    {
        final UserStores userStores = securityService.getUserStores();
        return new UserStoresJson( userStores );
    }

    @GET
    @Path("principals")
    public PrincipalsJson getPrincipals( @QueryParam("userStoreKey") final String userStoreKey, @QueryParam("type") final String type )

    {
        final UserStoreKey storeKey = new UserStoreKey( userStoreKey );
        final PrincipalType principalType = Stream.of( PrincipalType.values() ).
            filter( val -> val.name().equalsIgnoreCase( type ) ).
            findFirst().orElse( null );

        if ( principalType == null )
        {
            throw new WebApplicationException( String.format( "Invalid principal type: %s", type ), Response.Status.BAD_REQUEST );
        }

        final Principals principals = securityService.getPrincipals( storeKey, principalType );
        return new PrincipalsJson( principals );
    }

    @GET
    @Path("principals/{key: ([^:(%3A)]+)(:|%3A)(user|group|role)(:|%3A)([^:(%3A)]+)}")
    public PrincipalJson getPrincipalByKey( @PathParam("key") final String keyParam )
    {
        final PrincipalKey principalKey = PrincipalKey.from( keyParam );
        final Optional<? extends Principal> principalResult = securityService.getPrincipal( principalKey );

        if ( !principalResult.isPresent() )
        {
            throw new NotFoundWebException( String.format( "Principal [%s] was not found", keyParam ) );
        }

        final Principal principal = principalResult.get();
        switch ( principalKey.getType() )
        {
            case USER:
                return new UserJson( (User) principal );

            case GROUP:
                final PrincipalKeys groupMembers = getMembers( principalKey );
                return new GroupJson( (Group) principal, groupMembers );

            case ROLE:
                final PrincipalKeys roleMembers = getMembers( principalKey );
                return new RoleJson( (Role) principal, roleMembers );
        }

        throw new NotFoundWebException( String.format( "Principal [%s] was not found", keyParam ) );
    }

    private PrincipalKeys getMembers( final PrincipalKey principal )
    {
        final PrincipalRelationships relationships = this.securityService.getRelationships( principal );
        final List<PrincipalKey> members = relationships.stream().map( PrincipalRelationship::getTo ).collect( toList() );
        return PrincipalKeys.from( members );
    }

    public void setSecurityService( final SecurityService securityService )
    {
        this.securityService = securityService;
    }

}
