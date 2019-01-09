package com.enonic.xp.admin.impl.rest.resource.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

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

import com.google.common.collect.Lists;

import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.admin.impl.rest.resource.security.json.CreateGroupJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.CreateIdProviderJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.CreateRoleJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.CreateUserJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.DeleteIdProviderJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.DeleteIdProviderResultJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.DeleteIdProvidersResultJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.DeletePrincipalJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.DeletePrincipalResultJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.DeletePrincipalsResultJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.EmailAvailabilityJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.FetchPrincipalsByKeysJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.FindPrincipalsResultJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.FindPrincipalsWithRolesResultJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.GroupJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.IdProviderJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.IdProvidersJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.PrincipalJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.RoleJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.SyncIdProviderJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.SyncIdProviderResultJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.SyncIdProvidersResultJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.UpdateGroupJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.UpdateIdProviderJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.UpdatePasswordJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.UpdateRoleJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.UpdateUserJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.UserJson;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.idprovider.IdProviderDescriptor;
import com.enonic.xp.idprovider.IdProviderDescriptorMode;
import com.enonic.xp.idprovider.IdProviderDescriptorService;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.jaxrs.JaxRsExceptions;
import com.enonic.xp.portal.idprovider.IdProviderControllerExecutionParams;
import com.enonic.xp.portal.idprovider.IdProviderControllerService;
import com.enonic.xp.security.Group;
import com.enonic.xp.security.IdProvider;
import com.enonic.xp.security.IdProviderConfig;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.IdProviders;
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
import com.enonic.xp.security.acl.IdProviderAccessControlList;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang.StringUtils.isBlank;


@SuppressWarnings("UnusedDeclaration")
@Path(ResourceConstants.REST_ROOT + "security")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({RoleKeys.ADMIN_LOGIN_ID, RoleKeys.ADMIN_ID})
@Component(immediate = true, property = "group=admin")
public final class SecurityResource
    implements JaxRsComponent
{
    private SecurityService securityService;

    private IdProviderDescriptorService idProviderDescriptorService;

    private IdProviderControllerService idProviderControllerService;

    @GET
    @Path("idprovider/list")
    public IdProvidersJson getIdProviders()
    {
        final IdProviders idProviders = securityService.getIdProviders();
        return new IdProvidersJson( idProviders );
    }

    @GET
    @Path("idprovider")
    public IdProviderJson getIdProvider( @QueryParam("key") final String keyParam )
    {
        if ( keyParam == null )
        {
            return null;
        }

        final IdProviderKey idProviderKey = IdProviderKey.from( keyParam );
        final IdProvider idProvider = securityService.getIdProvider( idProviderKey );
        if ( idProvider == null )
        {
            throw JaxRsExceptions.notFound( String.format( "User Store [%s] not found", keyParam ) );
        }

        final IdProviderDescriptorMode idProviderMode = retrieveIdProviderMode( idProvider );
        final IdProviderAccessControlList idProviderPermissions = securityService.getIdProviderPermissions( idProviderKey );

        final Principals principals = securityService.getPrincipals( idProviderPermissions.getAllPrincipals() );
        return new IdProviderJson( idProvider, idProviderMode, idProviderPermissions, principals );
    }

    @GET
    @Path("idprovider/default")
    public IdProviderJson getDefaultIdProvider()
    {
        final IdProvider idProvider = IdProvider.create().displayName( "" ).key( IdProviderKey.createDefault() ).build();

        final IdProviderAccessControlList idProviderPermissions = securityService.getDefaultIdProviderPermissions();

        final IdProviderDescriptorMode idProviderMode = retrieveIdProviderMode( idProvider );
        final Principals principals = securityService.getPrincipals( idProviderPermissions.getAllPrincipals() );
        return new IdProviderJson( idProvider, idProviderMode, idProviderPermissions, principals );
    }

    @POST
    @Path("idprovider/create")
    public IdProviderJson createIdProvider( final CreateIdProviderJson params )
    {
        final IdProvider idProvider = securityService.createIdProvider( params.getCreateIdProviderParams() );
        final IdProviderAccessControlList permissions = securityService.getIdProviderPermissions( idProvider.getKey() );

        final IdProviderDescriptorMode idProviderMode = retrieveIdProviderMode( idProvider );
        final Principals principals = securityService.getPrincipals( permissions.getAllPrincipals() );
        return new IdProviderJson( idProvider, idProviderMode, permissions, principals );
    }

    @POST
    @Path("idprovider/update")
    public IdProviderJson updateIdProvider( final UpdateIdProviderJson params )
    {
        final IdProvider idProvider = securityService.updateIdProvider( params.getUpdateIdProviderParams() );
        final IdProviderAccessControlList permissions = securityService.getIdProviderPermissions( idProvider.getKey() );

        final IdProviderDescriptorMode idProviderMode = retrieveIdProviderMode( idProvider );
        final Principals principals = securityService.getPrincipals( permissions.getAllPrincipals() );
        return new IdProviderJson( idProvider, idProviderMode, permissions, principals );
    }

    @POST
    @Path("idprovider/delete")
    public DeleteIdProvidersResultJson deleteIdProvider( final DeleteIdProviderJson params )
    {
        final DeleteIdProvidersResultJson resultsJson = new DeleteIdProvidersResultJson();
        params.getKeys().stream().map( IdProviderKey::from ).forEach( ( idProviderKey ) -> {
            try
            {
                securityService.deleteIdProvider( idProviderKey );
                resultsJson.add( DeleteIdProviderResultJson.success( idProviderKey ) );
            }
            catch ( Exception e )
            {
                resultsJson.add( DeleteIdProviderResultJson.failure( idProviderKey, e.getMessage() ) );
            }
        } );
        return resultsJson;
    }

    @POST
    @Path("idprovider/sync")
    public SyncIdProvidersResultJson synchIdProvider( final SyncIdProviderJson params, @Context HttpServletRequest httpRequest )
    {
        final SyncIdProvidersResultJson resultsJson = new SyncIdProvidersResultJson();
        params.getKeys().stream().map( IdProviderKey::from ).forEach( ( idProviderKey ) -> {
            try
            {
                final IdProviderControllerExecutionParams syncParams = IdProviderControllerExecutionParams.create().
                    idProviderKey( idProviderKey ).
                    functionName( "sync" ).
                    servletRequest( httpRequest ).
                    build();
                idProviderControllerService.execute( syncParams );
                resultsJson.add( SyncIdProviderResultJson.success( idProviderKey ) );
            }
            catch ( Exception e )
            {
                resultsJson.add( SyncIdProviderResultJson.failure( idProviderKey, e.getMessage() ) );
            }
        } );
        return resultsJson;
    }

    private List<PrincipalType> parsePrincipalTypes( final String types )
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
        return principalTypes;
    }

    @GET
    @Path("principals")
    public FindPrincipalsResultJson findPrincipals( @QueryParam("types") final String types, @QueryParam("query") final String query,
                                                    @QueryParam("idProviderKey") final String storeKey,
                                                    @QueryParam("from") final Integer from, @QueryParam("size") final Integer size )
    {

        final List<PrincipalType> principalTypes = parsePrincipalTypes( types );

        final PrincipalQuery.Builder principalQuery = PrincipalQuery.create().
            getAll().
            includeTypes( principalTypes ).
            searchText( query );

        if ( StringUtils.isNotEmpty( storeKey ) )
        {
            principalQuery.idProvider( IdProviderKey.from( storeKey ) );
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

    @GET
    @Path("principalsWithRoles")
    public FindPrincipalsWithRolesResultJson findPrincipals( @QueryParam("types") final String types,
                                                             @QueryParam("roles") final String roles,
                                                             @QueryParam("query") final String query,
                                                             @QueryParam("idProviderKey") final String storeKey,
                                                             @QueryParam("from") final Integer from,
                                                             @QueryParam("size") final Integer size )
    {
        final List<PrincipalType> principalTypes = parsePrincipalTypes( types );

        final PrincipalQuery.Builder principalQuery = PrincipalQuery.create().
            getAll().
            includeTypes( principalTypes ).
            searchText( query );

        if ( StringUtils.isNotEmpty( storeKey ) )
        {
            principalQuery.idProvider( IdProviderKey.from( storeKey ) );
        }

        FetchPrincipalsWithRolesResult fwResult =
            fetchPrincipalsWithRoles( principalQuery, roles, from == null ? 0 : from, size == null ? PrincipalQuery.DEFAULT_SIZE : size );

        return new FindPrincipalsWithRolesResultJson( Principals.from( fwResult.getPrincipals() ), fwResult.getUnfilteredSize(),
                                                      fwResult.hasMore() );
    }

    private FetchPrincipalsWithRolesResult fetchPrincipalsWithRoles( final PrincipalQuery.Builder principalQuery, final String roles,
                                                                     final int from, final int size )
    {
        final List<Principal> resultingPrincipals = Lists.newArrayList();
        int totalCount;
        int fromTemp = from;
        final AtomicInteger unfilteredCount = new AtomicInteger( 0 );
        final AtomicInteger filteredCount = new AtomicInteger( 0 );
        final PrincipalKeys roleKeys = roles != null ? PrincipalKeys.from( roles.split( "," ) ) : null;
        principalQuery.size( size );

        do
        {
            principalQuery.from( fromTemp );
            final PrincipalQueryResult pqResult = securityService.query( principalQuery.build() );
            totalCount = pqResult.getTotalSize();

            if ( roleKeys != null )
            {
                Predicate<? super Principal> rolesFilter = p -> {
                    if ( filteredCount.get() < size )
                    {
                        unfilteredCount.incrementAndGet();
                        final boolean satisfies = securityService.getAllMemberships( p.getKey() ).stream().anyMatch( roleKeys::contains );
                        if ( satisfies )
                        {
                            filteredCount.incrementAndGet();
                        }
                        return satisfies;
                    }
                    else
                    {
                        return false;
                    }
                };
                resultingPrincipals.addAll( pqResult.getPrincipals().stream().filter( rolesFilter ).collect( toList() ) );
            }
            else
            {
                unfilteredCount.addAndGet( pqResult.getPrincipals().getSize() );
                resultingPrincipals.addAll( pqResult.getPrincipals().getList() );
            }
            fromTemp += size;
        }
        while ( filteredCount.get() < size && ( from + unfilteredCount.get() ) < totalCount );

        return new FetchPrincipalsWithRolesResult( resultingPrincipals, unfilteredCount.get(),
                                                   ( unfilteredCount.get() + from ) < totalCount );
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
        final PrincipalKeys principalKeys = PrincipalKeys.from( json.getKeys().stream().map( PrincipalKey::from ).collect( toList() ) );

        final Principals principalsResult = securityService.getPrincipals( principalKeys );

        return principalsResult.stream().map( principal -> this.principalToJson( principal, json.getResolveMemberships() ) ).collect(
            toList() );
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
    public EmailAvailabilityJson isEmailAvailable( @QueryParam("idProviderKey") final String idProviderKeyParam,
                                                   @QueryParam("email") final String email )
    {
        if ( isBlank( email ) )
        {
            throw new WebApplicationException( "Expected email parameter" );
        }
        final IdProviderKey idProviderKey =
            isBlank( idProviderKeyParam ) ? IdProviderKey.system() : IdProviderKey.from( idProviderKeyParam );
        final PrincipalQuery query = PrincipalQuery.create().email( email ).idProvider( idProviderKey ).build();
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

    private IdProviderDescriptorMode retrieveIdProviderMode( IdProvider idProvider )
    {
        final IdProviderConfig idProviderConfig = idProvider.getIdProviderConfig();
        final ApplicationKey idProviderKey = idProviderConfig == null ? null : idProviderConfig.getApplicationKey();
        final IdProviderDescriptor idProviderDescriptor =
            idProviderKey == null ? null : idProviderDescriptorService.getDescriptor( idProviderKey );
        return idProviderDescriptor == null ? null : idProviderDescriptor.getMode();
    }


    @Reference
    public void setSecurityService( final SecurityService securityService )
    {
        this.securityService = securityService;
    }

    @Reference
    public void setIdProviderDescriptorService( final IdProviderDescriptorService idProviderDescriptorService )
    {
        this.idProviderDescriptorService = idProviderDescriptorService;
    }

    @Reference
    public void setIdProviderControllerService( final IdProviderControllerService idProviderControllerService )
    {
        this.idProviderControllerService = idProviderControllerService;
    }

    private class FetchPrincipalsWithRolesResult
    {
        private List<Principal> principals;

        private int unfilteredSize;

        private boolean hasMore;

        FetchPrincipalsWithRolesResult( final List<Principal> principals, final int unfilteredSize, final boolean hasMore )
        {
            this.principals = principals;
            this.unfilteredSize = unfilteredSize;
            this.hasMore = hasMore;
        }

        public List<Principal> getPrincipals()
        {
            return principals;
        }

        public int getUnfilteredSize()
        {
            return unfilteredSize;
        }

        public boolean hasMore()
        {
            return hasMore;
        }
    }
}
