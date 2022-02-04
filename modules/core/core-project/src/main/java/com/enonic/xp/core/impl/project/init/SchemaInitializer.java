package com.enonic.xp.core.impl.project.init;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.issue.SchemaConstants;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;


public class SchemaInitializer
    extends RepoDependentInitializer
{
    private static final Logger LOG = LoggerFactory.getLogger( SchemaInitializer.class );

    private static final AccessControlList SCHEMA_ROOT_DEFAULT_ACL = AccessControlList.create()
        .add( AccessControlEntry.create().principal( RoleKeys.ADMIN ).allowAll().build() )
        .add( AccessControlEntry.create().principal( RoleKeys.CONTENT_MANAGER_ADMIN ).allowAll().build() )
        .add( AccessControlEntry.create()
                  .principal( RoleKeys.CONTENT_MANAGER_APP )
                  .allow( Permission.READ, Permission.CREATE, Permission.MODIFY, Permission.DELETE )
                  .build() )
        .build();

    private SchemaInitializer( final Builder builder )
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
        createAdminContext( ContentConstants.BRANCH_DRAFT ).runWith( this::initSchemaNode );
    }

    @Override
    public boolean isInitialized()
    {
        return createAdminContext( ContentConstants.BRANCH_DRAFT ).callWith(
            () -> nodeService.getByPath( SchemaConstants.SCHEMA_ROOT_PATH ) != null );
    }

    @Override
    protected String getInitializationSubject()
    {
        return repositoryId + " repo [schema] layout";
    }

    private void initSchemaNode()
    {
        LOG.info( "Schema root-node not found, creating" );

        final User user = ContextAccessor.current().getAuthInfo().getUser();

        final Node schemaRoot = nodeService.create( CreateNodeParams.create()
                                                        .data( new PropertyTree() )
                                                        .name( SchemaConstants.SCHEMA_ROOT_NAME )
                                                        .parent( SchemaConstants.SCHEMA_ROOT_PARENT )
                                                        .permissions(
                                                            Objects.requireNonNullElse( accessControlList, SCHEMA_ROOT_DEFAULT_ACL ) )
                                                        .build() );

        final NodeIds.Builder pushToMaster = NodeIds.create();

        final NodeIds adminNodeIds = initAdminNodes( schemaRoot.path() );
        final NodeIds siteNodeIds = initSiteNodes( schemaRoot.path() );

        pushToMaster.addAll( adminNodeIds ).addAll( siteNodeIds );
        LOG.info( "Created schema root-node: " + schemaRoot.path() );

        nodeService.refresh( RefreshMode.ALL );

//        nodeService.push( pushToMaster.build(), ContentConstants.BRANCH_MASTER );
    }

    private NodeIds initSiteNodes( final NodePath parent )
    {
        final Node siteRoot = nodeService.create( CreateNodeParams.create()
                                                      .data( new PropertyTree() )
                                                      .name( SchemaConstants.SITE_ROOT_NAME )
                                                      .parent( parent )
                                                      .permissions(
                                                          Objects.requireNonNullElse( accessControlList, SCHEMA_ROOT_DEFAULT_ACL ) )
                                                      .build() );
        final NodeId contentTypeNodeId = initContentTypeNode( siteRoot.path() );
        final NodeId partNodeId = initPartNode( siteRoot.path() );
        final NodeId layoutNodeId = initLayoutNode( siteRoot.path() );
        final NodeId pageNodeId = initPageNode( siteRoot.path() );

        return NodeIds.from( siteRoot.id(), contentTypeNodeId, partNodeId, layoutNodeId, pageNodeId );
    }

    private NodeIds initAdminNodes( final NodePath parent )
    {
        final Node adminRoot = nodeService.create( CreateNodeParams.create()
                                                       .data( new PropertyTree() )
                                                       .name( SchemaConstants.ADMIN_ROOT_NAME )
                                                       .parent( parent )
                                                       .permissions(
                                                           Objects.requireNonNullElse( accessControlList, SCHEMA_ROOT_DEFAULT_ACL ) )
                                                       .build() );
        final NodeId widgetNodeId = initWidgetNode( adminRoot.path() );

        return NodeIds.from( adminRoot.id(), widgetNodeId );
    }

    private NodeId initContentTypeNode( final NodePath parent )
    {
        return nodeService.create( CreateNodeParams.create()
                                       .data( new PropertyTree() )
                                       .name( SchemaConstants.CONTENT_TYPE_ROOT_NAME )
                                       .parent( parent )
                                       .permissions( Objects.requireNonNullElse( accessControlList, SCHEMA_ROOT_DEFAULT_ACL ) )
                                       .build() ).id();
    }

    private NodeId initPartNode( final NodePath parent )
    {
        return nodeService.create( CreateNodeParams.create()
                                       .data( new PropertyTree() )
                                       .name( SchemaConstants.PART_ROOT_NAME )
                                       .parent( parent )
                                       .permissions( Objects.requireNonNullElse( accessControlList, SCHEMA_ROOT_DEFAULT_ACL ) )
                                       .build() ).id();
    }

    private NodeId initLayoutNode( final NodePath parent )
    {
        return nodeService.create( CreateNodeParams.create()
                                       .data( new PropertyTree() )
                                       .name( SchemaConstants.LAYOUT_ROOT_NAME )
                                       .parent( parent )
                                       .permissions( Objects.requireNonNullElse( accessControlList, SCHEMA_ROOT_DEFAULT_ACL ) )
                                       .build() ).id();
    }

    private NodeId initPageNode( final NodePath parent )
    {
        return nodeService.create( CreateNodeParams.create()
                                       .data( new PropertyTree() )
                                       .name( SchemaConstants.PAGE_ROOT_NAME )
                                       .parent( parent )
                                       .permissions( Objects.requireNonNullElse( accessControlList, SCHEMA_ROOT_DEFAULT_ACL ) )
                                       .build() ).id();
    }

    private NodeId initWidgetNode( final NodePath parent )
    {
        return nodeService.create( CreateNodeParams.create()
                                       .data( new PropertyTree() )
                                       .name( SchemaConstants.WIDGET_ROOT_NAME )
                                       .parent( parent )
                                       .permissions( Objects.requireNonNullElse( accessControlList, SCHEMA_ROOT_DEFAULT_ACL ) )
                                       .build() ).id();
    }

    public static class Builder
        extends RepoDependentInitializer.Builder<Builder>
    {
        public SchemaInitializer build()
        {
            validate();
            return new SchemaInitializer( this );
        }
    }
}
