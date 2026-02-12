package com.enonic.xp.core.content;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.DuplicateContentParams;
import com.enonic.xp.content.DuplicateContentsResult;
import com.enonic.xp.content.PublishContentResult;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.content.UnpublishContentParams;
import com.enonic.xp.content.UpdateContentMetadataParams;
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
        assertNotNull( storedContent.getPublishInfo().first() );
        assertNotNull( storedContent.getPublishInfo().from() );

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

        final ContentPublishInfo publishInfo = getPublishInfo( content.getId() );
        assertNotNull( publishInfo );
        assertNotNull( publishInfo.first() );
        assertNotNull( publishInfo.from() );

        final UpdateContentParams updateContentParams = new UpdateContentParams();
        updateContentParams.contentId( content.getId() ).editor( edit -> edit.displayName = "new display name" );

        this.contentService.update( updateContentParams );

        this.contentService.updateWorkflow(
            UpdateWorkflowParams.create().contentId( content.getId() ).editor( edit -> edit.workflow = WorkflowInfo.ready() ).build() );

        doPublishContent( content );
        assertVersions( content.getId(), 5 );

        final ContentPublishInfo unUpdatedPublishInfo = getPublishInfo( content.getId() );
        assertNotNull( unUpdatedPublishInfo );
        assertEquals( publishInfo.from(), unUpdatedPublishInfo.first() );
        assertEquals( publishInfo.from(), unUpdatedPublishInfo.from() );
    }

    @Test
    void published_time_is_reset_on_any_change()
    {
        final Content content = doCreateContent();
        final ContentId id = content.getId();
        final ContentPublishInfo publishInfo = getPublishInfo( id );
        assertNull( publishInfo );

        doPublishContent( content );

        final ContentPublishInfo updatedPublishInfo1 = getPublishInfo( id );
        assertNotNull( updatedPublishInfo1 );
        assertNotNull( updatedPublishInfo1.from() );
        assertEquals( updatedPublishInfo1.from(), updatedPublishInfo1.first() );
        assertNotNull( updatedPublishInfo1.time() );

        final UpdateContentParams updateContentParams = new UpdateContentParams();
        updateContentParams.contentId( content.getId() ).editor( edit -> edit.displayName = "new display name" );

        this.contentService.update( updateContentParams );

        final ContentPublishInfo updatedPublishInfo2 = getPublishInfo( id );

        assertNull( updatedPublishInfo2.time() );

        this.contentService.updateWorkflow(
            UpdateWorkflowParams.create().contentId( content.getId() ).editor( edit -> edit.workflow = WorkflowInfo.ready() ).build() );

        doPublishContent( content );

        final ContentPublishInfo updatedPublishInfo3 = getPublishInfo( id );

        assertNotNull( updatedPublishInfo3.time() );

        this.contentService.updateWorkflow( UpdateWorkflowParams.create()
                                                .contentId( content.getId() )
                                                .editor( edit -> edit.workflow = WorkflowInfo.inProgress() )
                                                .build() );

        final ContentPublishInfo updatedPublishInfo4 = getPublishInfo( id );

        assertNull( updatedPublishInfo4.time() );

        this.contentService.updateWorkflow(
            UpdateWorkflowParams.create().contentId( content.getId() ).editor( edit -> edit.workflow = WorkflowInfo.ready() ).build() );

        doPublishContent( content );

        final ContentPublishInfo updatedPublishInfo5 = getPublishInfo( id );

        assertNotNull( updatedPublishInfo5.time() );

        this.contentService.updateMetadata(
            UpdateContentMetadataParams.create().contentId( id ).editor( edit -> edit.language = Locale.CANADA ).build() );

        final ContentPublishInfo updatedPublishInfo6 = getPublishInfo( id );

        assertNull( updatedPublishInfo6.time() );
    }

    private ContentPublishInfo getPublishInfo( final ContentId id )
    {
        return this.contentService.getById( id ).getPublishInfo();
    }

    @Test
    void set_publish_time_again_if_reset()
    {
        final Content content = doCreateContent();
        doPublishContent( content );

        final ContentPublishInfo publishInfo = getPublishInfo( content.getId() );

        doUnpublishContent( content );

        doPublishContent( content );

        final ContentPublishInfo updatedPublishInfo = getPublishInfo( content.getId() );

        assertThat( updatedPublishInfo.from() ).isAfter( publishInfo.from() );
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

        final ContentPublishInfo publishInfo = getPublishInfo( content.getId() );

        assertNotNull( publishInfo.to() );
        assertThat( publishInfo.to() ).isAfter( publishInfo.from() );
    }

    private Content doDuplicateContent( final Content rootContent )
    {
        final DuplicateContentParams params = DuplicateContentParams.create().contentId( rootContent.getId() ).build();
        final DuplicateContentsResult result = contentService.duplicate( params );

        return this.contentService.getById( result.getDuplicatedContents().first() );
    }

    private void doPublishContent( final Content content )
    {
        final PublishContentResult result = this.contentService.publish(
            PushContentParams.create().contentIds( ContentIds.from( content.getId() ) ).includeDependencies( false ).build() );
        assertThat( result.getFailed() ).isEmpty();
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
