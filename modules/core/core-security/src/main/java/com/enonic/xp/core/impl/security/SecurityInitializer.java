package com.enonic.xp.core.impl.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.init.ExternalInitializer;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.security.CreateIdProviderParams;
import com.enonic.xp.security.CreateRoleParams;
import com.enonic.xp.security.CreateUserParams;
import com.enonic.xp.security.IdProviderConfig;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalRelationship;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityConstants;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.security.User;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.IdProviderAccessControlEntry;
import com.enonic.xp.security.acl.IdProviderAccessControlList;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static com.enonic.xp.security.acl.IdProviderAccess.ADMINISTRATOR;
import static com.enonic.xp.security.acl.IdProviderAccess.READ;

final class SecurityInitializer
    extends ExternalInitializer
{
    public static final PrincipalKey SUPER_USER = PrincipalKey.ofSuperUser();

    private static final String SYSTEM_USER_STORE_DISPLAY_NAME = "System User Store";

    private static final String ADMIN_USER_CREATION_PROPERTY_KEY = "xp.init.adminUserCreation";

    private static final Logger LOG = LoggerFactory.getLogger( SecurityInitializer.class );

    private static final ApplicationKey SYSTEM_ID_PROVIDER_KEY = ApplicationKey.from( "com.enonic.xp.app.standardidprovider" );

    static final IdProviderAccessControlList DEFAULT_USER_STORE_ACL =
        IdProviderAccessControlList.of( IdProviderAccessControlEntry.create().principal( RoleKeys.ADMIN ).access( ADMINISTRATOR ).build(),
                                        IdProviderAccessControlEntry.create().principal( RoleKeys.USER_MANAGER_ADMIN ).access(
                                           ADMINISTRATOR ).build(),
                                        IdProviderAccessControlEntry.create().principal( RoleKeys.AUTHENTICATED ).access( READ ).build() );

    private final SecurityService securityService;

    private final NodeService nodeService;

    private SecurityInitializer( final Builder builder )
    {
        super( builder );
        this.securityService = builder.securityService;
        this.nodeService = builder.nodeService;
    }

    @Override
    public final void doInitialize()
    {
        createAdminContext().runWith( () -> {

            initializeIdProviderParentFolder();
            initializeRoleFolder();
            initializeSystemIdProvider();

            createRoles();
            createUsers();
        } );
    }

    @Override
    public boolean isInitialized()
    {
        return createAdminContext().
            callWith( () -> this.nodeService.getByPath( SUPER_USER.toPath() ) != null );
    }

    @Override
    protected String getInitializationSubject()
    {
        return "System-repo [security] layout";
    }

    private Context createAdminContext()
    {
        final User admin = User.create().
            key( SUPER_USER ).
            login( SUPER_USER.getId() ).
            build();
        final AuthenticationInfo authInfo = AuthenticationInfo.create().
            principals( RoleKeys.ADMIN ).
            user( admin ).
            build();
        return ContextBuilder.from( SecurityConstants.CONTEXT_SECURITY ).
            authInfo( authInfo ).
            build();
    }

    private void initializeIdProviderParentFolder()
    {
        final NodePath idProviderParentNodePath = IdProviderNodeTranslator.getIdProvidersParentPath();
        LOG.info( "Initializing [" + idProviderParentNodePath.toString() + "] folder" );

        final AccessControlEntry userManagerFullAccess = AccessControlEntry.create().
            allowAll().
            principal( RoleKeys.USER_MANAGER_ADMIN ).
            build();

        final ChildOrder childOrder = ChildOrder.create().
            add( FieldOrderExpr.create( NodeIndexPath.NAME, OrderExpr.Direction.ASC ) ).
            build();

        nodeService.create( CreateNodeParams.create().
            parent( idProviderParentNodePath.getParentPath() ).
            name( idProviderParentNodePath.getLastElement().toString() ).
            permissions( AccessControlList.create().
                addAll( SystemConstants.SYSTEM_REPO_DEFAULT_ACL.getEntries() ).
                add( userManagerFullAccess ).
                build() ).
            inheritPermissions( false ).
            childOrder( childOrder ).
            build() );
    }


    private void initializeRoleFolder()
    {
        final NodePath rolesNodePath = IdProviderNodeTranslator.getRolesNodePath();
        LOG.info( "Initializing [" + rolesNodePath.toString() + "] folder" );

        nodeService.create( CreateNodeParams.create().
            parent( rolesNodePath.getParentPath() ).
            name( rolesNodePath.getLastElement().toString() ).
            inheritPermissions( true ).
            build() );
    }

    private void initializeSystemIdProvider()
    {
        LOG.info( "Initializing id provider [" + IdProviderKey.system() + "]" );

        final PropertyTree idProviderConfigTree = new PropertyTree();
        if ( !"false".equalsIgnoreCase( System.getProperty( ADMIN_USER_CREATION_PROPERTY_KEY ) ) )
        {
            idProviderConfigTree.setBoolean( "adminUserCreationEnabled", true );
        }
        final IdProviderConfig idProviderConfig = IdProviderConfig.create().
            applicationKey( SYSTEM_ID_PROVIDER_KEY ).
            config( idProviderConfigTree ).
            build();

        final CreateIdProviderParams createParams = CreateIdProviderParams.create().
            key( IdProviderKey.system() ).
            displayName( SYSTEM_USER_STORE_DISPLAY_NAME ).
            idProviderConfig( idProviderConfig ).
            permissions( DEFAULT_USER_STORE_ACL ).
            build();

        this.securityService.createIdProvider( createParams );
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

        final CreateRoleParams createContentManagerExpert = CreateRoleParams.create().
            roleKey( RoleKeys.CONTENT_MANAGER_EXPERT ).
            displayName( "Content Manager Expert" ).
            build();
        addRole( createContentManagerExpert );

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
            login( SUPER_USER.getId() ).
            build();
        addUser( createSuperUser );

        addMember( RoleKeys.ADMIN, createSuperUser.getKey() );
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

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends ExternalInitializer.Builder<Builder>
    {
        private SecurityService securityService;

        private NodeService nodeService;

        public Builder setSecurityService( final SecurityService securityService )
        {
            this.securityService = securityService;
            return this;
        }

        public Builder setNodeService( final NodeService nodeService )
        {
            this.nodeService = nodeService;
            return this;
        }

        @Override
        protected void validate()
        {
            super.validate();
            Preconditions.checkNotNull( securityService );
            Preconditions.checkNotNull( nodeService );
        }

        public SecurityInitializer build()
        {
            validate();
            return new SecurityInitializer( this );
        }
    }
}

