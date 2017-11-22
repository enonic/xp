package com.enonic.xp.core.content;

import java.time.Instant;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.CompareContentParams;
import com.enonic.xp.content.CompareContentResult;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.DeleteContentParams;
import com.enonic.xp.content.MoveContentParams;
import com.enonic.xp.content.PublishContentResult;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.content.RenameContentParams;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.util.Reference;

import static org.junit.Assert.*;

public class ContentServiceImplTest_publish
    extends AbstractContentServiceTest
{

    private static final String LINE_SEPARATOR = System.getProperty( "line.separator" );

    private Content content1, content2, content1_1, content1_2_offline, content2_1;

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
            name( "myContent" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build();

        final Content content = this.contentService.create( createContentParams );

        final PublishContentResult push = this.contentService.publish( PushContentParams.create().
            contentIds( ContentIds.from( content.getId() ) ).
            target( CTX_OTHER.getBranch() ).
            includeDependencies( false ).
            build() );

        assertEquals( 0, push.getDeletedContents().getSize() );
        assertEquals( 0, push.getFailedContents().getSize() );
        assertEquals( 1, push.getPushedContents().getSize() );
    }

    @Ignore
    @Test
    public void push_one_content_not_valid()
        throws Exception
    {

        ContentType contentType = ContentType.create().
            superType( ContentTypeName.structured() ).
            name( "myapplication:test" ).
            addFormItem( Input.create().name( "title" ).label( "Title" ).inputType( InputTypeName.TEXT_LINE ).required( true ).build() ).
            build();

        Mockito.when( this.contentTypeService.getByName( GetContentTypeParams.from( contentType.getName() ) ) ).
            thenReturn( contentType );

        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content" ).
            parent( ContentPath.ROOT ).
            type( contentType.getName() ).
            build();

        final Content content = this.contentService.create( createContentParams );

        final PublishContentResult push = this.contentService.publish( PushContentParams.create().
            contentIds( ContentIds.from( content.getId() ) ).
            target( WS_OTHER ).
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
            target( WS_OTHER ).
            build();

        final PublishContentResult push = this.contentService.publish( pushParams );
        assertEquals( 1, push.getPushedContents().getSize() );

        contentService.delete( DeleteContentParams.create().
            contentPath( content.getPath() ).
            build() );

        final PublishContentResult pushWithDeleted = this.contentService.publish( pushParams );
        assertEquals( 1, pushWithDeleted.getDeletedContents().getSize() );
    }

    @Test
    public void push_dependencies()
        throws Exception
    {
        createContentTree();

        final PushContentParams pushParams = PushContentParams.create().
            contentIds( ContentIds.from( content2.getId() ) ).
            target( WS_OTHER ).
            build();

        final PublishContentResult result = this.contentService.publish( pushParams );

        assertEquals( 4, result.getPushedContents().getSize() );
    }

    @Test
    public void push_with_children()
        throws Exception
    {
        createContentTree();

        final PushContentParams pushParams = PushContentParams.create().
            contentIds( ContentIds.from( content1.getId() ) ).
            target( WS_OTHER ).
            build();

        final PublishContentResult result = this.contentService.publish( pushParams );
        assertEquals( 3, result.getPushedContents().getSize() );
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
    public void push_parent_of_dependencies()
        throws Exception
    {
        createContentTree2();

        final PushContentParams pushParams = PushContentParams.create().
            contentIds( ContentIds.from( content1_1.getId() ) ).
            excludeChildrenIds( ContentIds.from( content1_1.getId() ) ).
            target( WS_OTHER ).
            build();

        final PublishContentResult result = this.contentService.publish( pushParams );

        assertEquals( 5, result.getPushedContents().getSize() );
        assertEquals( 0, result.getFailedContents().getSize() );
    }


    @Ignore("This test is not correct; it should not be allowed to exclude parent if new")
    @Test
    public void push_exclude_empty()
        throws Exception
    {
        createContentTree();

        final PushContentParams pushParams = PushContentParams.create().
            contentIds( ContentIds.from( content1.getId() ) ).
            excludedContentIds( ContentIds.from( content1.getId() ) ).
            target( WS_OTHER ).
            build();

        refresh();

        final PublishContentResult result = this.contentService.publish( pushParams );

        assertEquals( 0, result.getPushedContents().getSize() );
    }

    @Test
    public void push_exclude_without_children()
        throws Exception
    {
        createContentTree();

        final PushContentParams pushParams = PushContentParams.create().
            contentIds( ContentIds.from( content1.getId() ) ).
            excludedContentIds( ContentIds.from( content1_1.getId() ) ).
            excludeChildrenIds( ContentIds.from( content1.getId() ) ).
            target( WS_OTHER ).
            build();

        refresh();

        final PublishContentResult result = this.contentService.publish( pushParams );

        assertEquals( 1, result.getPushedContents().getSize() );
    }

    @Ignore("This test is not correct; it should not be allowed to exclude parent if new")
    @Test
    public void push_exclude_with_children()
        throws Exception
    {
        createContentTree();

        this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "content1_1_1" ).
            parent( content1_1.getPath() ).
            type( ContentTypeName.folder() ).
            build() );

        refresh();

        final PushContentParams pushParams = PushContentParams.create().
            contentIds( ContentIds.from( content1.getId(), content2.getId() ) ).
            excludedContentIds( ContentIds.from( content1_1.getId() ) ).
            target( WS_OTHER ).
            build();

        final PublishContentResult result = this.contentService.publish( pushParams );

        assertPushed( result, ContentIds.from( content1.getId(), content2.getId(), content2_1.getId() ) );
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
    public void push_without_dependencies()
        throws Exception
    {
        createContentTree2();

        final PushContentParams pushParams = PushContentParams.create().
            contentIds( ContentIds.from( content1.getId() ) ).
            includeDependencies( false ).
            target( WS_OTHER ).
            build();

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
    public void publish_move_delete_old_parent()
        throws Exception
    {
        createContentTree();

        this.contentService.publish( PushContentParams.create().
            contentIds( ContentIds.from( content1.getId() ) ).
            target( WS_OTHER ).
            excludeChildrenIds( ContentIds.from( content1.getId() ) ).
            build() );

        final MoveContentParams params = MoveContentParams.create().
            contentId( content2_1.getId() ).
            parentContentPath( content1.getPath() ).
            build();

        this.contentService.move( params );

        this.contentService.delete( DeleteContentParams.create().
            contentPath( content2.getPath() ).
            build() );

        final Content movedContent =
            this.contentService.getByPath( ContentPath.from( content1.getPath(), content2_1.getName().toString() ) );

        assertNotNull( movedContent );
    }

    @Test
    public void publish_move_delete_moved_also_published()
        throws Exception
    {
        final Content s1 = createContent( ContentPath.ROOT, "s1" );
        final Content f1 = createContent( s1.getPath(), "f1" );
        final Content c1 = createContent( f1.getPath(), "c1" );
        doPublish( ContentIds.empty(), s1.getId() );

        // Move to f2, delete f1
        final Content f2 = createContent( s1.getPath(), "f2" );
        doMove( c1.getId(), f2.getPath() );
        doDelete( f1.getPath(), false );

        // include children = false should be overridden since its a pending delete
        final PublishContentResult result = doPublish( ContentIds.from( f1.getId() ), f1.getId() );
        assertTrue( result.getPushedContents().contains( c1.getId() ) );
        assertStatus( c1.getId(), CompareStatus.EQUAL );
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
    public void publish_rename_move_publish()
        throws Exception
    {
        final Content a = createContent( ContentPath.ROOT, "a" );
        final Content b = createContent( ContentPath.ROOT, "b" );
        final Content a1 = createContent( a.getPath(), "a1" );
        final Content a2 = createContent( a.getPath(), "a2" );
        doPublish( ContentIds.empty(), a.getId() );

        System.out.println( "After initial push:" );
        printContentTree( getByPath( ContentPath.ROOT ).getId() );
        printContentTree( getByPath( ContentPath.ROOT ).getId(), CTX_OTHER );

        doRename( a.getId(), "a_old" );
        doRename( b.getId(), "a" );

        doMove( a1.getId(), "/a" );
        doMove( a2.getId(), "/a" );

        doPublish( ContentIds.empty(), b.getId() );

        System.out.println( "" );
        System.out.println( "After second push:" );
        printContentTree( getByPath( ContentPath.ROOT ).getId() );
        printContentTree( getByPath( ContentPath.ROOT ).getId(), CTX_OTHER );

        assertStatus( b.getId(), CompareStatus.EQUAL );
    }

    private Content doRename( final ContentId contentId, final String newName )
    {
        return this.contentService.rename( RenameContentParams.create().
            contentId( contentId ).
            newName( ContentName.from( newName ) ).
            build() );
    }

    private Content getInMaster( final ContentId contentId )
    {
        return CTX_OTHER.callWith( () -> this.contentService.getById( contentId ) );
    }

    private Content getByPath( final ContentPath path )
    {
        return this.contentService.getByPath( path );
    }

    private Content doGet( final ContentId contentId )
    {
        return this.contentService.getById( contentId );
    }

    private void assertStatus( final ContentId id, CompareStatus status )
    {
        final CompareContentResult compare = this.contentService.compare( new CompareContentParams( id, WS_OTHER ) );
        assertEquals( status, compare.getCompareStatus() );
    }

    private void doMove( final ContentId contentId, final ContentPath newParent )
    {
        final MoveContentParams params = MoveContentParams.create().
            contentId( contentId ).
            parentContentPath( newParent ).
            build();
        this.contentService.move( params );
    }

    private void doMove( final ContentId contentId, final String newParent )
    {
        final MoveContentParams params = MoveContentParams.create().
            contentId( contentId ).
            parentContentPath( ContentPath.from( newParent )  ).
            build();
        this.contentService.move( params );
    }

    private void doDelete( final ContentPath f1Path, boolean instantly )
    {
        final DeleteContentParams deleteContentParams = DeleteContentParams.create().
            contentPath( f1Path ).
            deleteOnline( instantly ).
            build();

        this.contentService.deleteWithoutFetch( deleteContentParams );
    }

    private PublishContentResult doPublish( final ContentIds excludeChildrenIds, final ContentId... contentIds )
    {
        return this.contentService.publish( PushContentParams.create().
            excludeChildrenIds( excludeChildrenIds ).
            contentIds( ContentIds.from( contentIds ) ).
            target( WS_OTHER ).
            build() );
    }

    /**
     * /content1
     * /content1_1
     * /content1_2_offline
     * /content2
     * /content2_1 -> ref:content1_1
     */
    private void createContentTree()
    {
        this.content1 = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "content1" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build() );

        this.content2 = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "content2" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build() );

        this.content1_1 = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "content1_1" ).
            parent( content1.getPath() ).
            type( ContentTypeName.folder() ).
            build() );

        this.content1_2_offline = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "content1_2_offline" ).
            parent( content1.getPath() ).
            type( ContentTypeName.folder() ).
            contentPublishInfo( ContentPublishInfo.create().first( Instant.now() ).build() ).
            build() );

        final PropertyTree data = new PropertyTree();
        data.addReference( "myRef", Reference.from( content1_1.getId().toString() ) );

        this.content2_1 = this.contentService.create( CreateContentParams.create().
            contentData( data ).
            displayName( "content2_1" ).
            parent( content2.getPath() ).
            type( ContentTypeName.folder() ).
            build() );

        refresh();
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
        this.content1 = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "content1" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build() );

        this.content2 = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "content2" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build() );

        this.content2_1 = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "content2_1" ).
            parent( content2.getPath() ).
            type( ContentTypeName.folder() ).
            build() );

        final Content content2_1_1 = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "content2_1_1" ).
            parent( content2_1.getPath() ).
            type( ContentTypeName.folder() ).
            build() );

        final PropertyTree data = new PropertyTree();
        data.addReference( "myRef", Reference.from( content2_1_1.getId().toString() ) );

        this.content1_1 = this.contentService.create( CreateContentParams.create().
            contentData( data ).
            displayName( "content1_1" ).
            parent( content1.getPath() ).
            type( ContentTypeName.folder() ).
            build() );

        this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "content3" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build() );

        refresh();
    }

    private void assertPushed( final PublishContentResult result, final ContentIds pushed )
    {
        assertContent( result, pushed, ContentIds.empty(), ContentIds.empty() );
    }

    private void assertContent( final PublishContentResult result, final ContentIds pushed, final ContentIds deleted,
                                final ContentIds failed )
    {

        StringBuilder message = new StringBuilder();

        boolean hasFailed = checkCollection( pushed, result.getPushedContents(), message ) ||
            checkCollection( deleted, result.getDeletedContents(), message ) ||
            checkCollection( failed, result.getFailedContents(), message );

        if ( hasFailed )
        {
            fail( message.toString() );
        }
    }

    private boolean checkCollection( final ContentIds currentExpected, final ContentIds currentResult, final StringBuilder message )
    {
        boolean hasFailed = false;

        for ( final ContentId expectedEntry : currentExpected )
        {
            message.append( LINE_SEPARATOR + "Content: " + getName( expectedEntry ) + " expected: " );

            if ( !currentResult.contains( expectedEntry ) )
            {
                message.append( "<FAIL>" );
                hasFailed = true;
            }
            else
            {
                message.append( "OK" );
            }
        }

        for ( final ContentId found : currentResult )
        {

            if ( !currentExpected.contains( found ) )
            {
                message.append( LINE_SEPARATOR + "Content: " + getName( found ) + " not expected" );
                hasFailed = true;
            }
        }

        return hasFailed;
    }

    private final String getName( final ContentId contentId )
    {
        return this.contentService.getById( contentId ).getPath().toString();
    }

}
