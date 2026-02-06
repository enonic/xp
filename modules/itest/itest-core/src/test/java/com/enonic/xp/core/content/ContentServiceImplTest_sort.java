package com.enonic.xp.core.content;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentVersion;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.FindContentVersionsParams;
import com.enonic.xp.content.FindContentVersionsResult;
import com.enonic.xp.content.ReorderChildContentParams;
import com.enonic.xp.content.SortContentParams;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ContentServiceImplTest_sort
    extends AbstractContentServiceTest
{
    @Test
    void manual_sort_sets_version_attributes_on_parent()
    {
        final Content parent = this.contentService.create( CreateContentParams.create()
                                                               .contentData( new PropertyTree() )
                                                               .displayName( "parent" )
                                                               .parent( ContentPath.ROOT )
                                                               .name( "parent" )
                                                               .type( ContentTypeName.folder() )
                                                               .build() );

        final Content child1 = this.contentService.create( CreateContentParams.create()
                                                               .contentData( new PropertyTree() )
                                                               .displayName( "child1" )
                                                               .parent( parent.getPath() )
                                                               .name( "child1" )
                                                               .type( ContentTypeName.folder() )
                                                               .build() );

        final Content child2 = this.contentService.create( CreateContentParams.create()
                                                               .contentData( new PropertyTree() )
                                                               .displayName( "child2" )
                                                               .parent( parent.getPath() )
                                                               .name( "child2" )
                                                               .type( ContentTypeName.folder() )
                                                               .build() );

        // Sort parent to manual order
        this.contentService.sort( SortContentParams.create()
                                      .contentId( parent.getId() )
                                      .childOrder( ChildOrder.manualOrder() )
                                      .build() );

        // Get versions of parent
        final FindContentVersionsResult parentVersions =
            this.contentService.getVersions( FindContentVersionsParams.create().contentId( parent.getId() ).build() );

        assertEquals( 2, parentVersions.getContentVersions().getSize(), "Parent should have 2 versions: create + sort" );

        final ContentVersion sortVersion = parentVersions.getContentVersions().first();
        assertThat( sortVersion.getActions() ).extracting( ContentVersion.Action::operation ).contains( "content.sort" );
    }

    @Test
    void manual_sort_sets_version_attributes_on_children()
    {
        final Content parent = this.contentService.create( CreateContentParams.create()
                                                               .contentData( new PropertyTree() )
                                                               .displayName( "parent" )
                                                               .parent( ContentPath.ROOT )
                                                               .name( "parent" )
                                                               .type( ContentTypeName.folder() )
                                                               .build() );

        final Content child1 = this.contentService.create( CreateContentParams.create()
                                                               .contentData( new PropertyTree() )
                                                               .displayName( "child1" )
                                                               .parent( parent.getPath() )
                                                               .name( "child1" )
                                                               .type( ContentTypeName.folder() )
                                                               .build() );

        final Content child2 = this.contentService.create( CreateContentParams.create()
                                                               .contentData( new PropertyTree() )
                                                               .displayName( "child2" )
                                                               .parent( parent.getPath() )
                                                               .name( "child2" )
                                                               .type( ContentTypeName.folder() )
                                                               .build() );

        // Sort parent to manual order
        this.contentService.sort( SortContentParams.create()
                                      .contentId( parent.getId() )
                                      .childOrder( ChildOrder.manualOrder() )
                                      .build() );

        // Get versions of children
        final FindContentVersionsResult child1Versions =
            this.contentService.getVersions( FindContentVersionsParams.create().contentId( child1.getId() ).build() );
        final FindContentVersionsResult child2Versions =
            this.contentService.getVersions( FindContentVersionsParams.create().contentId( child2.getId() ).build() );

        assertEquals( 2, child1Versions.getContentVersions().getSize(), "Child1 should have 2 versions: create + manual sort" );
        assertEquals( 2, child2Versions.getContentVersions().getSize(), "Child2 should have 2 versions: create + manual sort" );

        final ContentVersion child1SortVersion = child1Versions.getContentVersions().first();
        final ContentVersion child2SortVersion = child2Versions.getContentVersions().first();

        // Check that the sort version has content.sort action
        assertThat( child1SortVersion.getActions() ).extracting( ContentVersion.Action::operation ).contains( "content.sort" );
        assertThat( child2SortVersion.getActions() ).extracting( ContentVersion.Action::operation ).contains( "content.sort" );

        // Check that the sort version has manualOrderValue field
        assertThat( child1SortVersion.getActions() )
            .filteredOn( action -> "content.sort".equals( action.operation() ) )
            .flatExtracting( ContentVersion.Action::fields )
            .contains( "manualOrderValue" );

        assertThat( child2SortVersion.getActions() )
            .filteredOn( action -> "content.sort".equals( action.operation() ) )
            .flatExtracting( ContentVersion.Action::fields )
            .contains( "manualOrderValue" );
    }

    @Test
    void manual_reorder_sets_version_attributes_on_moved_children()
    {
        final Content parent = this.contentService.create( CreateContentParams.create()
                                                               .contentData( new PropertyTree() )
                                                               .displayName( "parent" )
                                                               .parent( ContentPath.ROOT )
                                                               .name( "parent" )
                                                               .type( ContentTypeName.folder() )
                                                               .build() );

        final Content child1 = this.contentService.create( CreateContentParams.create()
                                                               .contentData( new PropertyTree() )
                                                               .displayName( "child1" )
                                                               .parent( parent.getPath() )
                                                               .name( "child1" )
                                                               .type( ContentTypeName.folder() )
                                                               .build() );

        final Content child2 = this.contentService.create( CreateContentParams.create()
                                                               .contentData( new PropertyTree() )
                                                               .displayName( "child2" )
                                                               .parent( parent.getPath() )
                                                               .name( "child2" )
                                                               .type( ContentTypeName.folder() )
                                                               .build() );

        // First sort to manual order
        this.contentService.sort( SortContentParams.create()
                                      .contentId( parent.getId() )
                                      .childOrder( ChildOrder.manualOrder() )
                                      .build() );

        // Now reorder children
        this.contentService.sort( SortContentParams.create()
                                      .contentId( parent.getId() )
                                      .childOrder( ChildOrder.manualOrder() )
                                      .addManualOrder( ReorderChildContentParams.create()
                                                           .contentToMove( child2.getId() )
                                                           .contentToMoveBefore( child1.getId() )
                                                           .build() )
                                      .build() );

        // Get versions of child2 which was moved
        final FindContentVersionsResult child2Versions =
            this.contentService.getVersions( FindContentVersionsParams.create().contentId( child2.getId() ).build() );

        assertEquals( 3, child2Versions.getContentVersions().getSize(), "Child2 should have 3 versions: create + initial sort + reorder" );

        final ContentVersion reorderVersion = child2Versions.getContentVersions().first();

        // Check that the reorder version has content.sort action
        assertThat( reorderVersion.getActions() ).extracting( ContentVersion.Action::operation ).contains( "content.sort" );

        // Check that the reorder version has manualOrderValue field
        assertThat( reorderVersion.getActions() )
            .filteredOn( action -> "content.sort".equals( action.operation() ) )
            .flatExtracting( ContentVersion.Action::fields )
            .contains( "manualOrderValue" );
    }
}
