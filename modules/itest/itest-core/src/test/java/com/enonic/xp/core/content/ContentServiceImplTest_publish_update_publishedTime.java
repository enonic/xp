package com.enonic.xp.core.content;

import org.junit.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.DuplicateContentParams;
import com.enonic.xp.content.DuplicateContentsResult;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.junit.Assert.*;

public class ContentServiceImplTest_publish_update_publishedTime
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
        assertNull( content.getPublishInfo() );
        assertVersions( content.getId(), 1 );

        doPublishContent( content );
        assertVersions( content.getId(), 2 );

        final Content storedContent = this.contentService.getById( content.getId() );
        assertNotNull( storedContent.getPublishInfo() );
        assertNotNull( storedContent.getPublishInfo().getFirst() );
        assertNotNull( storedContent.getPublishInfo().getFrom() );

        final Content publishedContent = ContextBuilder.from( ContextAccessor.current() ).
            branch( WS_OTHER ).
            build().
            callWith( () -> this.contentService.getById( content.getId() ) );

        assertEquals( storedContent.getPublishInfo(), publishedContent.getPublishInfo() );
    }

    @Test
    public void keep_original_published_time()
        throws Exception
    {
        final Content content = doCreateContent();

        doPublishContent( content );
        assertVersions( content.getId(), 2 );

        final ContentPublishInfo publishInfo = this.contentService.getById( content.getId() ).getPublishInfo();
        assertNotNull( publishInfo );
        assertNotNull( publishInfo.getFirst() );
        assertNotNull( publishInfo.getFrom() );

        final UpdateContentParams updateContentParams = new UpdateContentParams();
        updateContentParams.contentId( content.getId() ).
            editor( edit -> edit.displayName = "new display name" );

        this.contentService.update( updateContentParams );

        doPublishContent( content );
        assertVersions( content.getId(), 3 );

        final ContentPublishInfo unUpdatedPublishInfo = this.contentService.getById( content.getId() ).getPublishInfo();
        assertEquals( publishInfo, unUpdatedPublishInfo );
    }

    @Test
    public void set_publish_time_again_if_reset()
        throws Exception
    {
        final Content content = doCreateContent();
        doPublishContent( content );

        final ContentPublishInfo publishInfo = this.contentService.getById( content.getId() ).getPublishInfo();

        final UpdateContentParams updateContentParams = new UpdateContentParams();
        updateContentParams.contentId( content.getId() ).
            editor( edit -> edit.publishInfo = null );

        this.contentService.update( updateContentParams );

        doPublishContent( content );

        final ContentPublishInfo updatedPublishInfo = this.contentService.getById( content.getId() ).getPublishInfo();

        assertTrue( updatedPublishInfo.getFrom().isAfter( publishInfo.getFrom() ) );
    }

    @Test
    public void publish_info_is_removed_on_duplicate()
        throws Exception
    {

        final Content rootContent = createContent( ContentPath.ROOT );
        this.contentService.publish( PushContentParams.create().
            contentIds( ContentIds.from( rootContent.getId() ) ).
            target( WS_OTHER ).
            build() );

        final Content duplicateContent = doDuplicateContent( rootContent );

        assertNull( duplicateContent.getPublishInfo() );
    }

    private Content doDuplicateContent( final Content rootContent )
    {
        final DuplicateContentParams params = DuplicateContentParams.create().contentId( rootContent.getId() ).build();
        final DuplicateContentsResult result = contentService.duplicate( params );

        return this.contentService.getById( result.getDuplicatedContents().first() );
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
