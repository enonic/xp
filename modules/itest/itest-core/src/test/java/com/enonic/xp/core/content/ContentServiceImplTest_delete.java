package com.enonic.xp.core.content;

import java.util.stream.StreamSupport;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.audit.LogAuditLogParams;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.DeleteContentParams;
import com.enonic.xp.content.DeleteContentsResult;
import com.enonic.xp.content.GetContentByIdsParams;
import com.enonic.xp.content.MoveContentParams;
import com.enonic.xp.content.PublishContentResult;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ContentServiceImplTest_delete
    extends AbstractContentServiceTest
{

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

        final PublishContentResult result = this.contentService.publish( PushContentParams.create().
            contentIds( ContentIds.from( content.getId() ) ).
            build() );

        assertEquals( 1, result.getPushedContents().getSize() );

        //Deletes the content
        final DeleteContentParams deleteContentParams = DeleteContentParams.create().contentPath( content.getPath() ).build();

        final DeleteContentsResult deletedContents = this.contentService.deleteWithoutFetch( deleteContentParams );

        assertNotNull( deletedContents );

        assertEquals( 1, deletedContents.getDeletedContents().getSize() );
        assertEquals( 1, deletedContents.getUnpublishedContents().getSize() );
        assertEquals( deletedContents.getUnpublishedContents(), deletedContents.getDeletedContents() );
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

        this.contentService.create( createSubChildContentParams );

        //Publishes the content
        final PushContentParams pushParams = PushContentParams.create().
            contentIds( ContentIds.from( content.getId() ) ).
            build();

        refresh();

        this.contentService.publish( pushParams );

        //Deletes the content
        final DeleteContentParams deleteContentParams = DeleteContentParams.create().contentPath( content.getPath() ).build();

        final DeleteContentsResult deletedContents = this.contentService.deleteWithoutFetch( deleteContentParams );
        assertNotNull( deletedContents );
        assertEquals( 4, deletedContents.getDeletedContents().getSize() );
        assertEquals( deletedContents.getDeletedContents(), deletedContents.getUnpublishedContents() );
    }

    @Test
    public void delete_published_content_with_child_moved_in()
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

        final CreateContentParams createMovedContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "Content to move" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build();

        final Content contentToMove = this.contentService.create( createMovedContentParams );

        final CreateContentParams createChild1ContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "Child1 Content" ).
            parent( content.getPath() ).
            type( ContentTypeName.folder() ).
            build();

        final Content child1Content = this.contentService.create( createChild1ContentParams );

        refresh();

        this.contentService.publish( PushContentParams.create().
            contentIds( ContentIds.from( content.getId(), contentToMove.getId() ) ).
            build() );

        refresh();

        this.contentService.move( MoveContentParams.create().
            contentId( contentToMove.getId() ).
            parentContentPath( child1Content.getPath() ).
            build() );

        refresh();

        final DeleteContentsResult result = this.contentService.deleteWithoutFetch( DeleteContentParams.create().
            contentPath( content.getPath() ).
            build() );

        assertEquals( 3, result.getDeletedContents().getSize() );
        assertEquals( 3, result.getUnpublishedContents().getSize() );
    }

    @Test
    public void delete_published_content_with_child_moved_out()
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

        final CreateContentParams createMovedContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "Content to move" ).
            parent( child1Content.getPath() ).
            type( ContentTypeName.folder() ).
            build();

        final Content contentToMove = this.contentService.create( createMovedContentParams );

        refresh();

        this.contentService.publish( PushContentParams.create().
            contentIds( ContentIds.from( contentToMove.getId() ) ).
            build() );

        refresh();

        this.contentService.move( MoveContentParams.create().
            contentId( contentToMove.getId() ).
            parentContentPath( ContentPath.ROOT ).
            build() );

        refresh();

        DeleteContentsResult result = this.contentService.deleteWithoutFetch( DeleteContentParams.create().
            contentPath( content.getPath() ).
            build() );

        assertEquals( 2, result.getDeletedContents().getSize() );
        assertEquals( 3, result.getUnpublishedContents().getSize() );
    }

    @Test
    public void publish_pending_content_with_child_moved_inside_the_tree()
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

        final CreateContentParams createMovedContentParams = CreateContentParams.create().contentData( new PropertyTree() )
            .displayName( "Content to move" )
            .parent( child1Content.getPath() )
            .type( ContentTypeName.folder() )
            .build();

        final Content contentToMove = this.contentService.create( createMovedContentParams );

        refresh();

        this.contentService.publish( PushContentParams.create().contentIds( ContentIds.from( contentToMove.getId() ) ).build() );

        refresh();

        this.contentService.move(
            MoveContentParams.create().contentId( contentToMove.getId() ).parentContentPath( content.getPath() ).build() );

        refresh();

        DeleteContentsResult result =
            this.contentService.deleteWithoutFetch( DeleteContentParams.create().contentPath( content.getPath() ).build() );

        assertEquals( 3, result.getDeletedContents().getSize() );
        assertEquals( result.getDeletedContents(), result.getUnpublishedContents() );
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

        final Content contentOther = ctxOther().callWith( () -> this.contentService.create( params ) );

        //Deletes the content
        final DeleteContentsResult deletedContents =
            this.contentService.deleteWithoutFetch( DeleteContentParams.create().contentPath( content.getPath() ).build() );
        assertNotNull( deletedContents );
        assertEquals( 1, deletedContents.getDeletedContents().getSize() );

        final DeleteContentsResult deletedOther = ctxOther().callWith(
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

    @Test
    public void audit_data()
    {
        final ArgumentCaptor<LogAuditLogParams> captor = ArgumentCaptor.forClass( LogAuditLogParams.class );

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

        this.contentService.create( createChild1ContentParams );

        final DeleteContentParams deleteContentParams = DeleteContentParams.create().contentPath( content.getPath() ).build();
        this.contentService.deleteWithoutFetch( deleteContentParams );

        Mockito.verify( auditLogService, Mockito.timeout( 5000 ).atLeast( 17 ) ).log( captor.capture() );

        final PropertySet logResultSet = captor.getAllValues()
            .stream()
            .filter( log -> log.getType().equals( "system.content.delete" ) )
            .findFirst()
            .get()
            .getData()
            .getSet( "result" );

        final Iterable<String> deletedContents = logResultSet.getStrings( "deletedContents" );
        assertEquals( 2, StreamSupport.stream( deletedContents.spliterator(), false ).count() );
    }
}
