package com.enonic.xp.core.impl.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.CreateRootNodeParams;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.security.CreateRoleParams;
import com.enonic.xp.security.CreateUserParams;
import com.enonic.xp.security.CreateUserStoreParams;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalRelationship;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.security.acl.UserStoreAccessControlEntry;
import com.enonic.xp.security.acl.UserStoreAccessControlList;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static com.enonic.xp.security.SystemConstants.CONTEXT_SECURITY;
import static com.enonic.xp.security.acl.UserStoreAccess.ADMINISTRATOR;
import static com.enonic.xp.security.acl.UserStoreAccess.READ;

public final class SecurityInitializer
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
        final String branchName = CONTEXT_SECURITY.getBranch().getName();

        runAsAdmin( () -> {

            if ( isBranchInitialized() )
            {
                LOG.info( "Branch [" + branchName + "] already initialized" );
                return;
            }

            LOG.info( "Initializing [" + branchName + "] branch..." );

            initializeUsersBranch();
            initializeSystemUserStore();

            createRoles();
            createUsers();

            LOG.info( "[" + branchName + "] branch successfully initialized" );
        } );
    }

    private void runAsAdmin( Runnable runnable )
    {
        final User admin = User.create().key( SUPER_USER ).login( "su" ).build();
        final AuthenticationInfo authInfo = AuthenticationInfo.create().principals( RoleKeys.ADMIN ).user( admin ).build();
        ContextBuilder.from( CONTEXT_SECURITY ).authInfo( authInfo ).build().runWith( runnable );
    }

    private boolean isBranchInitialized()
    {
        return this.nodeService.getRoot() != null;
    }

    private void initializeUsersBranch()
    {
        final AccessControlEntry adminFullAccess = AccessControlEntry.create().
            allowAll().
            principal( RoleKeys.ADMIN ).
            build();
        final AccessControlEntry authenticatedRead = AccessControlEntry.create().
            allow( Permission.READ ).
            principal( RoleKeys.AUTHENTICATED ).
            build();

        this.nodeService.createRootNode( CreateRootNodeParams.create().
            childOrder( ChildOrder.defaultOrder() ).
            permissions( AccessControlList.of( adminFullAccess, authenticatedRead ) ).

            build() );

        initializeRoleFolder();
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

        final UserStoreAccessControlList permissions =
            UserStoreAccessControlList.of( UserStoreAccessControlEntry.create().principal( RoleKeys.ADMIN ).access( ADMINISTRATOR ).build(),
                                           UserStoreAccessControlEntry.create().principal( RoleKeys.AUTHENTICATED ).access(
                                               READ ).build() );

        final CreateUserStoreParams createParams = CreateUserStoreParams.create().
            key( UserStoreKey.system() ).
            displayName( SYSTEM_USER_STORE_DISPLAY_NAME ).
            permissions( permissions ).
            build();
        this.securityService.createUserStore( createParams );
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
}

