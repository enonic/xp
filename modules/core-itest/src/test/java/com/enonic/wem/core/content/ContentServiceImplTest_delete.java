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

        //Deletes the content
        final DeleteContentParams deleteContentParams = DeleteContentParams.create().contentPath( content.getPath() ).build();

        final Contents deletedContents = this.contentService.delete( deleteContentParams );
        assertNotNull( deletedContents );
        assertEquals( Contents.from( content ), deletedContents );

        //Checks that the content and the children are deleted
        final ContentIds contentIds =
            ContentIds.from( content.getId(), child1Content.getId(), child2Content.getId(), subChildContent.getId() );
        final GetContentByIdsParams getContentByIdsParams = new GetContentByIdsParams( contentIds );

        final Contents foundContents = this.contentService.getByIds( getContentByIdsParams );
        assertEquals( 0, foundContents.getSize() );
    }

    @Test
    public void create_delete_published_content()
        throws Exception
    {
        //Creates content
        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build();

        final Content content = this.contentService.create( createContentParams );

        //Publishes the content
        final PushContentParams pushParams = PushContentParams.create().
            contentIds( ContentIds.from( content.getId() ) ).
            target( CTX_OTHER.getBranch() ).
            allowPublishOutsideSelection( false ).
            build();

        this.contentService.push( pushParams );

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
    public void create_delete_published_content_with_unpublished_children()
        throws Exception
    {

        //Creates a content with a child
        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "Root Content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build();

        final Content content = this.contentService.create( createContentParams );

        final CreateContentParams createPublishedChildContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "Published Child Content" ).
            parent( content.getPath() ).
            type( ContentTypeName.folder() ).
            build();

        final Content publishedChildContent = this.contentService.create( createPublishedChildContentParams );

        //Publishes the content
        final PushContentParams pushParams = PushContentParams.create().
            contentIds( ContentIds.from( content.getId() ) ).
            target( CTX_OTHER.getBranch() ).
            allowPublishOutsideSelection( false ).
            build();

        final PushContentsResult push = this.contentService.push( pushParams );

        //Creates an unpublished child
        final CreateContentParams createUnpublishedChildContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "Unpublished Child Content" ).
            parent( content.getPath() ).
            type( ContentTypeName.folder() ).
            build();

        final Content unpublishedChildContent = this.contentService.create( createUnpublishedChildContentParams );

        //Deletes the root content
        final DeleteContentParams deleteContentParams = DeleteContentParams.create().contentPath( content.getPath() ).build();

        final Contents deletedContents = this.contentService.delete( deleteContentParams );
        assertNotNull( deletedContents );
        assertEquals( 3, deletedContents.getSize() );
        for ( Content deletedContent : deletedContents )
        {
            if ( unpublishedChildContent.getId().equals( deletedContent.getId() ) )
            {
                assertTrue( ContentState.DEFAULT == deletedContent.getContentState() );
            }
            else if ( content.getId().equals( deletedContent.getId() ) || publishedChildContent.getId().equals( deletedContent.getId() ) )
            {
                assertTrue( ContentState.PENDING_DELETE == deletedContent.getContentState() );
            }
            else
            {
                fail();
            }
        }

        //Checks that the content and published child are marked for deletion and that the unpublished child is deleted
        final ContentIds contentIds = ContentIds.from( content.getId(), publishedChildContent.getId(), unpublishedChildContent.getId() );
        final GetContentByIdsParams getContentByIdsParams = new GetContentByIdsParams( contentIds );

        final Contents foundContents = this.contentService.getByIds( getContentByIdsParams );
        assertEquals( 2, foundContents.getSize() );
        for ( Content foundContent : foundContents )
        {
            assertTrue( ContentState.PENDING_DELETE == foundContent.getContentState() );
            assertTrue( content.getId().equals( foundContent.getId() ) || publishedChildContent.getId().equals( foundContent.getId() ) );
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

        System.out.println( "Content: " + content.getId() );
        System.out.println( "ContentOther: " + contentOther.getId() );

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
