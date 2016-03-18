package com.enonic.xp.core.impl.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.security.AuthConfig;
import com.enonic.xp.security.CreatePathGuardParams;
import com.enonic.xp.security.CreateRoleParams;
import com.enonic.xp.security.CreateUserParams;
import com.enonic.xp.security.CreateUserStoreParams;
import com.enonic.xp.security.PathGuardKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalRelationship;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityConstants;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.acl.UserStoreAccessControlEntry;
import com.enonic.xp.security.acl.UserStoreAccessControlList;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.system.SystemRepoInitializer;

import static com.enonic.xp.security.acl.UserStoreAccess.ADMINISTRATOR;
import static com.enonic.xp.security.acl.UserStoreAccess.READ;

final class SecurityInitializer
{

    static final String SYSTEM_USER_STORE_DISPLAY_NAME = "System User Store";

    private static final Logger LOG = LoggerFactory.getLogger( SecurityInitializer.class );

    private static final PrincipalKey SUPER_USER = PrincipalKey.ofUser( UserStoreKey.system(), "su" );

    private final SecurityService securityService;

    private final NodeService nodeService;

    public SecurityInitializer( final SecurityService securityService, final NodeService nodeService )
    {
        this.securityService = securityService;
        this.nodeService = nodeService;
    }

    public final void initialize()
    {
        runAsAdmin( () -> {

            new SystemRepoInitializer( this.nodeService ).initialize();

            if ( isInitialized() )
            {
                LOG.info( "System-repo [security] layout already initialized" );
                return;
            }

            LOG.info( "Initializing system-repo [security] layout" );

            initializeUserStoreParentFolder();
            initializeRoleFolder();
            initializePathGuardFolder();
            initializeSystemUserStore();

            createRoles();
            createUsers();
            createPathGuards();

            LOG.info( "System-repo [security] layout successfully initialized" );

        } );
    }

    private boolean isInitialized()
    {
        return this.nodeService.getByPath( UserStoreNodeTranslator.getRolesNodePath() ) != null &&
            this.nodeService.getByPath( UserStoreNodeTranslator.getUserStoresParentPath() ) != null;
    }

    private void runAsAdmin( Runnable runnable )
    {
        final User admin = User.create().key( SUPER_USER ).login( "su" ).build();
        final AuthenticationInfo authInfo = AuthenticationInfo.create().principals( RoleKeys.ADMIN ).user( admin ).build();
        ContextBuilder.from( SecurityConstants.CONTEXT_SECURITY ).authInfo( authInfo ).build().runWith( runnable );
    }

    private void initializeUserStoreParentFolder()
    {
        final NodePath userStoreParentNodePath = UserStoreNodeTranslator.getUserStoresParentPath();
        LOG.info( "Initializing [" + userStoreParentNodePath.toString() + "] folder" );

        nodeService.create( CreateNodeParams.create().
            parent( userStoreParentNodePath.getParentPath() ).
            name( userStoreParentNodePath.getLastElement().toString() ).
            inheritPermissions( true ).
            build() );
    }


    private void initializeRoleFolder()
    {
        final NodePath rolesNodePath = UserStoreNodeTranslator.getRolesNodePath();
        LOG.info( "Initializing [" + rolesNodePath.toString() + "] folder" );

        nodeService.create( CreateNodeParams.create().
            parent( rolesNodePath.getParentPath() ).
            name( rolesNodePath.getLastElement().toString() ).
            inheritPermissions( true ).
            build() );
    }

    private void initializeSystemUserStore()
    {
        LOG.info( "Initializing user store [" + UserStoreKey.system() + "]" );

        final AuthConfig authConfig = AuthConfig.create().
            applicationKey( ApplicationKey.from( "com.enonic.xp.app.login" ) ).
            build();

        final UserStoreAccessControlList permissions =
            UserStoreAccessControlList.of( UserStoreAccessControlEntry.create().principal( RoleKeys.ADMIN ).access( ADMINISTRATOR ).build(),
                                           UserStoreAccessControlEntry.create().principal( RoleKeys.AUTHENTICATED ).access(
                                               READ ).build() );

        final CreateUserStoreParams createParams = CreateUserStoreParams.create().
            key( UserStoreKey.system() ).
            displayName( SYSTEM_USER_STORE_DISPLAY_NAME ).
            authConfig( authConfig ).
            permissions( permissions ).
            build();
        this.securityService.createUserStore( createParams );
    }

    private void initializePathGuardFolder()
    {
        final NodePath pathGuardsNodePath = PathGuardNodeTranslator.getPathGuardsNodePath();
        LOG.info( "Initializing [" + pathGuardsNodePath.toString() + "] folder" );

        nodeService.create( CreateNodeParams.create().
            parent( pathGuardsNodePath.getParentPath() ).
            name( pathGuardsNodePath.getLastElement().toString() ).
            inheritPermissions( true ).
            build() );
    }

    private void createRoles()
    {
        final CreateRoleParams createAdministratorRole = CreateRoleParams.create().
            roleKey( RoleKeys.ADMIN ).
            displayName( "Administrator" ).
            build();
        addRole( createAdministratorRole );

        final CreateRoleParams createAuthenticatedRole = CreateRoleParams.create().
            roleKey( RoleKeys.AUTHENTICATED ).
            displayName( "Authenticated" ).
            build();
        addRole( createAuthenticatedRole );

        final CreateRoleParams createEveryoneRole = CreateRoleParams.create().
            roleKey( RoleKeys.EVERYONE ).
            displayName( "Everyone" ).
            build();
        addRole( createEveryoneRole );

        final CreateRoleParams createAdminLoginRole = CreateRoleParams.create().
            roleKey( RoleKeys.ADMIN_LOGIN ).
            displayName( "Administration Console Login" ).
            build();
        addRole( createAdminLoginRole );

        final CreateRoleParams createUserManagerAppRole = CreateRoleParams.create().
            roleKey( RoleKeys.USER_MANAGER_APP ).
            displayName( "Users App" ).
            build();
        addRole( createUserManagerAppRole );

        final CreateRoleParams createUserManager = CreateRoleParams.create().
            roleKey( RoleKeys.USER_MANAGER_ADMIN ).
            displayName( "Users Administrator" ).
            build();
        addRole( createUserManager );

        final CreateRoleParams createContentManagerAppRole = CreateRoleParams.create().
            roleKey( RoleKeys.CONTENT_MANAGER_APP ).
            displayName( "Content Manager App" ).
            build();
        addRole( createContentManagerAppRole );

        final CreateRoleParams createContentManager = CreateRoleParams.create().
            roleKey( RoleKeys.CONTENT_MANAGER_ADMIN ).
            displayName( "Content Manager Administrator" ).
            build();
        addRole( createContentManager );
    }

    private void createUsers()
    {
        final CreateUserParams createAnonymousUser = CreateUserParams.create().
            userKey( PrincipalKey.ofAnonymous() ).
            displayName( "Anonymous User" ).
            login( "anonymous" ).
            build();
        addUser( createAnonymousUser );

        final CreateUserParams createSuperUser = CreateUserParams.create().
            userKey( SUPER_USER ).
            displayName( "Super User" ).
            login( "su" ).
            password( "password" ).
            build();
        addUser( createSuperUser );

        addMember( RoleKeys.ADMIN, createSuperUser.getKey() );
        addMember( RoleKeys.ADMIN_LOGIN, createSuperUser.getKey() );
    }

    private void createPathGuards()
    {
        final CreatePathGuardParams createPathGuardParams = CreatePathGuardParams.create().
            key( PathGuardKey.admin() ).
            displayName( "Admin guard" ).
            description( "Admin guard" ).
            userStoreKey( UserStoreKey.system() ).
            passive( true ).
            addPaths( "/admin" ).
            build();
        addPathGuard( createPathGuardParams );
    }

    private void addUser( final CreateUserParams createUser )
    {
        try
        {
            if ( !securityService.getUser( createUser.getKey() ).isPresent() )
            {
                securityService.createUser( createUser );
                LOG.info( "User created: " + createUser.getKey().toString() );
            }
        }
        catch ( final Exception t )
        {
            LOG.error( "Unable to initialize user: " + createUser.getKey().toString(), t );
        }
    }

    private void addRole( final CreateRoleParams createRoleParams )
    {
        try
        {
            if ( !securityService.getRole( createRoleParams.getKey() ).isPresent() )
            {
                securityService.createRole( createRoleParams );
                LOG.info( "Role created: " + createRoleParams.getKey().toString() );
            }
        }
        catch ( final Exception t )
        {
            LOG.error( "Unable to initialize role: " + createRoleParams.getKey().toString(), t );
        }
    }

    private void addMember( final PrincipalKey container, final PrincipalKey member )
    {
        try
        {
            securityService.addRelationship( PrincipalRelationship.from( container ).to( member ) );
            LOG.info( "Added " + member + " as member of " + container );
        }
        catch ( final Exception t )
        {
            LOG.error( "Unable to add member: " + container + " -> " + member, t );
        }
    }

    private void addPathGuard( final CreatePathGuardParams createPathGuardParams )
    {
        try
        {
            if ( !securityService.getPathGuardByKey( createPathGuardParams.getKey() ).isPresent() )
            {
                securityService.createPathGuard( createPathGuardParams );
                LOG.info( "Path guard created: " + createPathGuardParams.getKey().toString() );
            }
        }
        catch ( final Exception t )
        {
            LOG.error( "Unable to initialize path guard: " + createPathGuardParams.getKey().toString(), t );
        }
    }
}

