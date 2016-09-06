package com.enonic.xp.repo.impl.node;

import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.CreateRootNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityConstants;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.auth.AuthenticationInfo;

public class SystemNodeInitializer
{
    public static final PrincipalKey SUPER_USER = PrincipalKey.ofUser( UserStoreKey.system(), "su" );

    private final NodeService nodeService;

    public SystemNodeInitializer( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }

    public void initialize()
    {
        runAsAdmin( () -> {
            final Node existingRoot = this.nodeService.getRoot();
            if ( existingRoot == null )
            {

                this.nodeService.createRootNode( CreateRootNodeParams.create().
                    childOrder( ChildOrder.from( "_name ASC" ) ).
                    permissions( SystemConstants.SYSTEM_REPO_DEFAULT_ACL ).
                    build() );

            }
        });        
    }

    private void runAsAdmin( Runnable runnable )
    {
        final User admin = User.create().key( SUPER_USER ).login( "su" ).build();
        final AuthenticationInfo authInfo = AuthenticationInfo.create().principals( RoleKeys.ADMIN ).user( admin ).build();
        ContextBuilder.from( SecurityConstants.CONTEXT_SECURITY ).authInfo( authInfo ).build().runWith( runnable );
    }
}
