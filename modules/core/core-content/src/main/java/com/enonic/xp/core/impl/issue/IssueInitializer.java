package com.enonic.xp.core.impl.issue;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.issue.IssueConstants;
import com.enonic.xp.issue.IssuePropertyNames;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.security.auth.AuthenticationInfo;


public class IssueInitializer
{

    private final NodeService nodeService;

    private final static Logger LOG = LoggerFactory.getLogger( IssueInitializer.class );

    private static final AccessControlList ISSUE_ROOT_DEFAULT_ACL = AccessControlList.create().
        add( AccessControlEntry.create().
            principal( RoleKeys.ADMIN ).
            allowAll().
            build() ).
        add( AccessControlEntry.create().
            principal( RoleKeys.CONTENT_MANAGER_ADMIN ).
            allowAll().
            build() ).
        add( AccessControlEntry.create().
            principal( RoleKeys.CONTENT_MANAGER_APP ).
            allow( Permission.READ ).
            build() ).
        build();

    private static final User SUPER_USER = User.create().
        key( PrincipalKey.ofUser( UserStoreKey.system(), "su" ) ).
        login( "su" ).
        build();

    public IssueInitializer( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }

    public final void initialize()
    {
        runAsAdmin( () -> initIssueNode() );
    }

    private void initIssueNode()
    {
        final Node issueRootNode = nodeService.getByPath( IssueConstants.ISSUE_ROOT_PATH );

        final User user = ContextAccessor.current().getAuthInfo().getUser();

        if ( issueRootNode == null )
        {
            LOG.info( "Issue root-node not found, creating" );

            PropertyTree data = new PropertyTree();
            data.setString( IssuePropertyNames.TITLE, "Root issue" );
            data.setString( IssuePropertyNames.CREATOR, user.getKey().toString() );
            data.setInstant( ContentPropertyNames.CREATED_TIME, Instant.now() );

            final Node issueRoot = nodeService.create( CreateNodeParams.create().
                data( data ).
                name( IssueConstants.ISSUE_ROOT_NAME ).
                parent( NodePath.ROOT ).
                permissions( ISSUE_ROOT_DEFAULT_ACL ).
                childOrder( IssueConstants.DEFAULT_CHILD_ORDER ).
                build() );

            LOG.info( "Created issue root-node: " + issueRoot.path() );

            nodeService.refresh( RefreshMode.ALL );

            nodeService.push( NodeIds.from( issueRoot.id() ), ContentConstants.BRANCH_DRAFT );
        }
    }

    private void runAsAdmin( final Runnable runnable )
    {
        final AuthenticationInfo authInfo = createAdminAuthInfo();

        ContextBuilder.from( ContentConstants.CONTEXT_MASTER ).
            authInfo( authInfo ).
            build().
            runWith( runnable );
    }

    private AuthenticationInfo createAdminAuthInfo()
    {
        return AuthenticationInfo.create().
            principals( RoleKeys.ADMIN ).
            user( SUPER_USER ).
            build();
    }
}
