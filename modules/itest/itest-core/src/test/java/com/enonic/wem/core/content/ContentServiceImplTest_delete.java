package com.enonic.wem.core.content;

import org.junit.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentState;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.DeleteContentParams;
import com.enonic.xp.content.GetContentByIdsParams;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.content.PushContentsResult;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.junit.Assert.*;

public class ContentServiceImplTest_delete
    extends AbstractContentServiceTest
{
    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();
    }

    @Test
    public void create_delete_content()
        throws Exception
    {
        //Creates a content
        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build();

        final Content content = this.contentService.create( createContentParams );

        //Deletes the content
        final DeleteContentParams deleteContentParams = DeleteContentParams.create().contentPath( content.getPath() ).build();

        final Contents deletedContents = this.contentService.delete( deleteContentParams );
        assertNotNull( deletedContents );
        assertEquals( Contents.from( content ), deletedContents );

        //Checks that the content is deleted
        final ContentIds contentIds = ContentIds.from( content.getId() );
        final GetContentByIdsParams getContentByIdsParams = new GetContentByIdsParams( contentIds );

        final Contents foundContents = this.contentService.getByIds( getContentByIdsParams );
        assertEquals( 0, foundContents.getSize() );
    }

    @Test
    public void create_delete_content_with_children()
        throws Exception
    {
        //Creates a content with children
        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "Root Content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build();

        final Content content = this.contentService.create( createContentParams );

        final CreateContentParams createChild1ContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "Child1 Content" ).
            parent( content.getPath() ).
            type( ContentTypeName.folder() ).
            build();

        final Content child1Content = this.contentService.create( createChild1ContentParams );

        final CreateContentParams createChild2ContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "Child2 Content" ).
            parent( content.getPath() ).
            type( ContentTypeName.folder() ).
            build();

        final Content child2Content = this.contentService.create( createChild2ContentParams );

        final CreateContentParams createSubChildContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "SubChild Content" ).
            parent( child1Content.getPath() ).
            type( ContentTypeName.folder() ).
            build();

        final Content subChildContent = this.contentService.create( createSubChildContentParams );

        refresh();

        //Deletes the content
        final Contents deletedContents =
            this.contentService.delete( DeleteContentParams.create().contentPath( content.getPath() ).build() );
        assertNotNull( deletedContents );
        assertEquals( Contents.from( content ), deletedContents );

        //Checks that the content and the children are deleted
        final GetContentByIdsParams getContentByIdsParams = new GetContentByIdsParams(
            ContentIds.from( content.getId(), child1Content.getId(), child2Content.getId(), subChildContent.getId() ) );

        final Contents foundContents = this.contentService.getByIds( getContentByIdsParams );

        assertEquals( 0, foundContents.getSize() );
    }

    @Test
    public void create_delete_published_content()
        throws Exception
    {
        final Content content = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build() );

        refresh();

        final PushContentsResult result = this.contentService.push( PushContentParams.create().
            contentIds( ContentIds.from( content.getId() ) ).
            target( CTX_OTHER.getBranch() ).
            allowPublishOutsideSelection( false ).
            build() );

        assertEquals( 1, result.getPushedContent().getSize() );

        //Deletes the content
        final DeleteContentParams deleteContentParams = DeleteContentParams.create().contentPath( content.getPath() ).build();

        final Contents deletedContents = this.contentService.delete( deleteContentParams );
        assertNotNull( deletedContents );
        assertEquals( 1, deletedContents.getSize() );
        for ( Content deletedContent : deletedContents )
        {
            assertTrue( ContentState.PENDING_DELETE == deletedContent.getContentState() );
        }

        //Checks that the content is marked for deletion
        final Content foundContent = this.contentService.getById( content.getId() );
        assertTrue( ContentState.PENDING_DELETE == foundContent.getContentState() );
    }

    @Test
    public void create_delete_published_content_with_children()
        throws Exception
    {

        //Creates a content with children
        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "Root Content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build();

        final Content content = this.contentService.create( createContentParams );

        final CreateContentParams createChild1ContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "Child1 Content" ).
            parent( content.getPath() ).
            type( ContentTypeName.folder() ).
            build();

        final Content child1Content = this.contentService.create( createChild1ContentParams );

        final CreateContentParams createChild2ContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "Child2 Content" ).
            parent( content.getPath() ).
            type( ContentTypeName.folder() ).
            build();

        final Content child2Content = this.contentService.create( createChild2ContentParams );

        final CreateContentParams createSubChildContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "SubChild Content" ).
            parent( child1Content.getPath() ).
            type( ContentTypeName.folder() ).
            build();

        final Content subChildContent = this.contentService.create( createSubChildContentParams );

        //Publishes the content
        final PushContentParams pushParams = PushContentParams.create().
            contentIds( ContentIds.from( content.getId() ) ).
            target( CTX_OTHER.getBranch() ).
            allowPublishOutsideSelection( true ).
            build();

        refresh();

        final PushContentsResult push = this.contentService.push( pushParams );

        //Deletes the content
        final DeleteContentParams deleteContentParams = DeleteContentParams.create().contentPath( content.getPath() ).build();

        final Contents deletedContents = this.contentService.delete( deleteContentParams );
        assertNotNull( deletedContents );
        assertEquals( 4, deletedContents.getSize() );
        for ( Content deletedContent : deletedContents )
        {
            assertTrue( ContentState.PENDING_DELETE == deletedContent.getContentState() );
        }

        //Checks that the content and children are marked for deletion
        final ContentIds contentIds =
            ContentIds.from( content.getId(), child1Content.getId(), child2Content.getId(), subChildContent.getId() );
        final GetContentByIdsParams getContentByIdsParams = new GetContentByIdsParams( contentIds );

        final Contents foundContents = this.contentService.getByIds( getContentByIdsParams );
        assertEquals( 4, foundContents.getSize() );
        for ( Content foundContent : foundContents )
        {
            assertTrue( ContentState.PENDING_DELETE == foundContent.getContentState() );
        }
    }

    @Test
    public void delete_published_content_with_unpublished_children()
        throws Exception
    {
        final Content parent = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "Root Content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build() );

        final Content child = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "Published Child Content" ).
            parent( parent.getPath() ).
            type( ContentTypeName.folder() ).
            build() );

        refresh();

        final PushContentsResult result = this.contentService.push( PushContentParams.create().
            contentIds( ContentIds.from( parent.getId() ) ).
            target( CTX_OTHER.getBranch() ).
            allowPublishOutsideSelection( true ).
            includeChildren( true ).
            build() );
        assertEquals( 2, result.getPushedContent().getSize() );

        refresh();

        //Creates an child that we wont publish
        final Content unpublishedChildContent = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "Unpublished Child Content" ).
            parent( parent.getPath() ).
            type( ContentTypeName.folder() ).
            build() );

        refresh();

        //Deletes the root content
        final Contents deletedContents = this.contentService.delete( DeleteContentParams.create().
            contentPath( parent.getPath() ).
            build() );

        assertNotNull( deletedContents );
        assertEquals( 3, deletedContents.getSize() );

        DeleteContentParams.create().
            contentPath( parent.getPath() ).
            build();

        for ( Content deletedContent : deletedContents )
        {
            if ( unpublishedChildContent.getId().equals( deletedContent.getId() ) )
            {
                assertTrue( ContentState.DEFAULT == deletedContent.getContentState() );
            }
            else if ( parent.getId().equals( deletedContent.getId() ) || child.getId().equals( deletedContent.getId() ) )
            {
                assertTrue( ContentState.PENDING_DELETE == deletedContent.getContentState() );
            }
            else
            {
                fail();
            }
        }

        //Checks that the content and published child are marked for deletion and that the unpublished child is deleted
        final ContentIds contentIds = ContentIds.from( parent.getId(), child.getId(), unpublishedChildContent.getId() );
        final GetContentByIdsParams getContentByIdsParams = new GetContentByIdsParams( contentIds );

        final Contents foundContents = this.contentService.getByIds( getContentByIdsParams );
        assertEquals( 2, foundContents.getSize() );
        for ( Content foundContent : foundContents )
        {
            assertTrue( ContentState.PENDING_DELETE == foundContent.getContentState() );
            assertTrue( parent.getId().equals( foundContent.getId() ) || child.getId().equals( foundContent.getId() ) );
        }
    }


    @Test
    public void create_content_with_same_paths_in_two_repos_then_delete()
        throws Exception
    {
        final CreateContentParams params = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build();

        final Content content = this.contentService.create( params );

        final Content contentOther = CTX_OTHER.callWith( () -> this.contentService.create( params ) );

        //Deletes the content
        final Contents deletedContents =
            this.contentService.delete( DeleteContentParams.create().contentPath( content.getPath() ).build() );
        assertNotNull( deletedContents );
        assertEquals( 1, deletedContents.getSize() );

        final Contents deletedOther = CTX_OTHER.callWith(
            () -> this.contentService.delete( DeleteContentParams.create().contentPath( contentOther.getPath() ).build() ) );

        assertNotNull( deletedOther );
        assertEquals( 1, deletedOther.getSize() );
    }


}
