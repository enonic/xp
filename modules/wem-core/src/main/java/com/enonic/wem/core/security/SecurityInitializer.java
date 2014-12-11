package com.enonic.wem.core.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeService;
import com.enonic.wem.api.security.CreateRoleParams;
import com.enonic.wem.api.security.CreateUserParams;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.PrincipalRelationship;
import com.enonic.wem.api.security.SecurityService;
import com.enonic.wem.api.security.SystemConstants;
import com.enonic.wem.api.security.User;
import com.enonic.wem.api.security.UserStoreKey;

public final class SecurityInitializer
{
    private final static Logger LOG = LoggerFactory.getLogger( SecurityInitializer.class );

    private SecurityService securityService;

    private NodeService nodeService;

    public final void init()
    {
        LOG.info( "Initializing security principals" );

        initializeUserStores();

        final User anonymous = User.anonymous();
        final CreateUserParams createUser = CreateUserParams.create().
            userKey( anonymous.getKey() ).
            displayName( anonymous.getDisplayName() ).
            login( anonymous.getLogin() ).
            email( anonymous.getEmail() ).
            build();
        addUser( createUser );

        final CreateUserParams createAdmin = CreateUserParams.create().
            userKey( PrincipalKey.ofUser( UserStoreKey.system(), "admin" ) ).
            displayName( "Administrator" ).
            login( "admin" ).
            password( "password" ).
            build();
        addUser( createAdmin );

        initializeRoleFolder();

        final CreateRoleParams createEnterpriseAdmin = CreateRoleParams.create().
            roleKey( PrincipalKey.ofEnterpriseAdmin() ).
            displayName( "Enterprise Administrator" ).
            build();
        addRole( createEnterpriseAdmin );

        addMember( PrincipalKey.ofEnterpriseAdmin(), createAdmin.getKey() );
    }


    private void initializeUserStores()
    {
        final Node systemUserStore = SystemConstants.CONTEXT_USER_STORES.callWith( () -> nodeService.getByPath(
            NodePath.newNodePath( NodePath.ROOT, SystemConstants.SYSTEM_USERSTORE.getDisplayName() ).build() ) );

        if ( systemUserStore == null )
        {
            LOG.info( "Initializing userstore " + SystemConstants.SYSTEM_USERSTORE.getKey() );

            this.securityService.createUserStore( SystemConstants.SYSTEM_USERSTORE.getKey(),
                                                  SystemConstants.SYSTEM_USERSTORE.getDisplayName() );
        }
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
        catch ( Throwable t )
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
        catch ( Throwable t )
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
        catch ( Throwable t )
        {
            LOG.error( "Unable to add member: " + container + " -> " + member, t );
        }
    }

    private void initializeRoleFolder()
    {
        final NodePath rolesNodePath = UserStoreNodeTranslator.getRolesNodePath();
        final Node roleNode = SystemConstants.CONTEXT_USER_STORES.callWith( () -> nodeService.getByPath( rolesNodePath ) );

        if ( roleNode == null )
        {
            LOG.info( "Initializing roles folder" );

            SystemConstants.CONTEXT_USER_STORES.callWith( () -> nodeService.create( CreateNodeParams.create().
                parent( rolesNodePath.getParentPath() ).
                name( rolesNodePath.getLastElement().toString() ).
                build() ) );
        }
    }

    public void setSecurityService( final SecurityService securityService )
    {
        this.securityService = securityService;
    }

    public void setNodeService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }
}


