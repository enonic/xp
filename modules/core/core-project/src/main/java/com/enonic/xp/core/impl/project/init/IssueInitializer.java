package com.enonic.xp.core.impl.project.init;

import java.time.Instant;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.issue.IssueConstants;
import com.enonic.xp.issue.IssuePropertyNames;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.PushNodeParams;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;


public class IssueInitializer
    extends RepoDependentInitializer
{
    private static final Logger LOG = LoggerFactory.getLogger( IssueInitializer.class );

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
            allow( Permission.READ, Permission.CREATE, Permission.MODIFY, Permission.DELETE ).
            build() ).
        build();

    private IssueInitializer( final Builder builder )
    {
        super( builder );
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    protected void doInitialize()
    {
        createAdminContext( ContentConstants.BRANCH_DRAFT ).runWith( this::initIssueNode );
    }

    @Override
    public boolean isInitialized()
    {
        return createAdminContext( ContentConstants.BRANCH_MASTER ).
            callWith( () -> nodeService.getByPath( IssueConstants.ISSUE_ROOT_PATH ) != null );
    }

    @Override
    protected String getInitializationSubject()
    {
        return repositoryId + " repo [issue] layout";
    }

    private void initIssueNode()
    {
        LOG.info( "Issue root-node not found, creating" );

        final User user = ContextAccessor.current().getAuthInfo().getUser();

        PropertyTree data = new PropertyTree();
        data.setString( IssuePropertyNames.TITLE, "Root issue" );
        data.setString( IssuePropertyNames.CREATOR, user.getKey().toString() );
        data.setInstant( ContentPropertyNames.CREATED_TIME, Instant.now() );

        final Node issueRoot = nodeService.create( CreateNodeParams.create().
            data( data ).
            name( IssueConstants.ISSUE_ROOT_NAME ).
            parent( NodePath.ROOT ).
            permissions( Objects.requireNonNullElse( accessControlList, ISSUE_ROOT_DEFAULT_ACL ) ).
            childOrder( IssueConstants.DEFAULT_CHILD_ORDER ).
            refresh( RefreshMode.ALL ).
            build() );

        LOG.info( "Created issue root-node: " + issueRoot.path() );

        final NodeIds ids = NodeIds.from( issueRoot.id() );
        nodeService.push( PushNodeParams.create().ids( ids ).target( ContentConstants.BRANCH_MASTER ).build() );
    }

    public static class Builder
        extends RepoDependentInitializer.Builder<Builder>
    {
        public IssueInitializer build()
        {
            validate();
            return new IssueInitializer( this );
        }
    }
}
