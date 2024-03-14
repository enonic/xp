package com.enonic.xp.core.impl.security;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

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
import com.enonic.xp.node.NodeName;
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

public final class SecurityInitializer
    extends ExternalInitializer
{
    private static final Logger LOG = LoggerFactory.getLogger( SecurityInitializer.class );

    private static final PrincipalKey SUPER_USER = PrincipalKey.ofSuperUser();

    private static final NodePath KEYS_PATH = new NodePath( NodePath.ROOT, NodeName.from( "keys" ) );

    /**
     * Generic Key suitable to do HMAC SHA512 hashing.
     * Should be used for low severity security purposes only, like anti open-redirect attacks.
     */
    private static final NodePath GENERIC_KEY_PATH = new NodePath( KEYS_PATH, NodeName.from( "generic-hmac-sha512" ) );

    private static final NodePath IDENTITY_PATH = IdProviderNodeTranslator.ID_PROVIDERS_PARENT_PATH;

    private static final NodePath ROLES_PATH = new NodePath( IDENTITY_PATH, NodeName.from( PrincipalKey.ROLES_NODE_NAME ) );

    private static final String ADMIN_USER_CREATION_PROPERTY_KEY = "xp.init.adminUserCreation";

    private static final ApplicationKey SYSTEM_ID_PROVIDER_KEY = IdProviderNodeTranslator.SYSTEM_ID_PROVIDER_KEY;

    static final IdProviderAccessControlList DEFAULT_ID_PROVIDER_ACL =
        IdProviderAccessControlList.of( IdProviderAccessControlEntry.create().principal( RoleKeys.ADMIN ).access( ADMINISTRATOR ).build(),
                                        IdProviderAccessControlEntry.create()
                                            .principal( RoleKeys.USER_MANAGER_ADMIN )
                                            .access( ADMINISTRATOR )
                                            .build(),
                                        IdProviderAccessControlEntry.create().principal( RoleKeys.AUTHENTICATED ).access( READ ).build() );

    private static final List<CreateRoleParams> ROLES_TO_CREATE =
        List.of( CreateRoleParams.create().roleKey( RoleKeys.ADMIN ).displayName( "Administrator" ).build(),
                 CreateRoleParams.create().roleKey( RoleKeys.AUTHENTICATED ).displayName( "Authenticated" ).build(),
                 CreateRoleParams.create().roleKey( RoleKeys.EVERYONE ).displayName( "Everyone" ).build(),
                 CreateRoleParams.create().roleKey( RoleKeys.ADMIN_LOGIN ).displayName( "Administration Console Login" ).build(),
                 CreateRoleParams.create().roleKey( RoleKeys.USER_MANAGER_APP ).displayName( "Users App" ).build(),
                 CreateRoleParams.create().roleKey( RoleKeys.USER_MANAGER_ADMIN ).displayName( "Users Administrator" ).build(),
                 CreateRoleParams.create().roleKey( RoleKeys.CONTENT_MANAGER_APP ).displayName( "Content Manager App" ).build(),
                 CreateRoleParams.create().roleKey( RoleKeys.CONTENT_MANAGER_EXPERT ).displayName( "Content Manager Expert" ).build(),
                 CreateRoleParams.create().roleKey( RoleKeys.CONTENT_MANAGER_ADMIN ).displayName( "Content Manager Administrator" ).build(),
                 CreateRoleParams.create().roleKey( RoleKeys.AUDIT_LOG ).displayName( "Audit Log" ).build() );

    private static final List<CreateUserParams> USERS_TO_CREATE =
        List.of( CreateUserParams.create().userKey( PrincipalKey.ofAnonymous() ).displayName( "Anonymous" ).login( "anonymous" ).build(),
                 CreateUserParams.create().userKey( SUPER_USER ).displayName( "Super User" ).login( SUPER_USER.getId() ).build() );

    private final SecurityService securityService;

    private final NodeService nodeService;

    private SecurityInitializer( final Builder builder )
    {
        super( builder );
        this.securityService = builder.securityService;
        this.nodeService = builder.nodeService;
    }

    @Override
    public void doInitialize()
    {
        createAdminContext().runWith( () -> {

            initializeIdProviderParentFolder();
            initializeRoles();

            initializeSystemIdProvider();
            initializeUsers();

            initializeKeys();
        } );
    }

    @Override
    public boolean isInitialized()
    {
        return createAdminContext().callWith(
            () -> pathInitialized( IDENTITY_PATH ) && rolesInitialized() && systemIdProviderInitialized() &&
                usersInitialized() && keysInitialized() );
    }

    @Override
    protected String getInitializationSubject()
    {
        return "System-repo [security] layout";
    }

    private Context createAdminContext()
    {
        final User admin = User.create().key( SUPER_USER ).login( SUPER_USER.getId() ).build();
        final AuthenticationInfo authInfo = AuthenticationInfo.create().principals( RoleKeys.ADMIN ).user( admin ).build();
        return ContextBuilder.create()
            .branch( SecurityConstants.BRANCH_SECURITY )
            .repositoryId( SystemConstants.SYSTEM_REPO_ID )
            .authInfo( authInfo )
            .build();
    }

    private void initializeIdProviderParentFolder()
    {
        if ( !pathInitialized( IDENTITY_PATH ) )
        {
            LOG.info( "Initializing [{}] folder", IDENTITY_PATH );

            final AccessControlEntry userManagerFullAccess =
                AccessControlEntry.create().allowAll().principal( RoleKeys.USER_MANAGER_ADMIN ).build();

            final ChildOrder childOrder =
                ChildOrder.create().add( FieldOrderExpr.create( NodeIndexPath.NAME, OrderExpr.Direction.ASC ) ).build();

            nodeService.create( CreateNodeParams.create()
                                    .parent( IDENTITY_PATH.getParentPath() )
                                    .name( IDENTITY_PATH.getName() )
                                    .permissions( AccessControlList.create()
                                                      .addAll( SystemConstants.SYSTEM_REPO_DEFAULT_ACL.getEntries() )
                                                      .add( userManagerFullAccess )
                                                      .build() )
                                    .inheritPermissions( false )
                                    .childOrder( childOrder )
                                    .build() );
        }
    }

    private void initializeKeys()
    {
        if ( !pathInitialized( KEYS_PATH ) )
        {
            LOG.info( "Initializing [{}] folder", KEYS_PATH );

            nodeService.create( CreateNodeParams.create()
                                    .parent( KEYS_PATH.getParentPath() )
                                    .name( KEYS_PATH.getName() )
                                    .inheritPermissions( true )
                                    .build() );
        }

        if ( !pathInitialized( GENERIC_KEY_PATH ) )
        {
            LOG.info( "Initializing [{}] key", GENERIC_KEY_PATH );

            final SecretKey key;
            try
            {
                key = KeyGenerator.getInstance( "HmacSHA512" ).generateKey();
            }
            catch ( NoSuchAlgorithmException e )
            {
                throw new IllegalStateException( e );
            }

            final PropertyTree data = new PropertyTree();
            data.setString( "key", Base64.getEncoder().encodeToString( key.getEncoded() ) );
            nodeService.create( CreateNodeParams.create()
                                    .parent( GENERIC_KEY_PATH.getParentPath() )
                                    .name( GENERIC_KEY_PATH.getName() )
                                    .data( data )
                                    .inheritPermissions( true )
                                    .build() );
        }
    }

    private boolean keysInitialized()
    {
        return pathInitialized( KEYS_PATH ) && pathInitialized( GENERIC_KEY_PATH );
    }

    private void initializeSystemIdProvider()
    {
        if ( !systemIdProviderInitialized() )
        {
            LOG.info( "Initializing id provider [{}]", IdProviderKey.system() );

            final PropertyTree idProviderConfigTree = new PropertyTree();
            if ( !"false".equalsIgnoreCase( System.getProperty( ADMIN_USER_CREATION_PROPERTY_KEY ) ) )
            {
                idProviderConfigTree.setBoolean( "adminUserCreationEnabled", true );
            }
            final IdProviderConfig idProviderConfig =
                IdProviderConfig.create().applicationKey( SYSTEM_ID_PROVIDER_KEY ).config( idProviderConfigTree ).build();

            final CreateIdProviderParams createParams = CreateIdProviderParams.create()
                .key( IdProviderKey.system() )
                .displayName( "System Id Provider" )
                .idProviderConfig( idProviderConfig )
                .permissions( DEFAULT_ID_PROVIDER_ACL )
                .build();

            this.securityService.createIdProvider( createParams );
        }
    }

    private boolean systemIdProviderInitialized()
    {
        return securityService.getIdProvider( IdProviderKey.system() ) != null;
    }

    private void initializeRoles()
    {
        if ( !pathInitialized( ROLES_PATH ) )
        {
            LOG.info( "Initializing [{}] folder", ROLES_PATH );

            nodeService.create( CreateNodeParams.create()
                                    .parent( ROLES_PATH.getParentPath() )
                                    .name( ROLES_PATH.getName() )
                                    .inheritPermissions( true )
                                    .build() );
        }
        for ( CreateRoleParams createRoleParams : ROLES_TO_CREATE )
        {
            addRole( createRoleParams );
        }
    }

    private boolean rolesInitialized()
    {
        if ( !pathInitialized( ROLES_PATH ) )
        {
            return false;
        }
        for ( CreateRoleParams createRoleParams : ROLES_TO_CREATE )
        {
            if ( securityService.getRole( createRoleParams.getKey() ).isEmpty() )
            {
                return false;
            }
        }
        return true;
    }

    private boolean pathInitialized( final NodePath path )
    {
        return nodeService.nodeExists( path );
    }

    private void initializeUsers()
    {
        for ( CreateUserParams createUser : USERS_TO_CREATE )
        {
            addUser( createUser );
        }
        addMember( RoleKeys.ADMIN, SUPER_USER );
    }

    private boolean usersInitialized()
    {
        for ( CreateUserParams createUser : USERS_TO_CREATE )
        {
            if ( securityService.getUser( createUser.getKey() ).isEmpty() )
            {
                return false;
            }
        }
        return securityService.getMemberships( SUPER_USER ).contains( RoleKeys.ADMIN );
    }

    private void addUser( final CreateUserParams createUser )
    {
        try
        {
            if ( securityService.getUser( createUser.getKey() ).isEmpty() )
            {
                securityService.createUser( createUser );
                LOG.info( "User created: {}", createUser.getKey().toString() );
            }
        }
        catch ( final Exception t )
        {
            LOG.error( "Unable to initialize user: {}", createUser.getKey().toString(), t );
        }
    }

    private void addRole( final CreateRoleParams createRoleParams )
    {
        try
        {
            if ( securityService.getRole( createRoleParams.getKey() ).isEmpty() )
            {
                securityService.createRole( createRoleParams );
                LOG.info( "Role created: " + createRoleParams.getKey().toString() );
            }
        }
        catch ( final Exception t )
        {
            LOG.error( "Unable to initialize role: {}", createRoleParams.getKey().toString(), t );
        }
    }

    private void addMember( final PrincipalKey container, final PrincipalKey member )
    {
        try
        {
            if ( !securityService.getMemberships( member ).contains( container ) )
            {
                securityService.addRelationship( PrincipalRelationship.from( container ).to( member ) );
            }
            LOG.info( "Added {} as member of {}", member, container );
        }
        catch ( final Exception t )
        {
            LOG.error( "Unable to add member: {} -> {}", container, member, t );
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
