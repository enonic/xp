package com.enonic.xp.core.impl.content;

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
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.init.ExternalInitializer;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.query.Direction;
import com.enonic.xp.repository.CreateBranchParams;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.security.auth.AuthenticationInfo;

public final class ContentInitializer
    extends ExternalInitializer
{
    private final static Logger LOG = LoggerFactory.getLogger( ContentInitializer.class );

    public static final User SUPER_USER = User.create().
        key( PrincipalKey.ofSuperUser() ).
        login( PrincipalKey.ofSuperUser().getId() ).
        build();

    private static final AccessControlList CONTENT_REPO_DEFAULT_ACL = AccessControlList.create().
        add( AccessControlEntry.create().
            allowAll().
            principal( RoleKeys.ADMIN ).
            build() ).
        add( AccessControlEntry.create().
            allow( Permission.READ ).
            principal( RoleKeys.CONTENT_MANAGER_ADMIN ).
            build() ).
        build();

    private static final AccessControlList CONTENT_ROOT_DEFAULT_ACL = AccessControlList.create().
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

    private static final IndexPath CONTENT_INDEX_PATH_DISPLAY_NAME = IndexPath.from( "displayName" );

    private static final ChildOrder CONTENT_DEFAULT_CHILD_ORDER = ChildOrder.from( CONTENT_INDEX_PATH_DISPLAY_NAME + " " + Direction.ASC );

    private final NodeService nodeService;

    private final RepositoryService repositoryService;  

    private ContentInitializer( final Builder builder )
    {
        super( builder );
        this.nodeService = builder.nodeService;
        this.repositoryService = builder.repositoryService;
    }

    @Override
    public final void doInitialize()
    {
        createAdminContext().runWith( () -> {
            initializeRepository();
            createDraftBranch();
            initContentNode();
        } );
    }

    @Override
    protected boolean isInitialized()
    {
        return createAdminContext().
            callWith( () -> repositoryService.isInitialized( ContentConstants.CONTENT_REPO.getId() ) &&
                nodeService.getByPath( ContentConstants.CONTENT_ROOT_PATH ) != null );
    }

    @Override
    protected String getInitializationSubject()
    {
        return "Cms-repo";
    }

    private void createDraftBranch()
    {
        this.repositoryService.createBranch( CreateBranchParams.from( ContentConstants.BRANCH_DRAFT.getValue() ) );
    }

    private void initializeRepository()
    {
        final CreateRepositoryParams createRepositoryParams = CreateRepositoryParams.create().
            repositoryId( RepositoryId.from( "cms-repo" ) ).
            rootPermissions( CONTENT_REPO_DEFAULT_ACL ).
            rootChildOrder( ContentConstants.DEFAULT_CONTENT_REPO_ROOT_ORDER ).
            build();

        this.repositoryService.createRepository( createRepositoryParams );
    }

    private void initContentNode()
    {
        final Node contentRootNode = nodeService.getByPath( ContentConstants.CONTENT_ROOT_PATH );

        final User user = ContextAccessor.current().getAuthInfo().getUser();

        if ( contentRootNode == null )
        {
            LOG.info( "Content root-node not found, creating" );

            PropertyTree data = new PropertyTree();
            data.setString( ContentPropertyNames.TYPE, "base:folder" );
            data.setString( ContentPropertyNames.DISPLAY_NAME, "Content" );
            data.addSet( ContentPropertyNames.DATA );
            data.addSet( ContentPropertyNames.FORM );
            data.setString( ContentPropertyNames.CREATOR, user.getKey().toString() );
            data.setInstant( ContentPropertyNames.CREATED_TIME, Instant.now() );

            final Node contentRoot = nodeService.create( CreateNodeParams.create().
                data( data ).
                name( ContentConstants.CONTENT_ROOT_NAME ).
                parent( NodePath.ROOT ).
                permissions( CONTENT_ROOT_DEFAULT_ACL ).
                childOrder( CONTENT_DEFAULT_CHILD_ORDER ).
                build() );

            LOG.info( "Created content root-node: " + contentRoot.path() );

            nodeService.refresh( RefreshMode.ALL );

            nodeService.push( NodeIds.from( contentRoot.id() ), ContentConstants.BRANCH_DRAFT );
        }
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

        private RepositoryService repositoryService;

        public Builder setNodeService( final NodeService nodeService )
        {
            this.nodeService = nodeService;
            return this;
        }

        public Builder setRepositoryService( final RepositoryService repositoryService )
        {
            this.repositoryService = repositoryService;
            return this;
        }

        @Override
        protected void validate()
        {
            super.validate();
            Preconditions.checkNotNull( nodeService );
        }

        public ContentInitializer build()
        {
            validate();
            return new ContentInitializer( this );
        }
    }

}
