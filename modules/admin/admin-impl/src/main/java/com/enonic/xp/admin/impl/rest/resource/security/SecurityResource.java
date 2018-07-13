package com.enonic.xp.admin.impl.rest.resource.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.admin.impl.rest.resource.security.json.CreateGroupJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.CreateRoleJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.CreateUserJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.CreateUserStoreJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.DeletePrincipalJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.DeletePrincipalResultJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.DeletePrincipalsResultJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.DeleteUserStoreJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.DeleteUserStoreResultJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.DeleteUserStoresResultJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.EmailAvailabilityJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.FetchPrincipalsByKeysJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.FindPrincipalsResultJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.GroupJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.PrincipalJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.RoleJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.SyncUserStoreJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.SyncUserStoreResultJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.SyncUserStoresResultJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.UpdateGroupJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.UpdatePasswordJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.UpdateRoleJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.UpdateUserJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.UpdateUserStoreJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.UserJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.UserStoreJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.UserStoresJson;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.auth.AuthDescriptor;
import com.enonic.xp.auth.AuthDescriptorMode;
import com.enonic.xp.auth.AuthDescriptorService;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.jaxrs.JaxRsExceptions;
import com.enonic.xp.portal.auth.AuthControllerExecutionParams;
import com.enonic.xp.portal.auth.AuthControllerService;
import com.enonic.xp.security.AuthConfig;
import com.enonic.xp.security.Group;
import com.enonic.xp.security.Principal;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.PrincipalQuery;
import com.enonic.xp.security.PrincipalQueryResult;
import com.enonic.xp.security.PrincipalRelationship;
import com.enonic.xp.security.PrincipalRelationships;
import com.enonic.xp.security.PrincipalType;
import com.enonic.xp.security.Principals;
import com.enonic.xp.security.Role;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserStore;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.UserStores;
import com.enonic.xp.security.acl.UserStoreAccessControlList;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang.StringUtils.isBlank;


@SuppressWarnings("UnusedDeclaration")
@Path(ResourceConstants.REST_ROOT + "security")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_LOGIN_ID)
@Component(immediate = true, property = "group=admin")
public final class SecurityResource
    implements JaxRsComponent
{
    private SecurityService securityService;

    private AuthDescriptorService authDescriptorService;

    private AuthControllerService authControllerService;

    @GET
    @Path("userstore/list")
    public UserStoresJson getUserStores()
    {
        final UserStores userStores = securityService.getUserStores();
        return new UserStoresJson( userStores );
    }

    @GET
    @Path("userstore")
    public UserStoreJson getUserStore( @QueryParam("key") final String keyParam )
    {
        if ( keyParam == null )
        {
            return null;
        }

        final UserStoreKey userStoreKey = UserStoreKey.from( keyParam );
        final UserStore userStore = securityService.getUserStore( userStoreKey );
        if ( userStore == null )
        {
            throw JaxRsExceptions.notFound( String.format( "User Store [%s] not found", keyParam ) );
        }

        final AuthDescriptorMode idProviderMode = retrieveIdProviderMode( userStore );
        final UserStoreAccessControlList userStorePermissions = securityService.getUserStorePermissions( userStoreKey );

        final Principals principals = securityService.getPrincipals( userStorePermissions.getAllPrincipals() );
        return new UserStoreJson( userStore, idProviderMode, userStorePermissions, principals );
    }

    @GET
    @Path("userstore/default")
    public UserStoreJson getDefaultUserStore()
    {
        final UserStore userStore = UserStore.create().displayName( "" ).key( UserStoreKey.createDefault() ).build();

        final UserStoreAccessControlList userStorePermissions = securityService.getDefaultUserStorePermissions();

        final AuthDescriptorMode idProviderMode = retrieveIdProviderMode( userStore );
        final Principals principals = securityService.getPrincipals( userStorePermissions.getAllPrincipals() );
        return new UserStoreJson( userStore, idProviderMode, userStorePermissions, principals );
    }

    @POST
    @Path("userstore/create")
    public UserStoreJson createUserStore( final CreateUserStoreJson params )
    {
        final UserStore userStore = securityService.createUserStore( params.getCreateUserStoreParams() );
        final UserStoreAccessControlList permissions = securityService.getUserStorePermissions( userStore.getKey() );

        final AuthDescriptorMode idProviderMode = retrieveIdProviderMode( userStore );
        final Principals principals = securityService.getPrincipals( permissions.getAllPrincipals() );
        return new UserStoreJson( userStore, idProviderMode, permissions, principals );
    }

    @POST
    @Path("userstore/update")
    public UserStoreJson updateUserStore( final UpdateUserStoreJson params )
    {
        final UserStore userStore = securityService.updateUserStore( params.getUpdateUserStoreParams() );
        final UserStoreAccessControlList permissions = securityService.getUserStorePermissions( userStore.getKey() );

        final AuthDescriptorMode idProviderMode = retrieveIdProviderMode( userStore );
        final Principals principals = securityService.getPrincipals( permissions.getAllPrincipals() );
        return new UserStoreJson( userStore, idProviderMode, permissions, principals );
    }

    @POST
    @Path("userstore/delete")
    public DeleteUserStoresResultJson deleteUserStore( final DeleteUserStoreJson params )
    {
        final DeleteUserStoresResultJson resultsJson = new DeleteUserStoresResultJson();
        params.getKeys().stream().map( UserStoreKey::from ).forEach( ( userStoreKey ) -> {
            try
            {
                securityService.deleteUserStore( userStoreKey );
                resultsJson.add( DeleteUserStoreResultJson.success( userStoreKey ) );
            }
            catch ( Exception e )
            {
                resultsJson.add( DeleteUserStoreResultJson.failure( userStoreKey, e.getMessage() ) );
            }
        } );
        return resultsJson;
    }

    @POST
    @Path("userstore/sync")
    public SyncUserStoresResultJson synchUserStore( final SyncUserStoreJson params, @Context HttpServletRequest httpRequest )
    {
        final SyncUserStoresResultJson resultsJson = new SyncUserStoresResultJson();
        params.getKeys().stream().map( UserStoreKey::from ).forEach( ( userStoreKey ) -> {
            try
            {
                final AuthControllerExecutionParams syncParams = AuthControllerExecutionParams.create().
                    userStoreKey( userStoreKey ).
                    functionName( "sync" ).
                    servletRequest( httpRequest ).
                    build();
                authControllerService.execute( syncParams );
                resultsJson.add( SyncUserStoreResultJson.success( userStoreKey ) );
            }
            catch ( Exception e )
            {
                resultsJson.add( SyncUserStoreResultJson.failure( userStoreKey, e.getMessage() ) );
            }
        } );
        return resultsJson;
    }

    @GET
    @Path("principals")
    public FindPrincipalsResultJson findPrincipals( @QueryParam("types") final String types,

                                                    @QueryParam("query") final String query,

                                                    @QueryParam("userStoreKey") final String storeKey,

                                                    @QueryParam("from") final Integer from,

                                                    @QueryParam("size") final Integer size )
    {

        final List<PrincipalType> principalTypes = new ArrayList<>();
        if ( StringUtils.isNotBlank( types ) )
        {
            final String[] typeItems = types.split( "," );
            for ( String typeItem : typeItems )
            {
                try
                {
                    principalTypes.add( PrincipalType.valueOf( typeItem.toUpperCase() ) );
                }
                catch ( IllegalArgumentException e )
                {
                    throw new WebApplicationException( "Invalid principal type: " + typeItem );
                }
            }
        }

        final PrincipalQuery.Builder principalQuery = PrincipalQuery.create().
            getAll().
            includeTypes( principalTypes ).
            searchText( query );

        if ( StringUtils.isNotEmpty( storeKey ) )
        {
            principalQuery.userStore( UserStoreKey.from( storeKey ) );
        }

        if ( from != null )
        {
            principalQuery.from( from );
        }

        if ( size != null )
        {
            principalQuery.size( size );
        }

        final PrincipalQueryResult result = securityService.query( principalQuery.build() );
        return new FindPrincipalsResultJson( result.getPrincipals(), result.getTotalSize() );
    }

    private PrincipalJson principalToJson( final Principal principal, final Boolean resolveMemberships )
    {

        if ( principal == null )
        {
            return null;
        }

        final PrincipalKey principalKey = principal.getKey();

        switch ( principalKey.getType() )
        {
            case USER:
                if ( resolveMemberships )
                {
                    final PrincipalKeys membershipKeys = securityService.getMemberships( principalKey );
                    final Principals memberships = securityService.getPrincipals( membershipKeys );
                    return new UserJson( (User) principal, memberships );
                }
                else
                {
                    return new UserJson( (User) principal );
                }

            case GROUP:
                final PrincipalKeys groupMembers = getMembers( principalKey );
                if ( resolveMemberships )
                {
                    final PrincipalKeys membershipKeys = securityService.getMemberships( principalKey );
                    final Principals memberships = securityService.getPrincipals( membershipKeys );
                    return new GroupJson( (Group) principal, groupMembers, memberships );
                }
                else
                {
                    return new GroupJson( (Group) principal, groupMembers );
                }

            case ROLE:
                final PrincipalKeys roleMembers = getMembers( principalKey );
                return new RoleJson( (Role) principal, roleMembers );
        }
        return null;
    }

    @POST
    @Path("principals/resolveByKeys")
    public List<PrincipalJson> getPrincipalsByKeys( final FetchPrincipalsByKeysJson json )
    {
        final PrincipalKeys principalKeys =
            PrincipalKeys.from( json.getKeys().stream().map( key -> PrincipalKey.from( key ) ).collect( Collectors.toList() ) );

        final Principals principalsResult = securityService.getPrincipals( principalKeys );

        return principalsResult.stream().map( principal -> this.principalToJson( principal, json.getResolveMemberships() ) ).collect(
            Collectors.toList() );
    }

    @GET
    @Path("principals/{key:.+}")
    public PrincipalJson getPrincipalByKey( @PathParam("key") final String keyParam,
                                            @QueryParam("memberships") final String resolveMembershipsParam )
    {
        final boolean resolveMemberships = "true".equals( resolveMembershipsParam );
        final PrincipalKey principalKey = PrincipalKey.from( keyParam );
        final Optional<? extends Principal> principalResult = securityService.getPrincipal( principalKey );

        if ( !principalResult.isPresent() )
        {
            throw JaxRsExceptions.notFound( String.format( "Principal [%s] was not found", keyParam ) );
        }

        final Principal principal = principalResult.get();

        return this.principalToJson( principal, resolveMemberships );
    }

    @GET
    @Path("principals/emailAvailable")
    public EmailAvailabilityJson isEmailAvailable( @QueryParam("userStoreKey") final String userStoreKeyParam,
                                                   @QueryParam("email") final String email )
    {
        if ( isBlank( email ) )
        {
            throw new WebApplicationException( "Expected email parameter" );
        }
        final UserStoreKey userStoreKey = isBlank( userStoreKeyParam ) ? UserStoreKey.system() : UserStoreKey.from( userStoreKeyParam );
        final PrincipalQuery query = PrincipalQuery.create().email( email ).userStore( userStoreKey ).build();
        final PrincipalQueryResult queryResult = securityService.query( query );
        return new EmailAvailabilityJson( queryResult.isEmpty() );
    }

    @POST
    @Path("principals/createUser")
    public UserJson createUser( final CreateUserJson params )
    {
        if ( StringUtils.isEmpty( params.password ) )
        {
            throw new WebApplicationException( "Password has not been set." );
        }

        final User user = this.securityService.createUser( params.toCreateUserParams() );
        final PrincipalKey userKey = user.getKey();

        this.securityService.setPassword( userKey, params.password );

        for ( final PrincipalKey membershipToAdd : params.toMembershipKeys() )
        {
            final PrincipalRelationship rel = PrincipalRelationship.from( membershipToAdd ).to( userKey );
            this.securityService.addRelationship( rel );
        }

        final Principals memberships = this.securityService.getPrincipals( this.securityService.getMemberships( userKey ) );
        return new UserJson( user, memberships );
    }

    @POST
    @Path("principals/createGroup")
    public GroupJson createGroup( final CreateGroupJson params )
    {
        final Group group = securityService.createGroup( params.toCreateGroupParams() );
        final PrincipalKey groupKey = group.getKey();
        final PrincipalKeys members = params.toMemberKeys();

        for ( final PrincipalKey member : members )
        {
            final PrincipalRelationship rel = PrincipalRelationship.from( groupKey ).to( member );
            securityService.addRelationship( rel );
        }

        for ( final PrincipalKey membershipToAdd : params.toMembershipKeys() )
        {
            final PrincipalRelationship rel = PrincipalRelationship.from( membershipToAdd ).to( groupKey );
            this.securityService.addRelationship( rel );
        }

        final Principals memberships = this.securityService.getPrincipals( this.securityService.getMemberships( groupKey ) );

        return new GroupJson( group, members, memberships );
    }

    @POST
    @Path("principals/createRole")
    public RoleJson createRole( final CreateRoleJson params )
    {
        final Role role = securityService.createRole( params.toCreateRoleParams() );
        final PrincipalKey roleKey = role.getKey();
        final PrincipalKeys members = params.toMemberKeys();

        for ( final PrincipalKey member : members )
        {
            final PrincipalRelationship rel = PrincipalRelationship.from( roleKey ).to( member );
            securityService.addRelationship( rel );
        }

        return new RoleJson( role, members );
    }

    @POST
    @Path("principals/updateUser")
    public UserJson updateUser( final UpdateUserJson params )
    {
        final User user = securityService.updateUser( params.getUpdateUserParams() );

        final PrincipalKey userKey = user.getKey();

        updateMemberships( userKey, params.getAddMemberships(), params.getRemoveMemberships() );

        final Principals memberships = securityService.getPrincipals( securityService.getMemberships( userKey ) );
        return new UserJson( user, memberships );
    }

    @POST
    @Path("principals/setPassword")
    public UserJson setPassword( final UpdatePasswordJson params )
    {
        final PrincipalKey userKey = params.getUserKey();

        if ( StringUtils.isEmpty( params.getPassword() ) )
        {
            throw new WebApplicationException( "Password has not been set." );
        }

        final User user = securityService.setPassword( userKey, params.getPassword() );
        return new UserJson( user );
    }

    @POST
    @Path("principals/updateGroup")
    public GroupJson updateGroup( final UpdateGroupJson params )
    {
        final Group group = securityService.updateGroup( params.getUpdateGroupParams() );
        final PrincipalKey groupKey = group.getKey();

        updateMembers( groupKey, params.getAddMembers(), params.getRemoveMembers() );

        updateMemberships( groupKey, params.getAddMemberships(), params.getRemoveMemberships() );

        final Principals memberships = securityService.getPrincipals( securityService.getMemberships( groupKey ) );

        final PrincipalKeys groupMembers = getMembers( groupKey );
        return new GroupJson( group, groupMembers, memberships );
    }

    @POST
    @Path("principals/updateRole")
    public RoleJson updateRole( final UpdateRoleJson params )
    {
        final Role role = securityService.updateRole( params.getUpdateRoleParams() );
        final PrincipalKey roleKey = role.getKey();

        updateMembers( roleKey, params.getAddMembers(), params.getRemoveMembers() );

        final PrincipalKeys roleMembers = getMembers( roleKey );
        return new RoleJson( role, roleMembers );
    }

    @POST
    @Path("principals/delete")
    public DeletePrincipalsResultJson deletePrincipals( final DeletePrincipalJson principalKeysParam )
    {
        final DeletePrincipalsResultJson resultsJson = new DeletePrincipalsResultJson();
        principalKeysParam.getKeys().stream().map( PrincipalKey::from ).forEach( ( principalKey ) -> {
            try
            {
                securityService.deletePrincipal( principalKey );
                resultsJson.add( DeletePrincipalResultJson.success( principalKey ) );
            }
            catch ( Exception e )
            {
                resultsJson.add( DeletePrincipalResultJson.failure( principalKey, e.getMessage() ) );
            }
        } );
        return resultsJson;
    }

    private void updateMembers( final PrincipalKey target, PrincipalKeys membersToAdd, PrincipalKeys membersToRemove )
    {
        for ( PrincipalKey memberToAdd : membersToAdd )
        {
            final PrincipalRelationship rel = PrincipalRelationship.from( target ).to( memberToAdd );
            securityService.addRelationship( rel );
        }

        for ( PrincipalKey memberToRemove : membersToRemove )
        {
            final PrincipalRelationship rel = PrincipalRelationship.from( target ).to( memberToRemove );
            securityService.removeRelationship( rel );
        }
    }

    private void updateMemberships( final PrincipalKey source, PrincipalKeys membershipsToAdd, PrincipalKeys membershipsToRemove )
    {
        for ( PrincipalKey membershipToAdd : membershipsToAdd )
        {
            final PrincipalRelationship rel = PrincipalRelationship.from( membershipToAdd ).to( source );
            securityService.addRelationship( rel );
        }

        for ( PrincipalKey membershipToRemove : membershipsToRemove )
        {
            final PrincipalRelationship rel = PrincipalRelationship.from( membershipToRemove ).to( source );
            securityService.removeRelationship( rel );
        }
    }

    private PrincipalKeys getMembers( final PrincipalKey principal )
    {
        final PrincipalRelationships relationships = this.securityService.getRelationships( principal );
        final List<PrincipalKey> members = relationships.stream().map( PrincipalRelationship::getTo ).collect( toList() );
        return PrincipalKeys.from( members );
    }

    private AuthDescriptorMode retrieveIdProviderMode( UserStore userStore )
    {
        final AuthConfig authConfig = userStore.getAuthConfig();
        final ApplicationKey idProviderKey = authConfig == null ? null : authConfig.getApplicationKey();
        final AuthDescriptor idProvider = idProviderKey == null ? null : authDescriptorService.getDescriptor( idProviderKey );
        return idProvider == null ? null : idProvider.getMode();
    }


    @Reference
    public void setSecurityService( final SecurityService securityService )
    {
        this.securityService = securityService;
    }

    @Reference
    public void setAuthDescriptorService( final AuthDescriptorService authDescriptorService )
    {
        this.authDescriptorService = authDescriptorService;
    }

    @Reference
    public void setAuthControllerService( final AuthControllerService authControllerService )
    {
        this.authControllerService = authControllerService;
    }
}
