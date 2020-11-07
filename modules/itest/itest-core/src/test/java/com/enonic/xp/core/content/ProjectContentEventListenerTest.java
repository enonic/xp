package com.enonic.xp.core.content;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.RejectedExecutionException;

import org.assertj.core.util.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.DeleteContentParams;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.content.FindContentByParentParams;
import com.enonic.xp.content.FindContentByParentResult;
import com.enonic.xp.content.MoveContentParams;
import com.enonic.xp.content.RenameContentParams;
import com.enonic.xp.content.ReorderChildContentsParams;
import com.enonic.xp.content.ReorderChildParams;
import com.enonic.xp.content.SetContentChildOrderParams;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.WorkflowInfo;
import com.enonic.xp.content.WorkflowState;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.impl.content.ParentContentSynchronizer;
import com.enonic.xp.core.impl.content.ProjectContentEventListener;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.event.Event;
import com.enonic.xp.form.Form;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageRegions;
import com.enonic.xp.page.PageTemplateKey;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.region.Region;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.security.PrincipalKey;

import static com.enonic.xp.media.MediaInfo.IMAGE_INFO_IMAGE_HEIGHT;
import static com.enonic.xp.media.MediaInfo.IMAGE_INFO_IMAGE_WIDTH;
import static com.enonic.xp.media.MediaInfo.IMAGE_INFO_PIXEL_SIZE;
import static com.enonic.xp.media.MediaInfo.MEDIA_INFO_BYTE_SIZE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProjectContentEventListenerTest
    extends AbstractContentSynchronizerTest
{
    private ProjectContentEventListener listener;

    private ArgumentCaptor<Event> eventCaptor;

    private Set<Event> handledEvents;


    @BeforeEach
    protected void setUpNode()
        throws Exception
    {
        super.setUpNode();

        final ParentContentSynchronizer synchronizer = new ParentContentSynchronizer( contentService, mediaInfoService );
        listener = new ProjectContentEventListener( this.projectService, synchronizer );

        eventCaptor = ArgumentCaptor.forClass( Event.class );
        handledEvents = Sets.newHashSet();
    }

    @Test
    public void testCreated()
        throws InterruptedException
    {
        final Content sourceContent = sourceContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );

        handleEvents();

        final Content targetContent = targetContext.callWith( () -> contentService.getById( sourceContent.getId() ) );

        compareSynched( sourceContent, targetContent );
    }

    @Test
    public void testSyncCreateWithExistedLocalName()
        throws InterruptedException
    {
        targetContext.callWith( () -> createContent( ContentPath.ROOT, "localName" ) );

        final Content sourceContent = sourceContext.callWith( () -> createContent( ContentPath.ROOT, "localName" ) );

        handleEvents();

        targetContext.runWith( () -> {
            final Content targetContent = contentService.getById( sourceContent.getId() );
            assertEquals( "localName-1", targetContent.getName().toString() );
        } );
    }

    @Test
    public void testUpdated()
        throws InterruptedException
    {
        final Content sourceContent = sourceContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );

        final Content updatedContent = sourceContext.callWith( () -> {

            final Content updated = contentService.update( new UpdateContentParams().
                contentId( sourceContent.getId() ).
                editor( ( edit -> {
                    edit.data = new PropertyTree();
                    edit.displayName = "newDisplayName";
                    edit.extraDatas = ExtraDatas.create().
                        add( createExtraData() ).
                        build();
                    edit.owner = PrincipalKey.from( "user:system:newOwner" );
                    edit.language = Locale.forLanguageTag( "no" );
                    edit.page = createPage();

                } ) ) );

            return updated;

        } );

        handleEvents();

        final Content targetContent = targetContext.callWith( () -> contentService.getById( sourceContent.getId() ) );

        compareSynched( updatedContent, targetContent );
        assertEquals( 4, targetContent.getInherit().size() );
    }


    @Test
    public void testUpdatedFromReadyToInProgress()
        throws InterruptedException
    {
        final Content sourceContent = sourceContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );

        sourceContext.callWith( () -> {

            contentService.update( new UpdateContentParams().
                contentId( sourceContent.getId() ).
                editor( ( edit -> edit.workflowInfo = WorkflowInfo.create().state( WorkflowState.READY ).build() ) ) );

            handleEvents();

            final Content sourceContentReady = contentService.update( new UpdateContentParams().
                contentId( sourceContent.getId() ).
                editor( ( edit -> edit.workflowInfo = WorkflowInfo.create().state( WorkflowState.IN_PROGRESS ).build() ) ) );

            handleEvents();

            return sourceContentReady;

        } );

        final Content targetContent = targetContext.callWith( () -> contentService.getById( sourceContent.getId() ) );

        assertEquals( WorkflowState.READY, targetContent.getWorkflowInfo().getState() );
    }

    @Test
    public void testUpdatedLocally()
        throws InterruptedException
    {
        final Content sourceContent = sourceContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );

        handleEvents();

        final Content updatedInChild = targetContext.callWith( () -> contentService.update( new UpdateContentParams().
            contentId( sourceContent.getId() ).
            editor( ( edit -> edit.data = new PropertyTree() ) ) ) );

        assertEquals( 2, updatedInChild.getInherit().size() );
        assertFalse( updatedInChild.getInherit().contains( ContentInheritType.CONTENT ) );
        assertFalse( updatedInChild.getInherit().contains( ContentInheritType.NAME ) );

        final Content updatedInParent = sourceContext.callWith( () -> contentService.update( new UpdateContentParams().
            contentId( sourceContent.getId() ).
            editor( ( edit -> edit.displayName = "new source display name" ) ) ) );

        handleEvents();

        assertNotEquals( updatedInParent.getDisplayName(),
                         targetContext.callWith( () -> contentService.getById( updatedInChild.getId() ).getDisplayName() ) );
    }

    @Test
    public void testMoved()
        throws InterruptedException
    {
        final Content sourceContent = sourceContext.callWith( () -> createContent( ContentPath.ROOT, "content" ) );
        final Content sourceChild = sourceContext.callWith( () -> createContent( sourceContent.getPath(), "child" ) );
        final Content sourceFolder = sourceContext.callWith( () -> createContent( ContentPath.ROOT, "folder" ) );

        handleEvents();

        sourceContext.runWith( () -> contentService.move( MoveContentParams.create().
            contentId( sourceContent.getId() ).
            parentContentPath( sourceFolder.getPath() ).
            build() ) );

        handleEvents();

        final Content targetContent = targetContext.callWith( () -> contentService.getById( sourceContent.getId() ) );
        final Content targetChild = targetContext.callWith( () -> contentService.getById( sourceChild.getId() ) );

        assertEquals( "/folder/content", targetContent.getPath().toString() );
        assertEquals( "/folder/content/child", targetChild.getPath().toString() );
    }

    @Test
    public void testMovedLocally()
        throws InterruptedException
    {
        final Content sourceContent1 = sourceContext.callWith( () -> createContent( ContentPath.ROOT, "content1" ) );
        final Content sourceContent2 = sourceContext.callWith( () -> createContent( ContentPath.ROOT, "content2" ) );
        final Content sourceContent3 = sourceContext.callWith( () -> createContent( ContentPath.ROOT, "content3" ) );

        handleEvents();

        targetContext.runWith( () -> contentService.move( MoveContentParams.create().
            contentId( sourceContent1.getId() ).
            parentContentPath( sourceContent2.getPath() ).
            build() ) );

        final Content targetMovedContent = targetContext.callWith( () -> contentService.getById( sourceContent1.getId() ) );

        assertEquals( 3, targetMovedContent.getInherit().size() );
        assertFalse( targetMovedContent.getInherit().contains( ContentInheritType.PARENT ) );

        sourceContext.runWith( () -> contentService.move( MoveContentParams.create().
            contentId( sourceContent1.getId() ).
            parentContentPath( sourceContent3.getPath() ).
            build() ) );

        assertEquals( "/content3/content1",
                      sourceContext.callWith( () -> contentService.getById( sourceContent1.getId() ) ).getPath().toString() );
        assertEquals( "/content2/content1",
                      targetContext.callWith( () -> contentService.getById( sourceContent1.getId() ) ).getPath().toString() );
    }

    @Test
    public void testSorted()
        throws InterruptedException
    {
        final Content sourceContent = sourceContext.callWith( () -> createContent( ContentPath.ROOT, "content" ) );
        final Content sourceChild1 = sourceContext.callWith( () -> createContent( sourceContent.getPath(), "child1" ) );
        final Content sourceChild2 = sourceContext.callWith( () -> createContent( sourceContent.getPath(), "child2" ) );
        final Content sourceChild3 = sourceContext.callWith( () -> createContent( sourceContent.getPath(), "child3" ) );

        handleEvents();

        sourceContext.runWith( () -> contentService.setChildOrder( SetContentChildOrderParams.create().
            contentId( sourceContent.getId() ).
            childOrder( ChildOrder.from( "_name DESC" ) ).
            build() ) );

        handleEvents();

        targetContext.runWith( () -> {
            final FindContentByParentResult result =
                contentService.findByParent( FindContentByParentParams.create().parentId( sourceContent.getId() ).build() );

            final Iterator<Content> iterator = result.getContents().iterator();

            assertEquals( sourceChild3.getId(), iterator.next().getId() );
            assertEquals( sourceChild2.getId(), iterator.next().getId() );
            assertEquals( sourceChild1.getId(), iterator.next().getId() );
        } );

    }

    @Test
    public void testSortedLocally()
        throws InterruptedException
    {
        final Content sourceContent = sourceContext.callWith( () -> createContent( ContentPath.ROOT, "content" ) );

        handleEvents();

        final Content sortedInChild = targetContext.callWith( () -> contentService.setChildOrder( SetContentChildOrderParams.create().
            contentId( sourceContent.getId() ).
            childOrder( ChildOrder.from( "_name DESC" ) ).
            build() ) );

        assertEquals( 3, sortedInChild.getInherit().size() );
        assertFalse( sortedInChild.getInherit().contains( ContentInheritType.SORT ) );

        final Content sortedInParent = sourceContext.callWith( () -> contentService.setChildOrder( SetContentChildOrderParams.create().
            contentId( sourceContent.getId() ).
            childOrder( ChildOrder.from( "_name ASC" ) ).
            build() ) );

        handleEvents();

        assertNotEquals( sortedInParent.getChildOrder(), targetContext.callWith( () -> contentService.getById( sortedInChild.getId() ) ) );

    }

    @Test
    public void testManualOrderUpdated()
        throws InterruptedException
    {
        final Content sourceContent = sourceContext.callWith( () -> createContent( ContentPath.ROOT, "content" ) );
        final Content sourceChild1 = sourceContext.callWith( () -> createContent( sourceContent.getPath(), "child1" ) );
        final Content sourceChild2 = sourceContext.callWith( () -> createContent( sourceContent.getPath(), "child2" ) );
        final Content sourceChild3 = sourceContext.callWith( () -> createContent( sourceContent.getPath(), "child3" ) );

        handleEvents();

        sourceContext.runWith( () -> contentService.setChildOrder( SetContentChildOrderParams.create().
            contentId( sourceContent.getId() ).
            childOrder( ChildOrder.from( "_name DESC" ) ).
            build() ) );

        handleEvents();

        targetContext.runWith( () -> {
            final FindContentByParentResult result =
                contentService.findByParent( FindContentByParentParams.create().parentId( sourceContent.getId() ).build() );

            final Iterator<Content> iterator = result.getContents().iterator();

            assertEquals( sourceChild3.getId(), iterator.next().getId() );
            assertEquals( sourceChild2.getId(), iterator.next().getId() );
            assertEquals( sourceChild1.getId(), iterator.next().getId() );
        } );

        sourceContext.runWith( () -> contentService.setChildOrder( SetContentChildOrderParams.create().
            contentId( sourceContent.getId() ).
            childOrder( ChildOrder.manualOrder() ).
            build() ) );

        handleEvents();

        sourceContext.runWith( () -> contentService.reorderChildren( ReorderChildContentsParams.create().
            contentId( sourceContent.getId() ).
            add( ReorderChildParams.create().
                contentToMove( sourceChild2.getId() ).
                contentToMoveBefore( sourceChild3.getId() ).
                build() ).
            add( ReorderChildParams.create().
                contentToMove( sourceChild1.getId() ).
                contentToMoveBefore( sourceChild3.getId() ).
                build() ).
            build() ) );

        handleEvents();

        targetContext.runWith( () -> {
            final FindContentByParentResult result =
                contentService.findByParent( FindContentByParentParams.create().parentId( sourceContent.getId() ).build() );

            final Iterator<Content> iterator = result.getContents().iterator();

            assertEquals( sourceChild2.getId(), iterator.next().getId() );
            assertEquals( sourceChild1.getId(), iterator.next().getId() );
            assertEquals( sourceChild3.getId(), iterator.next().getId() );
        } );

    }

    @Test
    public void testRenamed()
        throws InterruptedException
    {
        final Content sourceContent = sourceContext.callWith( () -> createContent( ContentPath.ROOT, "content" ) );
        final Content sourceChild1 = sourceContext.callWith( () -> createContent( sourceContent.getPath(), "child1" ) );
        final Content sourceChild2 = sourceContext.callWith( () -> createContent( sourceChild1.getPath(), "child2" ) );

        handleEvents();

        sourceContext.runWith( () -> contentService.rename( RenameContentParams.create().
            contentId( sourceContent.getId() ).
            newName( ContentName.from( "content-new" ) ).
            build() ) );

        handleEvents();

        targetContext.runWith( () -> {
            assertEquals( "/content-new", contentService.getById( sourceContent.getId() ).getPath().toString() );
            assertEquals( "/content-new/child1", contentService.getById( sourceChild1.getId() ).getPath().toString() );
            assertEquals( "/content-new/child1/child2", contentService.getById( sourceChild2.getId() ).getPath().toString() );
        } );

        sourceContext.runWith( () -> contentService.rename( RenameContentParams.create().
            contentId( sourceChild1.getId() ).
            newName( ContentName.from( "child1-new" ) ).
            build() ) );

        handleEvents();

        targetContext.runWith( () -> {
            assertEquals( "/content-new", contentService.getById( sourceContent.getId() ).getPath().toString() );
            assertEquals( "/content-new/child1-new", contentService.getById( sourceChild1.getId() ).getPath().toString() );
            assertEquals( "/content-new/child1-new/child2", contentService.getById( sourceChild2.getId() ).getPath().toString() );
        } );

        sourceContext.runWith( () -> contentService.rename( RenameContentParams.create().
            contentId( sourceChild2.getId() ).
            newName( ContentName.from( "child2-new" ) ).
            build() ) );

        handleEvents();

        targetContext.runWith( () -> {
            assertEquals( "/content-new", contentService.getById( sourceContent.getId() ).getPath().toString() );
            assertEquals( "/content-new/child1-new", contentService.getById( sourceChild1.getId() ).getPath().toString() );
            assertEquals( "/content-new/child1-new/child2-new", contentService.getById( sourceChild2.getId() ).getPath().toString() );
        } );
    }

    @Test
    public void testRenameToExisted()
        throws InterruptedException
    {
        final Content sourceContent = sourceContext.callWith( () -> createContent( ContentPath.ROOT, "content" ) );

        handleEvents();

        targetContext.runWith( () -> {
            createContent( ContentPath.ROOT, "newName" );
        } );

        sourceContext.runWith( () -> {
            contentService.rename( RenameContentParams.create().
                contentId( sourceContent.getId() ).
                newName( ContentName.from( "newName" ) ).
                build() );
        } );
        handleEvents();

        assertEquals( "newName-1", targetContext.callWith( () -> contentService.getById( sourceContent.getId() ) ).getName().toString() );
    }

    @Test
    public void testDeleted()
        throws InterruptedException
    {
        final Content sourceContent = sourceContext.callWith( () -> createContent( ContentPath.ROOT, "content" ) );
        final Content sourceChild1 = sourceContext.callWith( () -> createContent( sourceContent.getPath(), "child1" ) );
        final Content sourceChild2 = sourceContext.callWith( () -> createContent( sourceChild1.getPath(), "child2" ) );

        handleEvents();

        sourceContext.runWith( () -> contentService.deleteWithoutFetch( DeleteContentParams.create().
            contentPath( sourceContent.getPath() ).
            build() ) );

        handleEvents();

        targetContext.runWith( () -> {
            assertFalse( contentService.contentExists( sourceContent.getId() ) );
            assertFalse( contentService.contentExists( sourceChild1.getId() ) );
            assertFalse( contentService.contentExists( sourceChild2.getId() ) );
        } );
    }

    @Test
    public void testDeletedInherited()
        throws InterruptedException
    {
        final Content sourceContent = sourceContext.callWith( () -> createContent( ContentPath.ROOT, "content" ) );
        final Content sourceChild1 = sourceContext.callWith( () -> createContent( sourceContent.getPath(), "child1" ) );
        final Content sourceChild2 = sourceContext.callWith( () -> createContent( sourceChild1.getPath(), "child2" ) );

        handleEvents();

        targetContext.runWith( () -> contentService.deleteWithoutFetch( DeleteContentParams.create().
            contentPath( sourceContent.getPath() ).
            build() ) );

        handleEvents();

        targetContext.runWith( () -> {
            assertTrue( contentService.contentExists( sourceContent.getId() ) );
            assertTrue( contentService.contentExists( sourceChild1.getId() ) );
            assertTrue( contentService.contentExists( sourceChild2.getId() ) );
        } );
    }

    @Test
    public void testDeactivated()
    {
        listener.deactivate();
        sourceContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );

        assertThrows( RejectedExecutionException.class, this::handleEvents );
    }

    private ExtraData createExtraData()
    {
        final PropertyTree mediaData = new PropertyTree();
        mediaData.setLong( IMAGE_INFO_PIXEL_SIZE, 300L );
        mediaData.setLong( IMAGE_INFO_IMAGE_HEIGHT, 200L );
        mediaData.setLong( IMAGE_INFO_IMAGE_WIDTH, 300L );
        mediaData.setLong( MEDIA_INFO_BYTE_SIZE, 100000L );

        return new ExtraData( XDataName.from( "myApp:xData" ), mediaData );
    }

    private Page createPage()
    {
        PropertyTree componentConfig = new PropertyTree();
        componentConfig.setString( "my-prop", "value" );

        PartComponent component = PartComponent.create().
            descriptor( DescriptorKey.from( "mainapplication:partTemplateName" ) ).
            config( componentConfig ).
            build();

        Region region = Region.create().
            name( "my-region" ).
            add( component ).
            build();

        PageRegions regions = PageRegions.create().
            add( region ).
            build();

        PropertyTree pageConfig = new PropertyTree();
        pageConfig.setString( "background-color", "blue" );

        Mockito.when( partDescriptorService.getByKey( DescriptorKey.from( "mainapplication:partTemplateName" ) ) ).thenReturn(
            PartDescriptor.create().
                key( DescriptorKey.from( "mainapplication:partTemplateName" ) ).
                displayName( "my-component" ).
                config( Form.create().build() ).
                build() );

        return Page.create().
            template( PageTemplateKey.from( "mypagetemplate" ) ).
            regions( regions ).
            build();
    }

    private void handleEvents()
        throws InterruptedException
    {
        Mockito.verify( eventPublisher, Mockito.atLeastOnce() ).publish( eventCaptor.capture() );
        eventCaptor.getAllValues().stream().
            filter( event -> !handledEvents.contains( event ) ).
            forEach( listener::onEvent );
        handledEvents.addAll( eventCaptor.getAllValues() );
        Thread.sleep( 1000 );

    }

    private Event event( String type, Contents contents )
    {
        return Event.create( type ).
            distributed( true ).
            value( "nodes", contentsToList( contents ) ).build();
    }

    private ImmutableList contentsToList( final Contents contents )
    {
        List<ImmutableMap> list = new ArrayList<>();
        contents.stream().
            map( this::contentToMap ).
            forEach( list::add );

        return ImmutableList.copyOf( list );
    }


    private ImmutableMap contentToMap( final Content content )
    {
        return ImmutableMap.builder().
            put( "id", content.getId().toString() ).
            put( "path", "/content/" + content.getPath().asRelative().toString() ).
            put( "branch", ContextAccessor.current().getBranch().getValue() ).
            put( "repo", ContextAccessor.current().getRepositoryId().toString() ).
            build();
    }


  /*  private Content createContent( final ContentPath parent, final String name )
    {
        final PropertyTree data = new PropertyTree();
        data.addStrings( "stringField", "stringValue" );

        final CreateContentParams createParent = CreateContentParams.create().
            contentData( data ).
            name( name ).
            displayName( name ).
            parent( parent ).
            type( ContentTypeName.folder() ).
            build();

        return this.contentService.create( createParent );
    }

    private void compareSynched( final Content sourceContent, final Content targetContent )
    {
        assertEquals( sourceContent.getId(), targetContent.getId() );
        assertEquals( sourceContent.getName(), targetContent.getName() );
        assertEquals( sourceContent.getDisplayName(), targetContent.getDisplayName() );
        assertEquals( sourceContent.getData(), targetContent.getData() );
        assertEquals( sourceContent.getPath(), targetContent.getPath() );
        assertEquals( sourceContent.getAllExtraData(), targetContent.getAllExtraData() );
        assertEquals( sourceContent.getAttachments(), targetContent.getAttachments() );
        assertEquals( sourceContent.getOwner(), targetContent.getOwner() );
        assertEquals( sourceContent.getLanguage(), targetContent.getLanguage() );
        assertEquals( sourceContent.getWorkflowInfo(), targetContent.getWorkflowInfo() );
        assertEquals( sourceContent.getPage(), targetContent.getPage() );
        assertEquals( sourceContent.isValid(), targetContent.isValid() );
        assertEquals( sourceContent.inheritsPermissions(), targetContent.inheritsPermissions() );
        assertEquals( sourceContent.getCreatedTime(), targetContent.getCreatedTime() );

        assertNotEquals( sourceContent.getPermissions(), targetContent.getPermissions() );

        assertTrue( targetContent.getInherit().containsAll( EnumSet.allOf( ContentInheritType.class ) ) );
    }*/
}