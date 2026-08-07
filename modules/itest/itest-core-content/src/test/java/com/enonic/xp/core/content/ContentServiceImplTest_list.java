package com.enonic.xp.core.content;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentListEntry;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.ListContentsByParentParams;
import com.enonic.xp.content.ListContentsByParentResult;

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

        assertThat( result.getEntries() ).extracting( ContentListEntry::getId ).containsExactly( childA.getId(), childB.getId() );
        assertThat( result.getEntries() ).extracting( ContentListEntry::getPath )
            .containsExactly( childA.getPath(), childB.getPath() );
    }

    @Test
    void parent_id_lists_the_same()
    {
        final Content parent = createContent( ContentPath.ROOT, "parent" );
        final Content child = createContent( parent.getPath(), "child" );

        final ListContentsByParentResult result =
            contentService.list( ListContentsByParentParams.create().parentId( parent.getId() ).build() );

        assertThat( result.getEntries() ).extracting( ContentListEntry::getId ).containsExactly( child.getId() );
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

        assertThat( result.getEntries() ).extracting( ContentListEntry::getId )
            .containsExactly( childA.getId(), grandchild.getId(), childB.getId() );
    }

    @Test
    void root_lists_top_level_contents()
    {
        final Content top = createContent( ContentPath.ROOT, "top" );
        createContent( top.getPath(), "below" );

        final ListContentsByParentResult result =
            contentService.list( ListContentsByParentParams.create().parentPath( ContentPath.ROOT ).build() );

        assertThat( result.getEntries() ).extracting( ContentListEntry::getId ).containsExactly( top.getId() );
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
            assertThat( listed.getEntries() ).extracting( ContentListEntry::getId ).contains( pending.getId() );
        } );
    }
}
