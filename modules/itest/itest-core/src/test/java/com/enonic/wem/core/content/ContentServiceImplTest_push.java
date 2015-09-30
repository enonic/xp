package com.enonic.wem.core.content;

import org.junit.Ignore;
import org.junit.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.DeleteContentParams;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.content.PushContentsResult;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.util.Reference;

import static org.junit.Assert.*;

public class ContentServiceImplTest_push
    extends AbstractContentServiceTest
{
    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();
    }

    @Test
    public void push_one_content()
        throws Exception
    {
        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build();

        final Content content = this.contentService.create( createContentParams );

        refresh();

        final PushContentsResult push = this.contentService.push( PushContentParams.create().
            contentIds( ContentIds.from( content.getId() ) ).
            target( CTX_OTHER.getBranch() ).
            allowPublishOutsideSelection( false ).
            resolveDependencies( false ).
            build() );

        assertEquals( 1, push.getPushedContent().getSize() );
    }


    @Test
    public void push_12_content()
        throws Exception
    {
        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build();

        final Content content = this.contentService.create( createContentParams );

        for ( int i = 0; i <= 12; i++ )
        {
            createContent( "content-" + i, content.getPath() );
        }

        refresh();

        final PushContentsResult push = this.contentService.push( PushContentParams.create().
            contentIds( ContentIds.from( content.getId() ) ).
            target( CTX_OTHER.getBranch() ).
            includeChildren( true ).
            build() );

        assertEquals( 14, push.getPushedContent().getSize() );
    }

    private void createContent( final String id, final ContentPath parent )
    {
        this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( id ).
            parent( parent ).
            type( ContentTypeName.folder() ).
            build() );
    }

    @Ignore
    @Test
    public void push_deleted()
        throws Exception
    {
        final Content content = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build() );

        final PushContentParams pushParams = PushContentParams.create().
            contentIds( ContentIds.from( content.getId() ) ).
            target( CTX_OTHER.getBranch() ).
            allowPublishOutsideSelection( false ).
            build();

        refresh();

        this.contentService.push( pushParams );

        contentService.delete( DeleteContentParams.create().
            contentPath( content.getPath() ).
            build() );

        final PushContentsResult pushWithDeleted = this.contentService.push( pushParams );

        assertEquals( 1, pushWithDeleted.getDeleted().getSize() );
    }

    @Test
    public void push_dependencies_not_allow_outside()
        throws Exception
    {
        final PushContentsResult result = doPushWithDependencies( false );

        assertEquals( 2, result.getPushContentRequests().getPushedBecauseParentOfPusheds().size() );
        assertEquals( 1, result.getPushContentRequests().getPushBecauseRequested().size() );
        assertEquals( 1, result.getPushContentRequests().getPushedBecauseReferredTos().size() );
    }

    @Test
    public void push_dependencies_allow_outside()
        throws Exception
    {
        final PushContentsResult result = doPushWithDependencies( true );

        assertEquals( 4, result.getPushedContent().getSize() );
    }

    private PushContentsResult doPushWithDependencies( final boolean allowPublishOutsideSelection )
    {
        final Content content1 = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build() );

        final Content content2 = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content 2" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build() );

        final Content child1 = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my child 1" ).
            parent( content1.getPath() ).
            type( ContentTypeName.folder() ).
            build() );

        final PropertyTree data = new PropertyTree();
        data.addReference( "myRef", Reference.from( child1.getId().toString() ) );

        final Content child2 = this.contentService.create( CreateContentParams.create().
            contentData( data ).
            displayName( "This is my child 2" ).
            parent( content2.getPath() ).
            type( ContentTypeName.folder() ).
            build() );

        final PushContentParams pushParams = PushContentParams.create().
            contentIds( ContentIds.from( child2.getId() ) ).
            target( CTX_OTHER.getBranch() ).
            allowPublishOutsideSelection( allowPublishOutsideSelection ).
            build();

        refresh();

        return this.contentService.push( pushParams );
    }


}
