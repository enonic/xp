package com.enonic.xp.core.content;

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
            includeDependencies( false ).
            build() );

        assertEquals( 1, push.getPushedContents().getSize() );
    }

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
            build();

        final PushContentsResult push = this.contentService.push( pushParams );
        assertEquals( 1, push.getPushedContents().getSize() );

        contentService.delete( DeleteContentParams.create().
            contentPath( content.getPath() ).
            build() );

        final PushContentsResult pushWithDeleted = this.contentService.push( pushParams );
        assertEquals( 1, pushWithDeleted.getDeletedContents().getSize() );
    }

    @Test
    public void push_dependencies()
        throws Exception
    {
        final PushContentsResult result = doPushWithDependencies();

        assertEquals( 4, result.getPushedContents().getSize() );
    }

    private PushContentsResult doPushWithDependencies()
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
            build();

        refresh();

        return this.contentService.push( pushParams );
    }


}
