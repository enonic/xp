package com.enonic.xp.core.content;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import com.enonic.xp.archive.ArchiveContentParams;
import com.enonic.xp.archive.RestoreContentParams;
import com.enonic.xp.content.ApplyContentPermissionsParams;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentVersion;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.DuplicateContentParams;
import com.enonic.xp.content.DuplicateContentsResult;
import com.enonic.xp.content.FindContentVersionsParams;
import com.enonic.xp.content.FindContentVersionsResult;
import com.enonic.xp.content.MoveContentParams;
import com.enonic.xp.content.PatchContentParams;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.content.ReorderChildContentParams;
import com.enonic.xp.content.SortContentParams;
import com.enonic.xp.content.UnpublishContentParams;
import com.enonic.xp.content.UpdateContentMetadataParams;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.UpdateWorkflowParams;
import com.enonic.xp.content.WorkflowInfo;
import com.enonic.xp.content.WorkflowState;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

import static com.enonic.xp.content.ContentConstants.CONTENT_ROOT_PATH_ATTRIBUTE;
import static org.assertj.core.api.Assertions.assertThat;

class ContentServiceImplTest_versionAttributes
    extends AbstractContentServiceTest
{
    // --- Create ---

    @Test
    void create_sets_create_attr()
    {
        final Content content = createFolder( "content", ContentPath.ROOT );

        final ContentVersion version = getLatestVersion( content );

        assertThat( version.getActions() ).extracting( ContentVersion.Action::operation ).containsExactly( "content.create" );
    }

    // --- Sort ---

    @Test
    void sort_to_manual_order_sets_sort_attr_with_childOrder_on_parent()
    {
        final Content parent = createFolder( "parent", ContentPath.ROOT );
        createFolder( "child1", parent.getPath() );
        createFolder( "child2", parent.getPath() );

        this.contentService.sort( SortContentParams.create().contentId( parent.getId() ).childOrder( ChildOrder.manualOrder() ).build() );

        final ContentVersion sortVersion = getLatestVersion( parent );

        assertThat( sortVersion.getActions() ).extracting( ContentVersion.Action::operation ).containsExactly( "content.sort" );

        assertThat( sortVersion.getActions() ).filteredOn( action -> "content.sort".equals( action.operation() ) )
            .flatExtracting( ContentVersion.Action::fields )
            .containsExactly( "childOrder" );
    }

    @Test
    void sort_to_manual_order_sets_sort_attr_with_manualOrderValue_on_children()
    {
        final Content parent = createFolder( "parent", ContentPath.ROOT );
        final Content child1 = createFolder( "child1", parent.getPath() );
        final Content child2 = createFolder( "child2", parent.getPath() );

        this.contentService.sort( SortContentParams.create().contentId( parent.getId() ).childOrder( ChildOrder.manualOrder() ).build() );

        for ( final Content child : new Content[]{child1, child2} )
        {
            final ContentVersion childSortVersion = getLatestVersion( child );

            assertThat( childSortVersion.getActions() ).extracting( ContentVersion.Action::operation ).containsExactly( "content.sort" );

            assertThat( childSortVersion.getActions() ).filteredOn( action -> "content.sort".equals( action.operation() ) )
                .flatExtracting( ContentVersion.Action::fields )
                .containsExactly( "manualOrderValue" );
        }
    }

    @Test
    void manual_reorder_sets_sort_attr_with_manualOrderValue_on_reordered_child()
    {
        final Content parent = createFolder( "parent", ContentPath.ROOT );
        final Content child1 = createFolder( "child1", parent.getPath() );
        final Content child2 = createFolder( "child2", parent.getPath() );
        final Content child3 = createFolder( "child3", parent.getPath() );

        this.contentService.sort( SortContentParams.create().contentId( parent.getId() ).childOrder( ChildOrder.manualOrder() ).build() );

        this.contentService.sort( SortContentParams.create()
                                      .contentId( parent.getId() )
                                      .childOrder( ChildOrder.manualOrder() )
                                      .addManualOrder( ReorderChildContentParams.create()
                                                           .contentToMove( child3.getId() )
                                                           .contentToMoveBefore( child1.getId() )
                                                           .build() )
                                      .build() );

        final FindContentVersionsResult child3Versions =
            this.contentService.getVersions( FindContentVersionsParams.create().contentId( child3.getId() ).build() );

        assertThat( child3Versions.getContentVersions() ).hasSize( 3 );

        final ContentVersion reorderVersion = child3Versions.getContentVersions().first();

        assertThat( reorderVersion.getActions() ).extracting( ContentVersion.Action::operation ).containsExactly( "content.sort" );

        assertThat( reorderVersion.getActions() ).filteredOn( action -> "content.sort".equals( action.operation() ) )
            .flatExtracting( ContentVersion.Action::fields )
            .containsExactly( "manualOrderValue" );
    }

    // --- Move ---

    @Test
    void move_sets_move_attr_with_parentPath_on_parent()
    {
        final Content source = createFolder( "source", ContentPath.ROOT );
        final Content child = createFolder( "child", source.getPath() );
        final Content target = createFolder( "target", ContentPath.ROOT );

        this.contentService.move( MoveContentParams.create().contentId( child.getId() ).parentContentPath( target.getPath() ).build() );

        final ContentVersion moveVersion = getLatestVersion( child );

        assertThat( moveVersion.getActions() ).extracting( ContentVersion.Action::operation ).containsExactly( "content.move" );

        assertThat( moveVersion.getActions() ).filteredOn( action -> "content.move".equals( action.operation() ) )
            .flatExtracting( ContentVersion.Action::fields )
            .containsExactly( "parentPath" );
    }

    @Test
    void move_sets_move_attr_with_parentPath_and_name_when_renamed()
    {
        final Content source = createFolder( "source", ContentPath.ROOT );
        final Content child = createFolder( "child", source.getPath() );
        final Content target = createFolder( "target", ContentPath.ROOT );

        this.contentService.move( MoveContentParams.create()
                                      .contentId( child.getId() )
                                      .parentContentPath( target.getPath() )
                                      .newName( ContentName.from( "renamed" ) )
                                      .build() );

        final ContentVersion moveVersion = getLatestVersion( child );

        assertThat( moveVersion.getActions() ).extracting( ContentVersion.Action::operation ).containsExactly( "content.move" );

        assertThat( moveVersion.getActions() ).filteredOn( action -> "content.move".equals( action.operation() ) )
            .flatExtracting( ContentVersion.Action::fields )
            .containsExactlyInAnyOrder( "parentPath", "name" );
    }

    @Test
    void move_sets_move_attr_with_parentPath_on_children()
    {
        final Content source = createFolder( "source", ContentPath.ROOT );
        final Content parent = createFolder( "parent", source.getPath() );
        final Content child1 = createFolder( "child1", parent.getPath() );
        final Content child2 = createFolder( "child2", parent.getPath() );
        final Content grandchild = createFolder( "grandchild", child1.getPath() );
        final Content target = createFolder( "target", ContentPath.ROOT );

        this.contentService.move( MoveContentParams.create().contentId( parent.getId() ).parentContentPath( target.getPath() ).build() );

        final ContentVersion parentMoveVersion = getLatestVersion( parent );
        assertThat( parentMoveVersion.getActions() ).extracting( ContentVersion.Action::operation ).containsExactly( "content.move" );
        assertThat( parentMoveVersion.getActions() ).filteredOn( action -> "content.move".equals( action.operation() ) )
            .flatExtracting( ContentVersion.Action::fields )
            .containsExactly( "parentPath" );

        for ( final Content child : new Content[]{child1, child2, grandchild} )
        {
            final ContentVersion childMoveVersion = getLatestVersion( child );

            assertThat( childMoveVersion.getActions() ).as( "child %s actions", child.getName() )
                .extracting( ContentVersion.Action::operation )
                .containsExactly( "content.move" );

            assertThat( childMoveVersion.getActions() ).as( "child %s fields", child.getName() )
                .filteredOn( action -> "content.move".equals( action.operation() ) )
                .flatExtracting( ContentVersion.Action::fields )
                .containsExactly( "parentPath" );
        }
    }

    @Test
    void move_with_rename_children_still_get_parentPath_only()
    {
        final Content source = createFolder( "source", ContentPath.ROOT );
        final Content parent = createFolder( "parent", source.getPath() );
        final Content child = createFolder( "child", parent.getPath() );
        final Content target = createFolder( "target", ContentPath.ROOT );

        this.contentService.move( MoveContentParams.create()
                                      .contentId( parent.getId() )
                                      .parentContentPath( target.getPath() )
                                      .newName( ContentName.from( "renamed" ) )
                                      .build() );

        final ContentVersion parentMoveVersion = getLatestVersion( parent );
        assertThat( parentMoveVersion.getActions() ).filteredOn( action -> "content.move".equals( action.operation() ) )
            .flatExtracting( ContentVersion.Action::fields )
            .containsExactlyInAnyOrder( "parentPath", "name" );

        final ContentVersion childMoveVersion = getLatestVersion( child );
        assertThat( childMoveVersion.getActions() ).extracting( ContentVersion.Action::operation ).containsExactly( "content.move" );
        assertThat( childMoveVersion.getActions() ).filteredOn( action -> "content.move".equals( action.operation() ) )
            .flatExtracting( ContentVersion.Action::fields )
            .containsExactly( "parentPath" );
    }

    // --- Archive ---

    @Test
    void archive_sets_archive_attr()
    {
        final Content content = createFolder( "content", ContentPath.ROOT );

        this.contentService.archive( ArchiveContentParams.create().contentId( content.getId() ).build() );

        final ContentVersion archiveVersion = archiveContext().callWith( () -> getLatestVersion( content ) );

        assertThat( archiveVersion.getActions() ).extracting( ContentVersion.Action::operation ).containsExactly( "content.archive" );
    }

    @Test
    void archive_sets_archive_attr_on_children()
    {
        final Content parent = createFolder( "parent", ContentPath.ROOT );
        final Content child = createFolder( "child", parent.getPath() );

        this.contentService.archive( ArchiveContentParams.create().contentId( parent.getId() ).build() );

        final ContentVersion childVersion = archiveContext().callWith( () -> getLatestVersion( child ) );

        assertThat( childVersion.getActions() ).extracting( ContentVersion.Action::operation ).containsExactly( "content.archive" );
    }

    // --- Restore ---

    @Test
    void restore_sets_restore_attr()
    {
        final Content content = createFolder( "content", ContentPath.ROOT );
        this.contentService.archive( ArchiveContentParams.create().contentId( content.getId() ).build() );

        this.contentService.restore( RestoreContentParams.create().contentId( content.getId() ).build() );

        final ContentVersion restoreVersion = getLatestVersion( content );

        assertThat( restoreVersion.getActions() ).extracting( ContentVersion.Action::operation ).containsExactly( "content.restore" );
    }

    @Test
    void restore_sets_restore_attr_on_children()
    {
        final Content parent = createFolder( "parent", ContentPath.ROOT );
        final Content child = createFolder( "child", parent.getPath() );
        this.contentService.archive( ArchiveContentParams.create().contentId( parent.getId() ).build() );

        this.contentService.restore( RestoreContentParams.create().contentId( parent.getId() ).build() );

        final ContentVersion childVersion = getLatestVersion( child );

        assertThat( childVersion.getActions() ).extracting( ContentVersion.Action::operation ).containsExactly( "content.restore" );
    }

    // --- Apply permissions ---

    @Test
    void applyPermissions_sets_permissions_attr()
    {
        final Content content = createFolder( "content", ContentPath.ROOT );

        this.contentService.applyPermissions( ApplyContentPermissionsParams.create()
                                                  .contentId( content.getId() )
                                                  .addPermissions( AccessControlList.of( AccessControlEntry.create()
                                                                                             .principal( TEST_DEFAULT_USER.getKey() )
                                                                                             .allow( Permission.READ )
                                                                                             .build() ) )
                                                  .build() );

        final ContentVersion version = getLatestVersion( content );

        assertThat( version.getActions() ).extracting( ContentVersion.Action::operation ).containsExactly( "content.permissions" );
    }

    // --- Duplicate ---

    @Test
    void duplicate_sets_duplicate_attr()
    {
        final Content parent = createFolder( "parent", ContentPath.ROOT );
        final Content child = createFolder( "child", parent.getPath() );
        createFolder( "grandchild", child.getPath() );

        final DuplicateContentsResult result =
            this.contentService.duplicate( DuplicateContentParams.create().contentId( parent.getId() ).build() );

        for ( final ContentId duplicatedId : result.getDuplicatedContents() )
        {
            final ContentVersion version = getLatestVersion( duplicatedId );

            assertThat( version.getActions() ).as( "actions for duplicated content %s", duplicatedId )
                .extracting( ContentVersion.Action::operation )
                .containsExactly( "content.duplicate" );
        }
    }

    // --- Patch ---

    @Test
    void patch_sets_patch_attr()
    {
        final Content content = createFolder( "content", ContentPath.ROOT );

        this.contentService.patch(
            PatchContentParams.create().contentId( content.getId() ).patcher( edit -> edit.displayName.setValue( "patched" ) ).build() );

        final ContentVersion version = getLatestVersion( content );

        assertThat( version.getActions() ).extracting( ContentVersion.Action::operation ).containsExactly( "content.patch" );
    }

    // --- Publish ---

    @Test
    void publish_sets_publish_attr()
    {
        final Content content = createFolder( "content", ContentPath.ROOT );

        this.contentService.publish(
            PushContentParams.create().contentIds( ContentIds.from( content.getId() ) ).includeDependencies( false ).build() );

        final ContentVersion version = getLatestVersion( content );

        assertThat( version.getActions() ).extracting( ContentVersion.Action::operation ).containsExactly( "content.publish" );
    }

    // --- Unpublish ---

    @Test
    void unpublish_sets_unpublish_attr()
    {
        final Content content = createFolder( "content", ContentPath.ROOT );
        this.contentService.publish(
            PushContentParams.create().contentIds( ContentIds.from( content.getId() ) ).includeDependencies( false ).build() );

        this.contentService.unpublish( UnpublishContentParams.create().contentIds( ContentIds.from( content.getId() ) ).build() );

        final ContentVersion version = getLatestVersion( content );

        assertThat( version.getActions() ).extracting( ContentVersion.Action::operation ).contains( "content.unpublish" );
    }

    // --- Update ---

    @Test
    void update_sets_update_attr_with_modified_fields()
    {
        final Content content = createFolder( "content", ContentPath.ROOT );

        final UpdateContentParams updateParams = new UpdateContentParams();
        updateParams.contentId( content.getId() ).editor( edit -> edit.displayName = "updated" );
        this.contentService.update( updateParams );

        final ContentVersion version = getLatestVersion( content );

        assertThat( version.getActions() ).extracting( ContentVersion.Action::operation ).containsExactly( "content.update" );

        assertThat( version.getActions() ).filteredOn( action -> "content.update".equals( action.operation() ) )
            .flatExtracting( ContentVersion.Action::fields )
            .contains( "displayName" );
    }

    // --- Update workflow ---

    @Test
    void updateWorkflow_sets_updateWorkflow_attr()
    {
        final Content content = createFolder( "content", ContentPath.ROOT );

        this.contentService.updateWorkflow( UpdateWorkflowParams.create()
                                                .contentId( content.getId() )
                                                .editor( edit -> edit.workflow =
                                                    WorkflowInfo.create().state( WorkflowState.PENDING_APPROVAL ).build() )
                                                .build() );

        final ContentVersion version = getLatestVersion( content );

        assertThat( version.getActions() ).extracting( ContentVersion.Action::operation ).containsExactly( "content.updateWorkflow" );

        assertThat( version.getActions() ).filteredOn( action -> "content.updateWorkflow".equals( action.operation() ) )
            .flatExtracting( ContentVersion.Action::fields )
            .contains( "workflow" );
    }

    // --- Update metadata ---

    @Test
    void updateMetadata_sets_updateMetadata_attr()
    {
        final Content content = createFolder( "content", ContentPath.ROOT );

        this.contentService.updateMetadata(
            UpdateContentMetadataParams.create().contentId( content.getId() ).editor( edit -> edit.language = Locale.ENGLISH ).build() );

        final ContentVersion version = getLatestVersion( content );

        assertThat( version.getActions() ).extracting( ContentVersion.Action::operation ).containsExactly( "content.updateMetadata" );

        assertThat( version.getActions() ).filteredOn( action -> "content.updateMetadata".equals( action.operation() ) )
            .flatExtracting( ContentVersion.Action::fields )
            .contains( "language" );
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

    private ContentVersion getLatestVersion( final Content content )
    {
        return getLatestVersion( content.getId() );
    }

    private ContentVersion getLatestVersion( final ContentId contentId )
    {
        final FindContentVersionsResult versions =
            this.contentService.getVersions( FindContentVersionsParams.create().contentId( contentId ).build() );
        return versions.getContentVersions().first();
    }

    private Context archiveContext()
    {
        return ContextBuilder.from( ContextAccessor.current() )
            .attribute( CONTENT_ROOT_PATH_ATTRIBUTE, new NodePath( "/archive" ) )
            .build();
    }
}
