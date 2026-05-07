package com.enonic.xp.core.content;

import java.util.EnumSet;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.PatchContentParams;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.content.ReorderChildContentParams;
import com.enonic.xp.content.SortContentParams;
import com.enonic.xp.index.ChildOrder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ContentServiceImplTest_sort
    extends AbstractContentServiceTest
{
    @Test
    void publish_time_reset_on_sort()
    {
        final Content parent = createContent( ContentPath.ROOT, "parent" );
        createContent( parent.getPath(), "child1" );
        createContent( parent.getPath(), "child2" );

        this.contentService.publish(
            PushContentParams.create().contentIds( ContentIds.from( parent.getId() ) ).build() );

        final ContentPublishInfo publishInfoBeforeSort = this.contentService.getById( parent.getId() ).getPublishInfo();
        assertNotNull( publishInfoBeforeSort );
        assertNotNull( publishInfoBeforeSort.time() );

        this.contentService.sort(
            SortContentParams.create().contentId( parent.getId() ).childOrder( ChildOrder.manualOrder() ).build() );

        final ContentPublishInfo publishInfoAfterSort = this.contentService.getById( parent.getId() ).getPublishInfo();
        assertNotNull( publishInfoAfterSort );
        assertNull( publishInfoAfterSort.time() );
        assertNotNull( publishInfoAfterSort.from() );
        assertNotNull( publishInfoAfterSort.first() );
    }

    @Test
    void publish_time_reset_on_manual_reorder()
    {
        final Content parent = createContent( ContentPath.ROOT, "parent" );
        final Content child1 = createContent( parent.getPath(), "child1" );
        final Content child2 = createContent( parent.getPath(), "child2" );
        final Content child3 = createContent( parent.getPath(), "child3" );

        // Set Inherit.SORT on parent and children
        final EnumSet<ContentInheritType> inheritSet = EnumSet.of( ContentInheritType.SORT, ContentInheritType.CONTENT );
        setInherit( parent, inheritSet );
        setInherit( child1, inheritSet );
        setInherit( child3, inheritSet );

        this.contentService.publish(
            PushContentParams.create().contentIds( ContentIds.from( parent.getId() ) ).build() );

        // Set manual order first
        this.contentService.sort(
            SortContentParams.create().contentId( parent.getId() ).childOrder( ChildOrder.manualOrder() ).build() );

        // Re-publish to restore publish.time after the initial sort, and re-set inherit on parent (sort cleared it)
        setInherit( parent, inheritSet );
        this.contentService.publish( PushContentParams.create()
                                         .contentIds(
                                             ContentIds.from( parent.getId(), child1.getId(), child2.getId(), child3.getId() ) )
                                         .includeDependencies( false )
                                         .build() );

        final ContentPublishInfo child1PublishInfoBefore = this.contentService.getById( child1.getId() ).getPublishInfo();
        final ContentPublishInfo child3PublishInfoBefore = this.contentService.getById( child3.getId() ).getPublishInfo();
        assertNotNull( child1PublishInfoBefore.time() );
        assertNotNull( child3PublishInfoBefore.time() );

        // Verify inherit is set before reorder
        assertThat( this.contentService.getById( parent.getId() ).getInherit() ).contains( ContentInheritType.SORT );
        assertThat( this.contentService.getById( child3.getId() ).getInherit() ).contains( ContentInheritType.SORT );

        // Reorder: move child3 before child1 (child3 is last, moving to first)
        this.contentService.sort( SortContentParams.create()
                                      .contentId( parent.getId() )
                                      .childOrder( ChildOrder.manualOrder() )
                                      .addManualOrder( ReorderChildContentParams.create()
                                                           .contentToMove( child3.getId() )
                                                           .contentToMoveBefore( child1.getId() )
                                                           .build() )
                                      .build() );

        // Parent must lose Inherit.SORT
        assertThat( this.contentService.getById( parent.getId() ).getInherit() ).doesNotContain( ContentInheritType.SORT );

        // Reordered child must keep Inherit.SORT but lose publish.time
        final Content child3After = this.contentService.getById( child3.getId() );
        assertThat( child3After.getInherit() ).contains( ContentInheritType.SORT );
        assertNotNull( child3After.getPublishInfo() );
        assertNull( child3After.getPublishInfo().time() );
        assertNotNull( child3After.getPublishInfo().from() );
        assertNotNull( child3After.getPublishInfo().first() );

        // child1 was not reordered, so its publish.time and inherit should be preserved
        final Content child1After = this.contentService.getById( child1.getId() );
        assertThat( child1After.getInherit() ).contains( ContentInheritType.SORT );
        assertNotNull( child1After.getPublishInfo() );
        assertNotNull( child1After.getPublishInfo().time() );
    }

    private void setInherit( final Content content, final EnumSet<ContentInheritType> inheritSet )
    {
        this.contentService.patch(
            PatchContentParams.create().contentId( content.getId() ).patcher( edit -> edit.inherit.setValue( inheritSet ) ).build() );
    }
}
