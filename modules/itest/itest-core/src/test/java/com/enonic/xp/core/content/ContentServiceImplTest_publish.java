package com.enonic.xp.core.content;

import java.util.Iterator;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.audit.LogAuditLogParams;
import com.enonic.xp.content.CompareContentResult;
import com.enonic.xp.content.CompareContentsParams;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentVersion;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.DeleteContentParams;
import com.enonic.xp.content.DeleteContentsResult;
import com.enonic.xp.content.FindContentVersionsParams;
import com.enonic.xp.content.FindContentVersionsResult;
import com.enonic.xp.content.MoveContentParams;
import com.enonic.xp.content.PublishContentResult;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.WorkflowInfo;
import com.enonic.xp.content.WorkflowState;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.util.Reference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.verify;

class ContentServiceImplTest_publish
    extends AbstractContentServiceTest
{

    private Content content1;
    private Content content2;
    private Content content1_1;
    private Content content2_1;

    @Test
    void push_one_content()
    {
        final CreateContentParams createContentParams = CreateContentParams.create()
            .contentData( new PropertyTree() )
            .displayName( "This is my content" )
            .name( "myContent" )
            .parent( ContentPath.ROOT )
            .type( ContentTypeName.folder() )
            .build();

        final Content content = this.contentService.create( createContentParams );

        final PublishContentResult push = this.contentService.publish(
            PushContentParams.create().contentIds( ContentIds.from( content.getId() ) ).includeDependencies( false ).build() );

        assertEquals( 0, push.getFailedContents().getSize() );
        assertEquals( 1, push.getPushedContents().getSize() );
    }

    @Test
    void root_is_published()
    {
        final ContentId rootId = this.contentService.getByPath( ContentPath.ROOT ).getId();
        this.contentService.update( new UpdateContentParams().contentId( rootId ).editor( c -> c.data.setString( "foo", "bar" ) ) );

        final CreateContentParams createContentParams = CreateContentParams.create()
            .contentData( new PropertyTree() )
            .displayName( "This is my content" )
            .name( "myContent" )
            .parent( ContentPath.ROOT )
            .type( ContentTypeName.folder() )
            .build();

        final Content content = this.contentService.create( createContentParams );

        Content draftRoot = this.contentService.getById( rootId );
        Content masterRoot = ContextBuilder.from( ContextAccessor.current() )
            .branch( ContentConstants.BRANCH_MASTER )
            .build()
            .callWith( () -> this.contentService.getById( rootId ) );

        assertNotEquals( draftRoot, masterRoot );

        this.contentService.publish(
            PushContentParams.create().contentIds( ContentIds.from( content.getId() ) ).includeDependencies( false ).build() );

        draftRoot = this.contentService.getByPath( ContentPath.ROOT );
        masterRoot = ContextBuilder.from( ContextAccessor.current() )
            .branch( ContentConstants.BRANCH_MASTER )
            .build()
            .callWith( () -> this.contentService.getByPath( ContentPath.ROOT ) );

        assertEquals( draftRoot, masterRoot );

    }

    @Test
    void publish_workflow_not_ready()
    {
        final CreateContentParams createContentParams = CreateContentParams.create()
            .contentData( new PropertyTree() )
            .displayName( "This is my content" )
            .name( "myContent" )
            .parent( ContentPath.ROOT )
            .type( ContentTypeName.folder() )
            .workflowInfo( WorkflowInfo.create().state( WorkflowState.PENDING_APPROVAL ).build() )
            .build();

        final Content content = this.contentService.create( createContentParams );

        final PublishContentResult push = this.contentService.publish(
            PushContentParams.create().contentIds( ContentIds.from( content.getId() ) ).includeDependencies( false ).build() );

        assertEquals( 1, push.getFailedContents().getSize() );
        assertEquals( 0, push.getPushedContents().getSize() );
    }

    @Test
    void push_one_content_not_valid()
    {
        final CreateContentParams createContentParams = CreateContentParams.create()
            .contentData( new PropertyTree() )
            .displayName( "Shortcut without target is not valid" )
            .parent( ContentPath.ROOT )
            .type( ContentTypeName.shortcut() )
            .build();

        final Content content = this.contentService.create( createContentParams );

        final PublishContentResult push = this.contentService.publish(
            PushContentParams.create().contentIds( ContentIds.from( content.getId() ) ).includeDependencies( false ).build() );

        assertThat( push.getPushedContents() ).isEmpty();
        assertThat( push.getFailedContents() ).containsExactly( content.getId() );
    }

    @Test
    void push_dependencies()
    {
        createContentTree();

        final PushContentParams pushParams = PushContentParams.create().contentIds( ContentIds.from( content2.getId() ) ).build();

        final PublishContentResult result = this.contentService.publish( pushParams );

        assertEquals( 4, result.getPushedContents().getSize() );
    }

    @Test
    void push_with_children()
    {
        createContentTree();

        final PushContentParams pushParams = PushContentParams.create().contentIds( ContentIds.from( content1.getId() ) ).build();

        final PublishContentResult result = this.contentService.publish( pushParams );
        assertEquals( 2, result.getPushedContents().getSize() );
    }

    /**
     * ./content1
     * ../content1_1 -> Ref:content2_1_1
     * ./content2
     * ../content2_1
     * ../../content2_1_1
     * ./content3
     */
    @Test
    void push_parent_of_dependencies()
    {
        createContentTree2();

        final PushContentParams pushParams = PushContentParams.create()
            .contentIds( ContentIds.from( content1_1.getId() ) )
            .excludeDescendantsOf( ContentIds.from( content1_1.getId() ) )
            .build();

        final PublishContentResult result = this.contentService.publish( pushParams );

        assertEquals( 5, result.getPushedContents().getSize() );
        assertEquals( 0, result.getFailedContents().getSize() );
    }


    @Disabled("This test is not correct; it should not be allowed to exclude parent if new")
    @Test
    void push_exclude_empty()
    {
        createContentTree();

        final PushContentParams pushParams = PushContentParams.create()
            .contentIds( ContentIds.from( content1.getId() ) )
            .excludedContentIds( ContentIds.from( content1.getId() ) )
            .build();

        final PublishContentResult result = this.contentService.publish( pushParams );

        assertEquals( 0, result.getPushedContents().getSize() );
    }

    @Test
    void push_exclude_without_children()
    {
        createContentTree();

        final PushContentParams pushParams = PushContentParams.create()
            .contentIds( ContentIds.from( content1.getId() ) )
            .excludedContentIds( ContentIds.from( content1_1.getId() ) )
            .excludeDescendantsOf( ContentIds.from( content1.getId() ) )
            .build();

        final PublishContentResult result = this.contentService.publish( pushParams );

        assertEquals( 1, result.getPushedContents().getSize() );
    }

    @Disabled("This test is not correct; it should not be allowed to exclude parent if new")
    @Test
    void push_exclude_with_children()
    {
        createContentTree();

        this.contentService.create( CreateContentParams.create()
                                        .contentData( new PropertyTree() )
                                        .displayName( "content1_1_1" )
                                        .parent( content1_1.getPath() )
                                        .type( ContentTypeName.folder() )
                                        .build() );

        final PushContentParams pushParams = PushContentParams.create()
            .contentIds( ContentIds.from( content1.getId(), content2.getId() ) )
            .excludedContentIds( ContentIds.from( content1_1.getId() ) )
            .build();

        final PublishContentResult result = this.contentService.publish( pushParams );
        assertThat( result.getPushedContents() ).containsExactly( content1.getId(), content2.getId(), content2_1.getId() );
    }


    /**
     * ./content1
     * ../content1_1 -> Ref:content2_1_1
     * ./content2
     * ../content2_1
     * ../../content2_1_1
     * ./content3
     */
    @Test
    void push_without_dependencies()
    {
        createContentTree2();

        final PushContentParams pushParams =
            PushContentParams.create().contentIds( ContentIds.from( content1.getId() ) ).includeDependencies( false ).build();

        final PublishContentResult result = this.contentService.publish( pushParams );

        assertEquals( 2, result.getPushedContents().getSize() );
        assertTrue( result.getPushedContents().contains( content1.getId() ) );
        assertTrue( result.getPushedContents().contains( content1_1.getId() ) );
    }

    /**
     * /content1
     * /content1_1
     * /content2
     * /content2_1 -> ref:content1_1
     */
    @Test
    void publish_move_delete_old_parent()
    {
        createContentTree();

        this.contentService.publish( PushContentParams.create()
                                         .contentIds( ContentIds.from( content1.getId() ) )
                                         .excludeDescendantsOf( ContentIds.from( content1.getId() ) )
                                         .build() );

        final MoveContentParams params =
            MoveContentParams.create().contentId( content2_1.getId() ).parentContentPath( content1.getPath() ).build();

        this.contentService.move( params );

        this.contentService.delete( DeleteContentParams.create().contentPath( content2.getPath() ).build() );

        final Content movedContent =
            this.contentService.getByPath( ContentPath.from( content1.getPath(), content2_1.getName() ) );

        assertNotNull( movedContent );
    }

    @Test
    void move_delete_published()
    {
        final Content s1 = createContent( ContentPath.ROOT, "s1" );
        final Content f1 = createContent( s1.getPath(), "f1" );
        final Content c1 = createContent( f1.getPath(), "c1" );
        publishContent( s1.getId() );

        // Move to f2, delete f1
        final Content f2 = createContent( s1.getPath(), "f2" );
        moveContent( c1.getId(), f2.getPath() );
        DeleteContentsResult result = deleteContent( f1.getPath() );

        assertEquals( 1, result.getDeletedContents().getSize() );
        assertTrue( result.getDeletedContents().contains( f1.getId() ) );

        assertEquals( 2, result.getUnpublishedContents().getSize() );
        assertTrue( result.getUnpublishedContents().contains( f1.getId() ) );
        assertTrue( result.getUnpublishedContents().contains( c1.getId() ) );

    }

    /*

     create /a
     create /a/a1
     create /a/a2
     create /b

     publish /a, /a/a1, /a/a2

     rename /a name to "a_old"
     rename /b to "a"
     move a1 and a2 to the new /a

     publish the new /a ("b") and check "Include child items"
     */

    @Test
    void publish_rename_move_publish()
    {
        final Content a = createContent( ContentPath.ROOT, "a" );
        final Content b = createContent( ContentPath.ROOT, "b" );
        final Content a1 = createContent( a.getPath(), "a1" );
        final Content a2 = createContent( a.getPath(), "a2" );
        publishContent( a.getId() );

        System.out.println( "After initial push:" );
        printContentTree( getByPath( ContentPath.ROOT ).getId() );
        printContentTree( getByPath( ContentPath.ROOT ).getId(), ctxMaster() );

        renameContent( a.getId(), "a_old" );
        renameContent( b.getId(), "a" );

        moveContent( a1.getId(), "/a" );
        moveContent( a2.getId(), "/a" );

        publishContent( b.getId() );

        System.out.println();
        System.out.println( "After second push:" );
        printContentTree( getByPath( ContentPath.ROOT ).getId() );
        printContentTree( getByPath( ContentPath.ROOT ).getId(), ctxMaster() );

        assertStatus( b.getId(), CompareStatus.EQUAL );
    }

    @Test
    void move_children()
    {
        final Content a = createContent( ContentPath.ROOT, "a" );
        final Content a1 = createContent( a.getPath(), "a1" );

        publishContent( a.getId() );

        renameContent( a.getId(), "a_old" );
        final Content newA = createContent( ContentPath.ROOT, "a" );

        final PublishContentResult result = publishContent( newA.getId() );
        assertThat( result.getPushedContents() ).containsExactly( newA.getId(), a.getId(), a1.getId() );

        assertEquals( ContentPath.from( "/a_old" ), getInMaster( a.getId() ).getPath() );
        assertEquals( ContentPath.from( "/a_old/a1" ), getInMaster( a1.getId() ).getPath() );
        assertEquals( ContentPath.from( "/a" ), getInMaster( newA.getId() ).getPath() );
    }

    @Test
    void publish_with_message()
    {
        final Content content = createContent( ContentPath.ROOT, "a" );

        this.contentService.publish(
            PushContentParams.create().contentIds( ContentIds.from( content.getId() ) ).message( "My message" ).build() );

        FindContentVersionsResult versions =
            this.contentService.getVersions( FindContentVersionsParams.create().contentId( content.getId() ).build() );

        Iterator<ContentVersion> iterator = versions.getContentVersions().iterator();
        assertTrue( iterator.hasNext() );

        ContentVersion version = iterator.next();
        assertEquals( "My message", version.getComment() );
    }

    @Test
    void publish_with_message_no_message()
    {
        final Content content = createContent( ContentPath.ROOT, "a" );

        this.contentService.publish( PushContentParams.create().contentIds( ContentIds.from( content.getId() ) ).message( null ).build() );

        FindContentVersionsResult versions =
            this.contentService.getVersions( FindContentVersionsParams.create().contentId( content.getId() ).build() );

        Iterator<ContentVersion> iterator = versions.getContentVersions().iterator();
        assertTrue( iterator.hasNext() );

        ContentVersion version = iterator.next();
        assertNull( version.getComment() );
    }

    @Test
    void audit_data()
    {
        final ArgumentCaptor<LogAuditLogParams> captor = ArgumentCaptor.forClass( LogAuditLogParams.class );

        final CreateContentParams createContentParams = CreateContentParams.create()
            .contentData( new PropertyTree() )
            .displayName( "This is my content" )
            .name( "myContent" )
            .parent( ContentPath.ROOT )
            .type( ContentTypeName.folder() )
            .build();

        final Content content = this.contentService.create( createContentParams );

        Mockito.reset( auditLogService );

        this.contentService.publish(
            PushContentParams.create().contentIds( ContentIds.from( content.getId() ) ).includeDependencies( false ).build() );

        verify( auditLogService, atMostOnce() ).log( captor.capture() );

        final LogAuditLogParams log = captor.getValue();
        assertThat( log ).extracting( LogAuditLogParams::getType).isEqualTo( "system.content.publish" ) ;

        assertThat( log ).extracting( l -> l.getData().getSet( "result" ) )
            .extracting( result -> result.getString( "pushedContents" ), result -> result.getString( "deletedContents" ),
                         result -> result.getString( "pendingContents" ) )
            .containsExactly(content.getId().toString(), null, null);
    }

    @Test
    void push_not_from_draft()
    {
        final CreateContentParams createContentParams = CreateContentParams.create()
            .contentData( new PropertyTree() )
            .displayName( "This is my content" )
            .name( "myContent" )
            .parent( ContentPath.ROOT )
            .type( ContentTypeName.folder() )
            .build();

        final Content content = this.contentService.create( createContentParams );

        assertThrows( IllegalStateException.class, () -> ContextBuilder.from( ContextAccessor.current() )
            .branch( ContentConstants.BRANCH_MASTER )
            .build()
            .callWith( () -> this.contentService.publish(
                PushContentParams.create().contentIds( ContentIds.from( content.getId() ) ).includeDependencies( false ).build() ) ) );
    }

    private void renameContent( final ContentId contentId, final String newName )
    {
        this.contentService.move(
            MoveContentParams.create().contentId( contentId ).newName( ContentName.from( newName ) ).build() );
    }

    private Content getInMaster( final ContentId contentId )
    {
        return ctxMaster().callWith( () -> this.contentService.getById( contentId ) );
    }

    private Content getByPath( final ContentPath path )
    {
        return this.contentService.getByPath( path );
    }

    private void assertStatus( final ContentId id, CompareStatus status )
    {
        final CompareContentResult compare =
            this.contentService.compare( CompareContentsParams.create().contentIds( ContentIds.from( id ) ).build() ).iterator().next();
        assertEquals( status, compare.getCompareStatus() );
    }

    private void moveContent( final ContentId contentId, final ContentPath newParent )
    {
        final MoveContentParams params = MoveContentParams.create().contentId( contentId ).parentContentPath( newParent ).build();
        this.contentService.move( params );
    }

    private void moveContent( final ContentId contentId, final String newParent )
    {
        moveContent( contentId, ContentPath.from( newParent ) );
    }

    private DeleteContentsResult deleteContent( final ContentPath f1Path )
    {
        final DeleteContentParams deleteContentParams = DeleteContentParams.create().contentPath( f1Path ).build();

        return this.contentService.delete( deleteContentParams );
    }

    private PublishContentResult publishContent( final ContentId... contentIds )
    {
        return this.contentService.publish( PushContentParams.create().contentIds( ContentIds.from( contentIds ) ).build() );
    }

    /**
     * /content1
     * /content1_1
     * /content2
     * /content2_1 -> ref:content1_1
     */
    private void createContentTree()
    {
        this.content1 = this.contentService.create( CreateContentParams.create()
                                                        .contentData( new PropertyTree() )
                                                        .displayName( "content1" )
                                                        .parent( ContentPath.ROOT )
                                                        .type( ContentTypeName.folder() )
                                                        .build() );

        this.content2 = this.contentService.create( CreateContentParams.create()
                                                        .contentData( new PropertyTree() )
                                                        .displayName( "content2" )
                                                        .parent( ContentPath.ROOT )
                                                        .type( ContentTypeName.folder() )
                                                        .build() );

        this.content1_1 = this.contentService.create( CreateContentParams.create()
                                                          .contentData( new PropertyTree() )
                                                          .displayName( "content1_1" )
                                                          .parent( content1.getPath() )
                                                          .type( ContentTypeName.folder() )
                                                          .build() );

        final PropertyTree data = new PropertyTree();
        data.addReference( "myRef", Reference.from( content1_1.getId().toString() ) );

        this.content2_1 = this.contentService.create( CreateContentParams.create()
                                                          .contentData( data )
                                                          .displayName( "content2_1" )
                                                          .parent( content2.getPath() )
                                                          .type( ContentTypeName.folder() )
                                                          .build() );
    }

    /**
     * ./content1
     * ../content1_1 -> Ref:content2_1_1
     * ./content2
     * ../content2_1
     * ../../content2_1_1
     * ./content3
     */
    private void createContentTree2()
    {
        this.content1 = this.contentService.create( CreateContentParams.create()
                                                        .contentData( new PropertyTree() )
                                                        .displayName( "content1" )
                                                        .parent( ContentPath.ROOT )
                                                        .type( ContentTypeName.folder() )
                                                        .build() );

        this.content2 = this.contentService.create( CreateContentParams.create()
                                                        .contentData( new PropertyTree() )
                                                        .displayName( "content2" )
                                                        .parent( ContentPath.ROOT )
                                                        .type( ContentTypeName.folder() )
                                                        .build() );

        this.content2_1 = this.contentService.create( CreateContentParams.create()
                                                          .contentData( new PropertyTree() )
                                                          .displayName( "content2_1" )
                                                          .parent( content2.getPath() )
                                                          .type( ContentTypeName.folder() )
                                                          .build() );

        final Content content2_1_1 = this.contentService.create( CreateContentParams.create()
                                                                     .contentData( new PropertyTree() )
                                                                     .displayName( "content2_1_1" )
                                                                     .parent( content2_1.getPath() )
                                                                     .type( ContentTypeName.folder() )
                                                                     .build() );

        final PropertyTree data = new PropertyTree();
        data.addReference( "myRef", Reference.from( content2_1_1.getId().toString() ) );

        this.content1_1 = this.contentService.create( CreateContentParams.create()
                                                          .contentData( data )
                                                          .displayName( "content1_1" )
                                                          .parent( content1.getPath() )
                                                          .type( ContentTypeName.folder() )
                                                          .build() );

        this.contentService.create( CreateContentParams.create()
                                        .contentData( new PropertyTree() )
                                        .displayName( "content3" )
                                        .parent( ContentPath.ROOT )
                                        .type( ContentTypeName.folder() )
                                        .build() );
    }
}
