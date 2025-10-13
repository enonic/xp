package com.enonic.xp.core.impl.project.init;

import java.time.Instant;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.PushNodeParams;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.query.Direction;
import com.enonic.xp.repository.BranchNotFoundException;
import com.enonic.xp.security.RoleKeys;
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

    private final PropertyTree contentData;

    private ContentInitializer( final Builder builder )
    {
        super( builder );
        this.contentData = builder.contentData;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public void doInitialize()
    {
        createAdminContext( ContentConstants.BRANCH_DRAFT ).runWith( this::initContentNode );
    }

    @Override
    protected boolean isInitialized()
    {
        try
        {
            return createAdminContext( ContentConstants.BRANCH_MASTER ).callWith(
                () -> nodeService.getByPath( ContentConstants.CONTENT_ROOT_PATH ) != null );
        }
        catch ( BranchNotFoundException e )
        {
            return false;
        }
    }

    @Override
    protected String getInitializationSubject()
    {
        return repositoryId + " repo [content] layout";
    }

    private void initContentNode()
    {
        final Node contentRootNode = nodeService.getByPath( ContentConstants.CONTENT_ROOT_PATH );

        final NodeId contentRootNodeId;
        if ( contentRootNode != null )
        {
            contentRootNodeId = contentRootNode.id();
        }
        else
        {
            LOG.info( "Content root-node not found, creating" );

            final PropertyTree data = contentData != null ? contentData : new PropertyTree();
            data.setString( ContentPropertyNames.TYPE, "base:folder" );
            data.setString( ContentPropertyNames.DISPLAY_NAME, "Content" );
            data.addSet( ContentPropertyNames.DATA );
            data.addSet( ContentPropertyNames.FORM );
            data.setString( ContentPropertyNames.CREATOR, ContextAccessor.current().getAuthInfo().getUser().getKey().toString() );
            data.setInstant( ContentPropertyNames.CREATED_TIME, Instant.now() );

            final Node contentRoot = nodeService.create( CreateNodeParams.create()
                                                             .data( data )
                                                             .name( ContentConstants.CONTENT_ROOT_NAME )
                                                             .parent( NodePath.ROOT )
                                                             .permissions( Objects.requireNonNullElse( this.accessControlList,
                                                                                                       CONTENT_ROOT_DEFAULT_ACL ) )
                                                             .childOrder( CONTENT_DEFAULT_CHILD_ORDER )
                                                             .refresh( RefreshMode.ALL )
                                                             .build() );

            LOG.info( "Created content root-node: {}", contentRoot );

            contentRootNodeId = contentRoot.id();
        }
        nodeService.push(
            PushNodeParams.create().ids( NodeIds.from( contentRootNodeId ) ).target( ContentConstants.BRANCH_MASTER ).build() );
    }

    public static class Builder
        extends RepoDependentInitializer.Builder<Builder>
    {
        private PropertyTree contentData;

        public Builder setContentData( final PropertyTree contentData )
        {
            this.contentData = contentData;
            return this;
        }

        public ContentInitializer build()
        {
            validate();
            return new ContentInitializer( this );
        }
    }

}
