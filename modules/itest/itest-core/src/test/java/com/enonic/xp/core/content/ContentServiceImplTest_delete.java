package com.enonic.xp.core.content;

import org.junit.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentState;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.DeleteContentParams;
import com.enonic.xp.content.DeleteContentsResult;
import com.enonic.xp.content.GetContentByIdsParams;
import com.enonic.xp.content.MoveContentParams;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.content.PushContentsResult;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeState;
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
            name( "myContent" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build();

        final Content content = this.contentService.create( createContentParams );

        //Deletes the content
        final DeleteContentParams deleteContentParams = DeleteContentParams.create().contentPath( content.getPath() ).build();

        final DeleteContentsResult deletedContents = this.contentService.deleteWithoutFetch( deleteContentParams );
        assertNotNull( deletedContents );
        assertEquals( 1, deletedContents.getDeletedContents().getSize() );
        assertEquals( content.getId(), deletedContents.getDeletedContents().first() );

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
        final DeleteContentsResult deletedContents =
            this.contentService.deleteWithoutFetch( DeleteContentParams.create().contentPath( content.getPath() ).build() );
        assertNotNull( deletedContents );
        assertEquals( 4, deletedContents.getDeletedContents().getSize() );

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
            name( "myContent" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build() );

        refresh();

        final PushContentsResult result = this.contentService.push( PushContentParams.create().
            contentIds( ContentIds.from( content.getId() ) ).
            target( CTX_OTHER.getBranch() ).
            build() );

        assertEquals( 1, result.getPushedContents().getSize() );

        //Deletes the content
        final DeleteContentParams deleteContentParams = DeleteContentParams.create().contentPath( content.getPath() ).build();

        final DeleteContentsResult deletedContents = this.contentService.deleteWithoutFetch( deleteContentParams );
        assertNotNull( deletedContents );
        assertEquals( 1, deletedContents.getPendingContents().getSize() );
        for ( ContentId deletedContent : deletedContents.getPendingContents() )
        {
            assertTrue( NodeState.PENDING_DELETE == this.nodeService.getById( NodeId.from( deletedContent ) ).getNodeState() );
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
            build();

        refresh();

        this.contentService.push( pushParams );

        //Deletes the content
        final DeleteContentParams deleteContentParams = DeleteContentParams.create().contentPath( content.getPath() ).build();

        final DeleteContentsResult deletedContents = this.contentService.deleteWithoutFetch( deleteContentParams );
        assertNotNull( deletedContents );
        assertEquals( 4, deletedContents.getPendingContents().getSize() );

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
            build() );
        assertEquals( 2, result.getPushedContents().getSize() );

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
        final DeleteContentsResult deletedContents = this.contentService.deleteWithoutFetch( DeleteContentParams.create().
            contentPath( parent.getPath() ).
            build() );

        assertNotNull( deletedContents );
        assertEquals( 1, deletedContents.getDeletedContents().getSize() );
        assertEquals( 2, deletedContents.getPendingContents().getSize() );

        DeleteContentParams.create().
            contentPath( parent.getPath() ).
            build();

        assertTrue( deletedContents.getDeletedContents().contains( unpublishedChildContent.getId() ) );
        assertTrue( deletedContents.getPendingContents().contains( parent.getId() ) );
        assertTrue( deletedContents.getPendingContents().contains( child.getId() ) );

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
        final DeleteContentsResult deletedContents =
            this.contentService.deleteWithoutFetch( DeleteContentParams.create().contentPath( content.getPath() ).build() );
        assertNotNull( deletedContents );
        assertEquals( 1, deletedContents.getDeletedContents().getSize() );

        final DeleteContentsResult deletedOther = CTX_OTHER.callWith(
            () -> this.contentService.deleteWithoutFetch( DeleteContentParams.create().contentPath( contentOther.getPath() ).build() ) );

        assertNotNull( deletedOther );
        assertEquals( 1, deletedOther.getDeletedContents().getSize() );
    }


    @Test
    public void move_to_folder_starting_with_same_name_and_delete()
        throws Exception
    {

        final Content site = createContent( ContentPath.ROOT, "site" );
        final Content child1 = createContent( site.getPath(), "child1" );
        createContent( child1.getPath(), "child1_1" );
        createContent( child1.getPath(), "child2_1" );

        final Content site2 = createContent( ContentPath.ROOT, "site2" );

        refresh();

        final MoveContentParams params = MoveContentParams.create().
            contentId( child1.getId() ).
            parentContentPath( site2.getPath() ).
            build();

        this.contentService.move( params );

        final DeleteContentsResult result = this.contentService.deleteWithoutFetch( DeleteContentParams.create().
            contentPath( site.getPath() ).
            build() );

        assertEquals( 1, result.getDeletedContents().getSize() );


    }
}
