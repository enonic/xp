package com.enonic.xp.core.content;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.ApplyContentPermissionsParams;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.DuplicateContentParams;
import com.enonic.xp.content.MoveContentParams;
import com.enonic.xp.content.PatchContentParams;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.content.SortContentParams;
import com.enonic.xp.content.UnpublishContentParams;
import com.enonic.xp.content.UpdateContentMetadataParams;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.UpdateWorkflowParams;
import com.enonic.xp.content.WorkflowInfo;
import com.enonic.xp.content.WorkflowState;
import com.enonic.xp.core.impl.content.ContentAttributesHelper;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.Attributes;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionQuery;
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.repo.impl.version.VersionIndexPath;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.util.GenericValue;

import static org.assertj.core.api.Assertions.assertThat;

class ContentServiceImplTest_editorial
    extends AbstractContentServiceTest
{
    @Test
    void create_does_not_set_editorial()
    {
        final Content content = createFolder( "content", ContentPath.ROOT );

        assertThat( getEditorialProperty( content ) ).isNull();
    }

    @Test
    void update_does_not_set_editorial()
    {
        final Content content = createFolder( "content", ContentPath.ROOT );

        final UpdateContentParams updateParams = new UpdateContentParams();
        updateParams.contentId( content.getId() ).editor( edit -> edit.displayName = "updated" );
        this.contentService.update( updateParams );

        assertThat( getEditorialProperty( content ) ).isNull();
    }

    @Test
    void duplicate_does_not_set_editorial()
    {
        final Content content = createFolder( "content", ContentPath.ROOT );

        final var result = this.contentService.duplicate( DuplicateContentParams.create().contentId( content.getId() ).build() );

        for ( final var duplicatedId : result.getDuplicatedContents() )
        {
            assertThat( getEditorialProperty( duplicatedId ) ).isNull();
        }
    }

    @Test
    void move_does_not_set_editorial()
    {
        final Content source = createFolder( "source", ContentPath.ROOT );
        final Content child = createFolder( "child", source.getPath() );
        final Content target = createFolder( "target", ContentPath.ROOT );

        this.contentService.move( MoveContentParams.create().contentId( child.getId() ).parentContentPath( target.getPath() ).build() );

        assertThat( getEditorialProperty( child ) ).isNull();
    }

    @Test
    void sort_does_not_set_editorial()
    {
        final Content parent = createFolder( "parent", ContentPath.ROOT );
        createFolder( "child1", parent.getPath() );
        createFolder( "child2", parent.getPath() );

        this.contentService.sort( SortContentParams.create().contentId( parent.getId() ).childOrder( ChildOrder.manualOrder() ).build() );

        assertThat( getEditorialProperty( parent ) ).isNull();
    }

    // --- Non-editorial operations SHOULD set content.editorial ---

    @Test
    void unpublish_sets_editorial()
    {
        final Content content = createFolder( "content", ContentPath.ROOT );
        final String createVersionId = getLatestNodeVersionId( content );

        this.contentService.publish(
            PushContentParams.create().contentIds( ContentIds.from( content.getId() ) ).includeDependencies( false ).build() );

        this.contentService.unpublish( UnpublishContentParams.create().contentIds( ContentIds.from( content.getId() ) ).build() );

        assertThat( getEditorialProperty( content ) ).isEqualTo( createVersionId );
    }

    @Test
    void applyPermissions_sets_editorial()
    {
        final Content content = createFolder( "content", ContentPath.ROOT );
        final String createVersionId = getLatestNodeVersionId( content );

        this.contentService.applyPermissions( ApplyContentPermissionsParams.create()
                                                  .contentId( content.getId() )
                                                  .addPermissions( AccessControlList.of( AccessControlEntry.create()
                                                                                             .principal( TEST_DEFAULT_USER.getKey() )
                                                                                             .allow( Permission.READ )
                                                                                             .build() ) )
                                                  .build() );

        assertThat( getEditorialProperty( content ) ).isEqualTo( createVersionId );
    }

    @Test
    void updateWorkflow_sets_editorial()
    {
        final Content content = createFolder( "content", ContentPath.ROOT );
        final String createVersionId = getLatestNodeVersionId( content );

        this.contentService.updateWorkflow( UpdateWorkflowParams.create()
                                                .contentId( content.getId() )
                                                .editor( edit -> edit.workflow =
                                                    WorkflowInfo.create().state( WorkflowState.PENDING_APPROVAL ).build() )
                                                .build() );

        assertThat( getEditorialProperty( content ) ).isEqualTo( createVersionId );
    }

    @Test
    void updateMetadata_sets_editorial()
    {
        final Content content = createFolder( "content", ContentPath.ROOT );
        final String createVersionId = getLatestNodeVersionId( content );

        this.contentService.updateMetadata(
            UpdateContentMetadataParams.create().contentId( content.getId() ).editor( edit -> edit.language = Locale.ENGLISH ).build() );

        assertThat( getEditorialProperty( content ) ).isEqualTo( createVersionId );
    }

    @Test
    void patch_does_not_set_editorial()
    {
        final Content content = createFolder( "content", ContentPath.ROOT );

        this.contentService.patch(
            PatchContentParams.create().contentId( content.getId() ).patcher( edit -> edit.displayName.setValue( "patched" ) ).build() );

        assertThat( getEditorialProperty( content ) ).isNull();
    }

    @Test
    void editorial_is_preserved_through_multiple_non_editorial_operations()
    {
        final Content content = createFolder( "content", ContentPath.ROOT );
        final String createVersionId = getLatestNodeVersionId( content );

        // First non-editorial: updateWorkflow
        this.contentService.updateWorkflow( UpdateWorkflowParams.create()
                                                .contentId( content.getId() )
                                                .editor( edit -> edit.workflow =
                                                    WorkflowInfo.create().state( WorkflowState.PENDING_APPROVAL ).build() )
                                                .build() );

        assertThat( getEditorialProperty( content ) ).isEqualTo( createVersionId );

        // Second non-editorial: updateMetadata — should still point to original create version
        this.contentService.updateMetadata(
            UpdateContentMetadataParams.create().contentId( content.getId() ).editor( edit -> edit.language = Locale.ENGLISH ).build() );

        assertThat( getEditorialProperty( content ) ).isEqualTo( createVersionId );
    }

    @Test
    void editorial_resets_after_editorial_operation()
    {
        final Content content = createFolder( "content", ContentPath.ROOT );
        final String createVersionId = getLatestNodeVersionId( content );

        // Non-editorial: updateWorkflow — sets editorial to create version
        this.contentService.updateWorkflow( UpdateWorkflowParams.create()
                                                .contentId( content.getId() )
                                                .editor( edit -> edit.workflow =
                                                    WorkflowInfo.create().state( WorkflowState.PENDING_APPROVAL ).build() )
                                                .build() );

        assertThat( getEditorialProperty( content ) ).isEqualTo( createVersionId );

        // Editorial: update — clears editorial
        final UpdateContentParams updateParams = new UpdateContentParams();
        updateParams.contentId( content.getId() ).editor( edit -> edit.displayName = "updated" );
        this.contentService.update( updateParams );

        final String updateVersionId = getLatestNodeVersionId( content );
        assertThat( getEditorialProperty( content ) ).isNull();

        // Non-editorial: updateMetadata — should now point to the update version
        this.contentService.updateMetadata(
            UpdateContentMetadataParams.create().contentId( content.getId() ).editor( edit -> edit.language = Locale.FRENCH ).build() );

        assertThat( getEditorialProperty( content ) ).isEqualTo( updateVersionId );
    }

    // --- Helpers ---

    private Content createFolder( final String name, final ContentPath parentPath )
    {
        return this.contentService.create( CreateContentParams.create()
                                               .contentData( new PropertyTree() )
                                               .displayName( name )
                                               .parent( parentPath )
                                               .name( name )
                                               .type( ContentTypeName.folder() )
                                               .build() );
    }

    private String getEditorialProperty( final Content content )
    {
        return getEditorialProperty( content.getId() );
    }

    private String getEditorialProperty( final ContentId contentId )
    {
        final NodeId nodeId = NodeId.from( contentId );
        final NodeVersionQueryResult versions = this.nodeService.findVersions( NodeVersionQuery.create()
                                                                                   .nodeId( nodeId )
                                                                                   .size( 1 )
                                                                                   .addOrderBy( FieldOrderExpr.create( VersionIndexPath.TIMESTAMP,
                                                                                                                       OrderExpr.Direction.DESC ) )
                                                                                   .build() );
        final NodeVersion nodeVersion = versions.getNodeVersions().iterator().next();
        final Attributes attributes = nodeVersion.getAttributes();
        if ( attributes == null )
        {
            return null;
        }
        for ( final var entry : attributes.entrySet() )
        {
            if ( entry.getKey().startsWith( "content." ) )
            {
                final GenericValue editorial = entry.getValue().optional( ContentAttributesHelper.EDITORIAL_PROPERTY ).orElse( null );
                if ( editorial != null )
                {
                    return editorial.asString();
                }
            }
        }
        return null;
    }

    private String getLatestNodeVersionId( final Content content )
    {
        final Node node = this.nodeService.getById( NodeId.from( content.getId() ) );
        return node.getNodeVersionId().toString();
    }
}
