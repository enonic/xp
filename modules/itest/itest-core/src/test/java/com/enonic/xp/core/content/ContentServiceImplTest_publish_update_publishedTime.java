package com.enonic.xp.core.content;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.DuplicateContentParams;
import com.enonic.xp.content.DuplicateContentsResult;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.content.UnpublishContentParams;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.UpdateWorkflowParams;
import com.enonic.xp.content.WorkflowInfo;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ContentServiceImplTest_publish_update_publishedTime
    extends AbstractContentServiceTest
{

    @Test
    void set_published_time()
    {
        final Content content = doCreateContent();
        assertNull( content.getPublishInfo() );
        assertVersions( content.getId(), 1 );

        doPublishContent( content );
        assertVersions( content.getId(), 2 );

        final Content storedContent = this.contentService.getById( content.getId() );
        assertNotNull( storedContent.getPublishInfo() );
        assertNotNull( storedContent.getPublishInfo().getFirst() );
        assertNotNull( storedContent.getPublishInfo().getFrom() );

        final Content publishedContent = ContextBuilder.from( ContextAccessor.current() )
            .branch( ContentConstants.BRANCH_MASTER )
            .build()
            .callWith( () -> this.contentService.getById( content.getId() ) );

        assertEquals( storedContent.getPublishInfo(), publishedContent.getPublishInfo() );
    }

    @Test
    void keep_original_published_time()
    {
        final Content content = doCreateContent();

        doPublishContent( content );
        assertVersions( content.getId(), 2 );

        final ContentPublishInfo publishInfo = this.contentService.getById( content.getId() ).getPublishInfo();
        assertNotNull( publishInfo );
        assertNotNull( publishInfo.getFirst() );
        assertNotNull( publishInfo.getFrom() );

        final UpdateContentParams updateContentParams = new UpdateContentParams();
        updateContentParams.contentId( content.getId() ).editor( edit -> edit.displayName = "new display name" );

        this.contentService.update( updateContentParams );
        this.contentService.updateWorkflow(
            UpdateWorkflowParams.create().contentId( content.getId() ).editor( edit -> edit.workflow = WorkflowInfo.ready() ).build() );

        doPublishContent( content );
        assertVersions( content.getId(), 5 );

        final ContentPublishInfo unUpdatedPublishInfo = this.contentService.getById( content.getId() ).getPublishInfo();
        assertEquals( publishInfo, unUpdatedPublishInfo );
    }

    @Test
    void set_publish_time_again_if_reset()
    {
        final Content content = doCreateContent();
        doPublishContent( content );

        final ContentPublishInfo publishInfo = this.contentService.getById( content.getId() ).getPublishInfo();

        doUnpublishContent( content );

        doPublishContent( content );

        final ContentPublishInfo updatedPublishInfo = this.contentService.getById( content.getId() ).getPublishInfo();

        assertThat( updatedPublishInfo.getFrom() ).isAfter( publishInfo.getFrom() );
    }

    @Test
    void publish_info_is_removed_on_duplicate()
    {

        final Content rootContent = createContent( ContentPath.ROOT );
        this.contentService.publish( PushContentParams.create().contentIds( ContentIds.from( rootContent.getId() ) ).build() );

        final Content duplicateContent = doDuplicateContent( rootContent );

        assertNull( duplicateContent.getPublishInfo() );
    }

    @Test
    void set_published_from_now_keeps_published_to()
    {
        final Content content = doCreateContent();

        this.contentService.publish( PushContentParams.create()
                                         .contentIds( ContentIds.from( content.getId() ) )
                                         .publishTo( Instant.now().plus( 1, ChronoUnit.DAYS ) )
                                         .build() );

        final ContentPublishInfo publishInfo = this.contentService.getById( content.getId() ).getPublishInfo();

        assertNotNull( publishInfo.getTo() );
        assertThat( publishInfo.getTo() ).isAfter( publishInfo.getFrom() );
    }

    private Content doDuplicateContent( final Content rootContent )
    {
        final DuplicateContentParams params = DuplicateContentParams.create().contentId( rootContent.getId() ).build();
        final DuplicateContentsResult result = contentService.duplicate( params );

        return this.contentService.getById( result.getDuplicatedContents().first() );
    }

    private void doPublishContent( final Content content )
    {
        this.contentService.publish(
            PushContentParams.create().contentIds( ContentIds.from( content.getId() ) ).includeDependencies( false ).build() );
    }

    private void doUnpublishContent( final Content content )
    {
        this.contentService.unpublish( UnpublishContentParams.create().contentIds( ContentIds.from( content.getId() ) ).build() );
    }

    private Content doCreateContent()
    {
        final CreateContentParams createContentParams = CreateContentParams.create()
            .contentData( new PropertyTree() )
            .displayName( "This is my content" )
            .name( "myContent" )
            .parent( ContentPath.ROOT )
            .type( ContentTypeName.folder() )
            .build();

        return this.contentService.create( createContentParams );
    }

}
