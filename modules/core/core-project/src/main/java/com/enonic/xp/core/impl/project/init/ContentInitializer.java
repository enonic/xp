package com.enonic.xp.core.impl.project.init;

import java.time.Instant;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.archive.ArchiveConstants;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.query.Direction;
import com.enonic.xp.repository.CreateBranchParams;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

public final class ContentInitializer
    extends RepoDependentInitializer
{
    private static final Logger LOG = LoggerFactory.getLogger( ContentInitializer.class );

    private static final AccessControlList CONTENT_ROOT_DEFAULT_ACL = AccessControlList.create()
        .add( AccessControlEntry.create().principal( RoleKeys.ADMIN ).allowAll().build() )
        .add( AccessControlEntry.create().principal( RoleKeys.CONTENT_MANAGER_ADMIN ).allowAll().build() )
        .add( AccessControlEntry.create().principal( RoleKeys.CONTENT_MANAGER_APP ).allow( Permission.READ ).build() )
        .build();

    private static final IndexPath CONTENT_INDEX_PATH_DISPLAY_NAME = IndexPath.from( "displayName" );

    private static final ChildOrder CONTENT_DEFAULT_CHILD_ORDER = ChildOrder.from( CONTENT_INDEX_PATH_DISPLAY_NAME + " " + Direction.ASC );

    private final RepositoryService repositoryService;

    private final PropertyTree data;

    private ContentInitializer( final Builder builder )
    {
        super( builder );
        this.repositoryService = builder.repositoryService;
        this.data = builder.data;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public void doInitialize()
    {
        createAdminContext( ContentConstants.BRANCH_MASTER ).runWith( () -> {
            if ( !repositoryService.isInitialized( repositoryId ) )
            {
                initializeRepository();
                createDraftBranch();
            }
        } );
        createAdminContext( ContentConstants.BRANCH_DRAFT ).runWith( () -> {
            this.initContentNode();
            this.initArchiveNode();
        } );
    }

    @Override
    protected boolean isInitialized()
    {
        return createAdminContext( ContentConstants.BRANCH_MASTER ).callWith(
            () -> repositoryService.isInitialized( repositoryId ) && nodeService.getByPath( ContentConstants.CONTENT_ROOT_PATH ) != null &&
                nodeService.getByPath( ArchiveConstants.ARCHIVE_ROOT_PATH ) != null );
    }

    @Override
    protected String getInitializationSubject()
    {
        return repositoryId + " repo";
    }

    private void createDraftBranch()
    {
        this.repositoryService.createBranch( CreateBranchParams.from( ContentConstants.BRANCH_DRAFT.getValue() ) );
    }

    private void initializeRepository()
    {
        final CreateRepositoryParams createRepositoryParams = CreateRepositoryParams.create()
            .repositoryId( repositoryId )
            .data( data )
            .rootPermissions( ContentConstants.CONTENT_REPO_DEFAULT_ACL )
            .rootChildOrder( ContentConstants.DEFAULT_CONTENT_REPO_ROOT_ORDER )
            .build();

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

            final Node contentRoot = nodeService.create( CreateNodeParams.create()
                                                             .data( data )
                                                             .name( ContentConstants.CONTENT_ROOT_NAME )
                                                             .parent( NodePath.ROOT )
                                                             .permissions( Objects.requireNonNullElse( this.accessControlList,
                                                                                                       CONTENT_ROOT_DEFAULT_ACL ) )
                                                             .childOrder( CONTENT_DEFAULT_CHILD_ORDER )
                                                             .build() );

            LOG.info( "Created content root-node: {}", contentRoot );

            nodeService.refresh( RefreshMode.ALL );

            nodeService.push( NodeIds.from( contentRoot.id() ), ContentConstants.BRANCH_MASTER );
        }
    }

    private void initArchiveNode()
    {
        Node archiveNode = nodeService.getByPath( ArchiveConstants.ARCHIVE_ROOT_PATH );

        if ( archiveNode == null )
        {
            LOG.info( "Archive node not found, creating" );

            final User user = ContextAccessor.current().getAuthInfo().getUser();

            final PropertyTree data = new PropertyTree();
            data.setString( ContentPropertyNames.TYPE, ContentTypeName.folder().toString() );
            data.setString( ContentPropertyNames.DISPLAY_NAME, "Archive" );
            data.addSet( ContentPropertyNames.DATA );
            data.addSet( ContentPropertyNames.FORM );
            data.setString( ContentPropertyNames.CREATOR, user.getKey().toString() );
            data.setString( ContentPropertyNames.MODIFIER, user.getKey().toString() );
            data.setInstant( ContentPropertyNames.CREATED_TIME, Instant.now() );

            final CreateNodeParams createNodeParams = CreateNodeParams.create()
                .permissions( Objects.requireNonNullElse( accessControlList, ArchiveConstants.ARCHIVE_ROOT_DEFAULT_ACL ) )
                .parent( ContentConstants.CONTENT_ROOT_PATH )
                .name( ArchiveConstants.ARCHIVE_ROOT_NAME )
                .data( data )
                .childOrder( ArchiveConstants.DEFAULT_ARCHIVE_REPO_ROOT_ORDER )
                .build();

            archiveNode = nodeService.create( createNodeParams );

            LOG.info( "Created archive root-node: " + archiveNode.path() );

            nodeService.refresh( RefreshMode.ALL );
        }
    }

    public static class Builder
        extends RepoDependentInitializer.Builder<Builder>
    {
        private RepositoryService repositoryService;

        private PropertyTree data;

        public Builder setRepositoryService( final RepositoryService repositoryService )
        {
            this.repositoryService = repositoryService;
            return this;
        }

        public Builder setData( final PropertyTree data )
        {
            this.data = data;
            return this;
        }

        public ContentInitializer build()
        {
            validate();
            return new ContentInitializer( this );
        }
    }

}
