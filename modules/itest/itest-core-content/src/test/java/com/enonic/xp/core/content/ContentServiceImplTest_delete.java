package com.enonic.xp.core.content;

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
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.verify;

class ContentServiceImplTest_delete
    extends AbstractContentServiceTest
{

    @Test
    void create_delete_content()
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

        final DeleteContentsResult deletedContents = this.contentService.delete( deleteContentParams );
        assertNotNull( deletedContents );
        assertEquals( 1, deletedContents.getDeletedContents().getSize() );
        assertEquals( content.getId(), deletedContents.getDeletedContents().first() );

        //Checks that the content is deleted
        final ContentIds contentIds = ContentIds.from( content.getId() );
        final GetContentByIdsParams getContentByIdsParams = GetContentByIdsParams.create().contentIds( contentIds ).build();

        final Contents foundContents = this.contentService.getByIds( getContentByIdsParams );
        assertEquals( 0, foundContents.getSize() );
    }

    @Test
    void create_delete_content_with_children()
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
        final DeleteContentsResult deletedContents =
            this.contentService.delete( DeleteContentParams.create().contentPath( content.getPath() ).build() );
        assertNotNull( deletedContents );
        assertEquals( 4, deletedContents.getDeletedContents().getSize() );

        //Checks that the content and the children are deleted
        final GetContentByIdsParams getContentByIdsParams = GetContentByIdsParams.create()
            .contentIds( ContentIds.from( content.getId(), child1Content.getId(), child2Content.getId(), subChildContent.getId() ) )
            .build();

        final Contents foundContents = this.contentService.getByIds( getContentByIdsParams );

        assertEquals( 0, foundContents.getSize() );
    }

    @Test
    void create_delete_published_content()
    {
        final Content content = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content" ).
            name( "myContent" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build() );

        final PublishContentResult result = this.contentService.publish( PushContentParams.create().
            contentIds( ContentIds.from( content.getId() ) ).
            build() );

        assertEquals( 1, result.getPushedContents().getSize() );

        //Deletes the content
        final DeleteContentParams deleteContentParams = DeleteContentParams.create().contentPath( content.getPath() ).build();

        final DeleteContentsResult deletedContents = this.contentService.delete( deleteContentParams );

        assertNotNull( deletedContents );

        assertEquals( 1, deletedContents.getDeletedContents().getSize() );
        assertEquals( 1, deletedContents.getUnpublishedContents().getSize() );
        assertEquals( deletedContents.getUnpublishedContents(), deletedContents.getDeletedContents() );
    }

    @Test
    void create_delete_published_content_with_children()
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

        this.contentService.publish( pushParams );

        //Deletes the content
        final DeleteContentParams deleteContentParams = DeleteContentParams.create().contentPath( content.getPath() ).build();

        final DeleteContentsResult deletedContents = this.contentService.delete( deleteContentParams );
        assertNotNull( deletedContents );
        assertEquals( 4, deletedContents.getDeletedContents().getSize() );
        assertEquals( deletedContents.getDeletedContents(), deletedContents.getUnpublishedContents() );
    }

    @Test
    void delete_published_content_with_child_moved_in()
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

        this.contentService.publish( PushContentParams.create().
            contentIds( ContentIds.from( content.getId(), contentToMove.getId() ) ).
            build() );

        this.contentService.move( MoveContentParams.create().
            contentId( contentToMove.getId() ).
            parentContentPath( child1Content.getPath() ).
            build() );

        final DeleteContentsResult result = this.contentService.delete( DeleteContentParams.create().
            contentPath( content.getPath() ).
            build() );

        assertThat( result.getDeletedContents() ).containsExactlyInAnyOrder( content.getId(), contentToMove.getId(),
                                                                             child1Content.getId() );
        assertThat( result.getUnpublishedContents() ).containsExactlyInAnyOrder( content.getId(), contentToMove.getId(),
                                                                                 child1Content.getId() );
    }

    @Test
    void delete_published_content_with_child_moved_out()
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

        this.contentService.publish( PushContentParams.create().
            contentIds( ContentIds.from( contentToMove.getId() ) ).
            build() );

        this.contentService.move( MoveContentParams.create().
            contentId( contentToMove.getId() ).
            parentContentPath( ContentPath.ROOT ).
            build() );

        DeleteContentsResult result = this.contentService.delete( DeleteContentParams.create().
            contentPath( content.getPath() ).
            build() );

        assertEquals( 2, result.getDeletedContents().getSize() );
        assertEquals( 3, result.getUnpublishedContents().getSize() );
    }

    @Test
    void publish_pending_content_with_child_moved_inside_the_tree()
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

        this.contentService.publish( PushContentParams.create().contentIds( ContentIds.from( contentToMove.getId() ) ).build() );

        this.contentService.move(
            MoveContentParams.create().contentId( contentToMove.getId() ).parentContentPath( content.getPath() ).build() );

        DeleteContentsResult result =
            this.contentService.delete( DeleteContentParams.create().contentPath( content.getPath() ).build() );

        assertEquals( 3, result.getDeletedContents().getSize() );
        assertEquals( result.getDeletedContents(), result.getUnpublishedContents() );
    }

    @Test
    void create_content_with_same_paths_in_two_branches_then_delete()
    {
        final Content folder = contentService.create( CreateContentParams.create()
                                                            .contentData( new PropertyTree() )
                                                            .displayName( "This is my folder" )
                                                            .parent( ContentPath.ROOT )
                                                            .name( "folder" )
                                                            .type( ContentTypeName.folder() )
                                                            .build() );

        final CreateContentParams params = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content" ).
            name( "my-content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build();

        final Content content = contentService.create( params );
        contentService.publish( PushContentParams.create().contentIds( ContentIds.from( content.getId() ) ).build() );
        contentService.move( MoveContentParams.create().contentId( content.getId() ).parentContentPath( folder.getPath() ).build() );

        final Content content2 = contentService.create( params );

        //Deletes the content
        final DeleteContentsResult deletedContents =
            contentService.delete( DeleteContentParams.create().contentPath( content2.getPath() ).build() );
        assertNotNull( deletedContents );
        assertEquals( 1, deletedContents.getDeletedContents().getSize() );
        assertEquals( 0, deletedContents.getUnpublishedContents().getSize() );

        final DeleteContentsResult deletedOther = contentService.delete(
            DeleteContentParams.create().contentPath( ContentPath.from( folder.getPath(), content.getName() ) ).build() );

        assertNotNull( deletedOther );
        assertEquals( 1, deletedOther.getDeletedContents().getSize() );
        assertEquals( 0, deletedContents.getUnpublishedContents().getSize() );
    }


    @Test
    void move_to_folder_starting_with_same_name_and_delete()
    {

        final Content site = createContent( ContentPath.ROOT, "site" );
        final Content child1 = createContent( site.getPath(), "child1" );
        createContent( child1.getPath(), "child1_1" );
        createContent( child1.getPath(), "child2_1" );

        final Content site2 = createContent( ContentPath.ROOT, "site2" );

        final MoveContentParams params = MoveContentParams.create().
            contentId( child1.getId() ).
            parentContentPath( site2.getPath() ).
            build();

        this.contentService.move( params );

        final DeleteContentsResult result = this.contentService.delete( DeleteContentParams.create().
            contentPath( site.getPath() ).
            build() );

        assertEquals( 1, result.getDeletedContents().getSize() );


    }

    @Test
    void audit_data()
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

        final Content childContent = this.contentService.create( createChild1ContentParams );

        Mockito.reset( auditLogService );

        this.contentService.delete( DeleteContentParams.create().contentPath( content.getPath() ).build() );

        verify( auditLogService, atMost(2) ).log( captor.capture() );

        final LogAuditLogParams log = captor.getValue();
        assertThat( log ).extracting( LogAuditLogParams::getType).isEqualTo( "system.content.delete" ) ;

        assertThat( log ).extracting( l -> l.getData().getSet( "result" ) )
            .extracting( result -> result.getStrings( "deletedContents" ), LIST)
            .containsExactly( childContent.getId().toString(), content.getId().toString() );
    }
}
