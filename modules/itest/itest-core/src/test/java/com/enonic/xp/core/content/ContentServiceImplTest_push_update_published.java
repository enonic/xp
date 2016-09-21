package com.enonic.xp.core.content;

import java.time.Instant;

import org.junit.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.junit.Assert.*;

public class ContentServiceImplTest_push_update_published
    extends AbstractContentServiceTest
{
    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();
    }

    @Test
    public void set_published_time()
        throws Exception
    {
        final Content content = doCreateContent();
        assertNull( content.getPublishedTime() );

        doPublishContent( content );

        final Content storedContent = this.contentService.getById( content.getId() );
        assertNotNull( storedContent.getPublishedTime() );

        final Content publishedContent = ContextBuilder.from( ContextAccessor.current() ).
            branch( WS_OTHER ).
            build().
            callWith( () -> this.contentService.getById( content.getId() ) );
        assertNotNull( publishedContent.getPublishedTime() );

        assertEquals( storedContent.getPublishedTime(), publishedContent.getPublishedTime() );
    }

    @Test
    public void keep_original_published_time()
        throws Exception
    {
        final Content content = doCreateContent();

        doPublishContent( content );

        final Instant originalPublishTime = this.contentService.getById( content.getId() ).getPublishedTime();

        final UpdateContentParams updateContentParams = new UpdateContentParams();
        updateContentParams.contentId( content.getId() ).
            editor( edit -> {
                edit.displayName = "new display name";
            } );

        this.contentService.update( updateContentParams );

        doPublishContent( content );

        final Instant unUpdatedPublishTime = this.contentService.getById( content.getId() ).getPublishedTime();

        assertEquals( originalPublishTime, unUpdatedPublishTime );
    }


    @Test
    public void set_publish_time_again_if_reset()
        throws Exception
    {
        final Content content = doCreateContent();

        doPublishContent( content );

        final Instant originalPublishTime = this.contentService.getById( content.getId() ).getPublishedTime();

        final UpdateContentParams updateContentParams = new UpdateContentParams();
        updateContentParams.contentId( content.getId() ).
            editor( edit -> {
                edit.publishedTime = null;
            } );

        this.contentService.update( updateContentParams );

        doPublishContent( content );

        final Instant updatedPublishTime = this.contentService.getById( content.getId() ).getPublishedTime();

        assertTrue( updatedPublishTime.isAfter( originalPublishTime ) );
    }

    private void doPublishContent( final Content content )
    {
        this.contentService.publish( PushContentParams.create().
            contentIds( ContentIds.from( content.getId() ) ).
            target( CTX_OTHER.getBranch() ).
            includeDependencies( false ).
            build() );
    }

    private Content doCreateContent()
    {
        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content" ).
            name( "myContent" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build();

        return this.contentService.create( createContentParams );
    }

}
