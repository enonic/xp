package com.enonic.wem.core.content;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.context.ContextBuilder;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.index.IndexPath;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.CreateRootNodeParams;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeIds;
import com.enonic.wem.api.node.NodeService;
import com.enonic.wem.api.node.RootNode;
import com.enonic.wem.api.query.Direction;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.RoleKeys;
import com.enonic.wem.api.security.User;
import com.enonic.wem.api.security.UserStoreKey;
import com.enonic.wem.api.security.acl.AccessControlEntry;
import com.enonic.wem.api.security.acl.AccessControlList;
import com.enonic.wem.api.security.auth.AuthenticationInfo;

public final class ContentInitializer
{
    private final static Logger LOG = LoggerFactory.getLogger( ContentInitializer.class );

    public static final User SUPER_USER = User.create().
        key( PrincipalKey.ofUser( UserStoreKey.system(), "su" ) ).
        login( "su" ).
        build();


    private static final AccessControlList CONTENT_ROOT_DEFAULT_ACL = AccessControlList.create().
        add( AccessControlEntry.create().
            principal( RoleKeys.ADMIN ).
            allowAll().
            build() ).
        add( AccessControlEntry.create().
            principal( RoleKeys.CONTENT_MANAGER ).
            allowAll().
            build() ).
        build();

    private static final IndexPath CONTENT_INDEX_PATH_DISPLAY_NAME = IndexPath.from( "displayName" );

    private static final ChildOrder CONTENT_DEFAULT_CHILD_ORDER = ChildOrder.from( CONTENT_INDEX_PATH_DISPLAY_NAME + " " + Direction.ASC );

    private final NodeService nodeService;

    public ContentInitializer( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }

    public final void initialize()
    {
        final RootNode rootNode = runAsAdmin( this::doInitNodeRoot );
        runAsAdmin( () -> this.doInitContentRootNode( rootNode ) );
    }

    private void doInitContentRootNode( final RootNode rootNode )
    {
        final Node contentRootNode = nodeService.getByPath( ContentConstants.CONTENT_ROOT_PATH );

        if ( contentRootNode == null )
        {
            LOG.info( "Content root-node not found, creating" );

            PropertyTree data = new PropertyTree();
            data.setString( ContentPropertyNames.TYPE, "base:folder" );
            data.setString( ContentPropertyNames.DISPLAY_NAME, "Content" );
            data.addSet( ContentPropertyNames.DATA );
            data.addSet( ContentPropertyNames.FORM );

            final Node root = nodeService.create( CreateNodeParams.create().
                data( data ).
                name( ContentConstants.CONTENT_ROOT_NAME ).
                parent( rootNode.path() ).
                permissions( CONTENT_ROOT_DEFAULT_ACL ).
                childOrder( CONTENT_DEFAULT_CHILD_ORDER ).
                build() );

            LOG.info( "Created content root-node: " + root.path() );

            nodeService.push( NodeIds.from( root.id() ), ContentConstants.WORKSPACE_ONLINE );
        }
    }

    private void runAsAdmin( final Runnable runnable )
    {
        final AuthenticationInfo authInfo = createAdminAuthInfo();

        ContextBuilder.from( ContentConstants.CONTEXT_DRAFT ).
            authInfo( authInfo ).
            build().
            runWith( runnable );
    }

    private <T> T runAsAdmin( final Callable<T> callable )
    {
        final AuthenticationInfo authInfo = createAdminAuthInfo();

        return ContextBuilder.from( ContentConstants.CONTEXT_DRAFT ).
            authInfo( authInfo ).
            build().
            callWith( callable );
    }

    private AuthenticationInfo createAdminAuthInfo()
    {
        return AuthenticationInfo.create().
            principals( RoleKeys.ADMIN ).
            user( SUPER_USER ).
            build();
    }

    private RootNode doInitNodeRoot()
    {
        final RootNode existingRoot = this.nodeService.getRoot();

        if ( existingRoot == null )
        {
            final RootNode rootNode = this.nodeService.createRootNode( CreateRootNodeParams.create().
                childOrder( ChildOrder.from( "_name ASC" ) ).
                permissions( CONTENT_ROOT_DEFAULT_ACL ).
                build() );

            nodeService.push( NodeIds.from( rootNode.id() ), ContentConstants.WORKSPACE_ONLINE );

            return rootNode;
        }

        return existingRoot;
    }

}
