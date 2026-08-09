package com.enonic.xp.core.content;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.archive.ArchiveContentParams;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentListEntry;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.ListContentsByParentParams;
import com.enonic.xp.content.ListContentsByParentResult;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.NodePath;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContentServiceImplTest_list
    extends AbstractContentServiceTest
{
    @Test
    void parent_path_lists_direct_children_ordered_by_path()
    {
        final Content parent = createContent( ContentPath.ROOT, "parent" );
        final Content childB = createContent( parent.getPath(), "b" );
        final Content childA = createContent( parent.getPath(), "a" );
        createContent( childA.getPath(), "grandchild" );
        createContent( ContentPath.ROOT, "outside" );

        final ListContentsByParentResult result =
            contentService.list( ListContentsByParentParams.create().parentPath( parent.getPath() ).build() );

        assertThat( result.getEntries() ).extracting( ContentListEntry::id ).containsExactly( childA.getId(), childB.getId() );
        assertThat( result.getEntries() ).extracting( ContentListEntry::path )
            .containsExactly( childA.getPath(), childB.getPath() );
    }

    @Test
    void parent_id_lists_the_same()
    {
        final Content parent = createContent( ContentPath.ROOT, "parent" );
        final Content child = createContent( parent.getPath(), "child" );

        final ListContentsByParentResult result =
            contentService.list( ListContentsByParentParams.create().parentId( parent.getId() ).build() );

        assertThat( result.getEntries() ).extracting( ContentListEntry::id ).containsExactly( child.getId() );
    }

    @Test
    void recursive_lists_whole_subtree()
    {
        final Content parent = createContent( ContentPath.ROOT, "parent" );
        final Content childA = createContent( parent.getPath(), "a" );
        final Content childB = createContent( parent.getPath(), "b" );
        final Content grandchild = createContent( childA.getPath(), "grandchild" );

        final ListContentsByParentResult result =
            contentService.list( ListContentsByParentParams.create().parentPath( parent.getPath() ).recursive( true ).build() );

        assertThat( result.getEntries() ).extracting( ContentListEntry::id )
            .containsExactly( childA.getId(), grandchild.getId(), childB.getId() );
    }

    @Test
    void root_lists_top_level_contents()
    {
        final Content top = createContent( ContentPath.ROOT, "top" );
        createContent( top.getPath(), "below" );

        final ListContentsByParentResult result =
            contentService.list( ListContentsByParentParams.create().parentPath( ContentPath.ROOT ).build() );

        assertThat( result.getEntries() ).extracting( ContentListEntry::id ).containsExactly( top.getId() );
    }

    @Test
    void parent_id_that_does_not_exist_lists_nothing()
    {
        createContent( ContentPath.ROOT, "parent" );

        final ListContentsByParentResult result =
            contentService.list( ListContentsByParentParams.create().parentId( ContentId.from( "no-such-content" ) ).build() );

        assertTrue( result.isEmpty() );
    }

    @Test
    void publish_times_are_not_evaluated_on_master()
    {
        ctxMaster().runWith( () -> {
            final Content pending = createAndPublishContent( ContentPath.ROOT, Instant.now().plus( Duration.ofDays( 1 ) ) );

            // a search on master filters the pending content out...
            assertEquals( 0, contentService.find( ContentQuery.create().parentPath( ContentPath.ROOT ).build() ).getTotalHits() );

            // ...but an enumeration reads branch storage only and does not evaluate publish times
            final ListContentsByParentResult listed =
                contentService.list( ListContentsByParentParams.create().parentPath( ContentPath.ROOT ).build() );
            assertThat( listed.getEntries() ).extracting( ContentListEntry::id ).contains( pending.getId() );
        } );
    }

    @Test
    void archive_context_lists_below_the_archive_root()
    {
        final Content parent = createContent( ContentPath.ROOT, "parent" );
        final Content child = createContent( parent.getPath(), "child" );

        contentService.archive( ArchiveContentParams.create().contentId( parent.getId() ).build() );

        archiveContext().runWith( () -> {
            // a content path means whatever the content root of the context says it means, here /archive rather than /content
            final ListContentsByParentResult archived =
                contentService.list( ListContentsByParentParams.create().parentPath( ContentPath.ROOT ).recursive( true ).build() );

            assertThat( archived.getContentIds() ).contains( parent.getId(), child.getId() );
            assertThat( archived.getEntries() ).extracting( ContentListEntry::path )
                .allSatisfy( path -> assertTrue( path.toString().startsWith( "/" ) ) );
        } );

        // the same listing outside the archive context sees nothing of it
        assertTrue( contentService.list( ListContentsByParentParams.create().parentPath( ContentPath.ROOT ).recursive( true ).build() )
                        .getContentIds()
                        .stream()
                        .noneMatch( id -> id.equals( parent.getId() ) ) );
    }

    @Test
    void a_parent_id_outside_the_content_root_of_the_context_lists_nothing()
    {
        final Content parent = createContent( ContentPath.ROOT, "parent" );
        createContent( parent.getPath(), "child" );

        contentService.archive( ArchiveContentParams.create().contentId( parent.getId() ).build() );

        // the id still resolves, but it now lives under /archive - listing it from the content context must not reach across
        assertTrue( contentService.list( ListContentsByParentParams.create().parentId( parent.getId() ).build() ).isEmpty() );

        archiveContext().runWith(
            () -> assertThat( contentService.list( ListContentsByParentParams.create().parentId( parent.getId() ).build() )
                                  .getEntries() ).isNotEmpty() );
    }

    private Context archiveContext()
    {
        return ContextBuilder.from( ContextAccessor.current() )
            .attribute( ContentConstants.CONTENT_ROOT_PATH_ATTRIBUTE, new NodePath( "/archive" ) )
            .build();
    }
}
