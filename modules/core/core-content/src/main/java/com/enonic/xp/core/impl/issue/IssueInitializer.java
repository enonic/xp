package com.enonic.xp.core.impl.issue;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.init.ExternalInitializer;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.security.auth.AuthenticationInfo;


public class IssueInitializer
    extends ExternalInitializer
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
            allow( Permission.READ, Permission.CREATE, Permission.MODIFY, Permission.DELETE ).
            build() ).
        build();

    private static final User SUPER_USER = User.create().
        key( PrincipalKey.ofSuperUser() ).
        login( PrincipalKey.ofSuperUser().getId() ).
        build();

    private IssueInitializer( final Builder builder )
    {
        super( builder );
        this.nodeService = builder.nodeService;
    }

    @Override
    protected void doInitialize()
    {
        createAdminContext().runWith( this::initIssueNode );
    }

    public boolean isInitialized()
    {
        return createAdminContext().
            callWith( () -> nodeService.getByPath( IssueConstants.ISSUE_ROOT_PATH ) != null );
    }

    @Override
    protected String getInitializationSubject()
    {
        return "Cms-repo [issue] layout";
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
            permissions( ISSUE_ROOT_DEFAULT_ACL ).
            childOrder( IssueConstants.DEFAULT_CHILD_ORDER ).
            build() );

        LOG.info( "Created issue root-node: " + issueRoot.path() );

        nodeService.refresh( RefreshMode.ALL );

        nodeService.push( NodeIds.from( issueRoot.id() ), ContentConstants.BRANCH_DRAFT );
    }

    private Context createAdminContext()
    {
        final AuthenticationInfo authInfo = createAdminAuthInfo();

        return ContextBuilder.from( ContentConstants.CONTEXT_MASTER ).
            authInfo( authInfo ).
            build();
    }

    private AuthenticationInfo createAdminAuthInfo()
    {
        return AuthenticationInfo.create().
            principals( RoleKeys.ADMIN ).
            user( SUPER_USER ).
            build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends ExternalInitializer.Builder<Builder>
    {
        private NodeService nodeService;

        public Builder setNodeService( final NodeService nodeService )
        {
            this.nodeService = nodeService;
            return this;
        }

        @Override
        protected void validate()
        {
            super.validate();
            Preconditions.checkNotNull( nodeService );
        }

        public IssueInitializer build()
        {
            validate();
            return new IssueInitializer( this );
        }
    }
}
