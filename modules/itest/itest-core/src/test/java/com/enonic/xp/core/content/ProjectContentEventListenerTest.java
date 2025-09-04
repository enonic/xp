package com.enonic.xp.core.content;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.common.collect.ImmutableMap;

import com.enonic.xp.archive.ArchiveContentParams;
import com.enonic.xp.archive.RestoreContentParams;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.DeleteContentParams;
import com.enonic.xp.content.DuplicateContentParams;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.content.FindContentByParentParams;
import com.enonic.xp.content.FindContentByParentResult;
import com.enonic.xp.content.MoveContentParams;
import com.enonic.xp.content.ProjectSyncParams;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.content.RenameContentParams;
import com.enonic.xp.content.ReorderChildContentParams;
import com.enonic.xp.content.SortContentParams;
import com.enonic.xp.content.SortContentResult;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.WorkflowInfo;
import com.enonic.xp.content.WorkflowState;
import com.enonic.xp.core.impl.content.ParentContentSynchronizer;
import com.enonic.xp.core.impl.content.ProjectContentEventListener;
import com.enonic.xp.core.impl.content.SyncContentServiceImpl;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.event.Event;
import com.enonic.xp.form.Form;
import com.enonic.xp.index.ChildOrder;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProjectContentEventListenerTest
    extends AbstractContentSynchronizerTest
{
    private SyncContentServiceImpl syncContentService;

    @BeforeEach
    void setUp()
    {
        final ParentContentSynchronizer synchronizer = new ParentContentSynchronizer( contentService );
        listener = new ProjectContentEventListener( this.projectService, synchronizer, Runnable::run );

        syncContentService =
            new SyncContentServiceImpl( contentTypeService, nodeService, eventPublisher, projectService, contentService, synchronizer );
    }

    @Test
    public void testCreatedDiffParentsSameName()
    {
        final Content firstContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );
        final Content secondContent = secondProjectContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );

        handleEvents();

        final Content firstTargetContent = layerContext.callWith( () -> contentService.getById( firstContent.getId() ) );
        final Content secondTargetContent = layerContext.callWith( () -> contentService.getById( secondContent.getId() ) );

        compareSynched( firstContent, firstTargetContent );
        assertNotEquals( secondContent.getName(), secondTargetContent.getName() );
        assertEquals( ContentName.from( "name-1" ), secondTargetContent.getName() );
    }

    @Test
    public void testCreated()
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );

        handleEvents();

        final Content targetContent = layerContext.callWith( () -> contentService.getById( sourceContent.getId() ) );

        compareSynched( sourceContent, targetContent );
        assertEquals( project.getName(), targetContent.getOriginProject() );

        final Content secondParentContent = secondProjectContext.callWith( () -> createContent( ContentPath.ROOT, "name1" ) );

        handleEvents();

        final Content secondTargetContent = layerContext.callWith( () -> contentService.getById( secondParentContent.getId() ) );

        compareSynched( secondParentContent, secondTargetContent );
    }

    @Test
    public void testSyncCreateWithExistedLocalName()
    {
        layerContext.callWith( () -> createContent( ContentPath.ROOT, "localName" ) );

        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "localName" ) );

        handleEvents();

        layerContext.runWith( () -> {
            final Content targetContent = contentService.getById( sourceContent.getId() );
            assertEquals( "localName-1", targetContent.getName().toString() );
        } );
    }

    @Test
    public void testSyncDuplicateWithExistedLocalName()
    {
        layerContext.callWith( () -> createContent( ContentPath.ROOT, "localName-copy" ) );
        layerContext.callWith( () -> createContent( ContentPath.ROOT, "localName-copy-1" ) );

        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "localName" ) );

        handleEvents();

        final ContentId duplicatedContentId = projectContext.callWith(
            () -> contentService.duplicate( DuplicateContentParams.create().contentId( sourceContent.getId() ).build() )
                .getDuplicatedContents()
                .first() );

        handleEvents();

        layerContext.runWith( () -> {
            final Content duplicatedTargetContent = contentService.getById( duplicatedContentId );
            assertEquals( "localName-copy-1-1", duplicatedTargetContent.getName().toString() );
            assertEquals( 3, duplicatedTargetContent.getInherit().size() );
            assertFalse( duplicatedTargetContent.getInherit().contains( ContentInheritType.NAME ) );
        } );
    }

    @Test
    public void testDuplicateInherited()
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "localName" ) );

        handleEvents();

        final ContentId duplicatedContentId = layerContext.callWith(
            () -> contentService.duplicate( DuplicateContentParams.create().contentId( sourceContent.getId() ).build() )
                .getDuplicatedContents()
                .first() );

        layerContext.runWith( () -> {
            final Content duplicatedTargetContent = contentService.getById( duplicatedContentId );
            assertEquals( "localName-copy", duplicatedTargetContent.getName().toString() );
            assertTrue( duplicatedTargetContent.getInherit().isEmpty() );
        } );
    }

    @Test
    public void testDuplicateInheritedWithChildren()
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "localContent" ) );
        final Content sourceChild1 = projectContext.callWith( () -> createContent( sourceContent.getPath(), "localChild1" ) );
        projectContext.callWith( () -> createContent( sourceChild1.getPath(), "localChild2" ) );

        handleEvents();

        final ContentId duplicatedContentId = layerContext.callWith(
            () -> contentService.duplicate( DuplicateContentParams.create().contentId( sourceContent.getId() ).build() )
                .getDuplicatedContents()
                .first() );

        layerContext.runWith( () -> {
            final Content duplicatedTargetContent = contentService.getById( duplicatedContentId );
            final Content duplicatedTargetChild1 = contentService.getByPath( ContentPath.from( "/localContent-copy/localChild1" ) );
            final Content duplicatedTargetChild2 =
                contentService.getByPath( ContentPath.from( "/localContent-copy/localChild1/localChild2" ) );

            assertTrue( duplicatedTargetContent.getInherit().isEmpty() );
            assertEquals( "localContent-copy", duplicatedTargetContent.getName().toString() );

            assertEquals( "localChild1", duplicatedTargetChild1.getName().toString() );
            assertEquals( "localChild2", duplicatedTargetChild2.getName().toString() );
        } );
    }

    @Test
    public void testUpdated()
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );

        final Content updatedContent = projectContext.callWith(
            () -> contentService.update( new UpdateContentParams().contentId( sourceContent.getId() ).editor( ( edit -> {
                edit.data = new PropertyTree();
                edit.displayName = "newDisplayName";
                edit.extraDatas = ExtraDatas.create().add( createExtraData() ).build();
                edit.owner = PrincipalKey.from( "user:system:newOwner" );
                edit.language = Locale.forLanguageTag( "no" );
                edit.page = createPage();
            } ) ) ) );

        handleEvents();

        final Content targetContent = layerContext.callWith( () -> contentService.getById( sourceContent.getId() ) );

        compareSynched( updatedContent, targetContent );
        assertEquals( 4, targetContent.getInherit().size() );
    }

    @Test
    public void testUpdatedInSecondParent()
    {
        projectContext.callWith( () -> createContent( ContentPath.ROOT, "name1" ) );
        final Content sourceContent2 = secondProjectContext.callWith( () -> createContent( ContentPath.ROOT, "name2" ) );

        final Content updatedContent = secondProjectContext.callWith(
            () -> contentService.update( new UpdateContentParams().contentId( sourceContent2.getId() ).editor( ( edit -> {
                edit.data = new PropertyTree();
                edit.displayName = "newDisplayName";
                edit.extraDatas = ExtraDatas.create().add( createExtraData() ).build();
                edit.owner = PrincipalKey.from( "user:system:newOwner" );
                edit.language = Locale.forLanguageTag( "no" );
                edit.page = createPage();
            } ) ) ) );

        handleEvents();

        final Content targetContent = layerContext.callWith( () -> contentService.getById( sourceContent2.getId() ) );

        compareSynched( updatedContent, targetContent );
        assertEquals( 4, targetContent.getInherit().size() );
    }

    @Test
    public void testUpdatedFromReadyToInProgress()
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );

        projectContext.callWith( () -> {

            contentService.update( new UpdateContentParams().contentId( sourceContent.getId() )
                                       .editor(
                                           ( edit -> edit.workflowInfo = WorkflowInfo.create().state( WorkflowState.READY ).build() ) ) );

            handleEvents();

            final Content sourceContentReady = contentService.update( new UpdateContentParams().contentId( sourceContent.getId() )
                                                                          .editor( ( edit -> edit.workflowInfo = WorkflowInfo.create()
                                                                              .state( WorkflowState.IN_PROGRESS )
                                                                              .build() ) ) );

            handleEvents();

            return sourceContentReady;

        } );

        final Content targetContent = layerContext.callWith( () -> contentService.getById( sourceContent.getId() ) );

        assertEquals( WorkflowState.READY, targetContent.getWorkflowInfo().getState() );
    }

    @Test
    public void testUpdatedLocally()
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );

        handleEvents();

        final Content updatedInChild = layerContext.callWith( () -> contentService.update(
            new UpdateContentParams().contentId( sourceContent.getId() ).editor( ( edit -> edit.data = new PropertyTree() ) ) ) );

        assertEquals( 2, updatedInChild.getInherit().size() );
        assertFalse( updatedInChild.getInherit().contains( ContentInheritType.CONTENT ) );
        assertFalse( updatedInChild.getInherit().contains( ContentInheritType.NAME ) );

        final Content updatedInParent = projectContext.callWith( () -> contentService.update(
            new UpdateContentParams().contentId( sourceContent.getId() )
                .editor( ( edit -> edit.displayName = "new source display name" ) ) ) );

        handleEvents();

        assertNotEquals( updatedInParent.getDisplayName(),
                         layerContext.callWith( () -> contentService.getById( updatedInChild.getId() ).getDisplayName() ) );
    }

    @Test
    public void testMoved()
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "content" ) );
        final Content sourceChild = projectContext.callWith( () -> createContent( sourceContent.getPath(), "child" ) );
        final Content sourceFolder = projectContext.callWith( () -> createContent( ContentPath.ROOT, "folder" ) );

        handleEvents();

        projectContext.runWith( () -> contentService.move(
            MoveContentParams.create().contentId( sourceContent.getId() ).parentContentPath( sourceFolder.getPath() ).build() ) );

        handleEvents();

        final Content targetContent = layerContext.callWith( () -> contentService.getById( sourceContent.getId() ) );
        final Content targetChild = layerContext.callWith( () -> contentService.getById( sourceChild.getId() ) );

        assertEquals( "/folder/content", targetContent.getPath().toString() );
        assertEquals( "/folder/content/child", targetChild.getPath().toString() );
    }

    @Test
    public void testMovedToContentFromOtherProjectAndRemove()
    {
        final Content sourceContent1 = projectContext.callWith( () -> createContent( ContentPath.ROOT, "content1" ) );
        final Content sourceContent2 = secondProjectContext.callWith( () -> createContent( ContentPath.ROOT, "content2" ) );

        handleEvents();

        layerContext.runWith( () -> contentService.move(
            MoveContentParams.create().contentId( sourceContent2.getId() ).parentContentPath( sourceContent1.getPath() ).build() ) );

        handleEvents();

        final Content targetContent1 = layerContext.callWith( () -> contentService.getById( sourceContent1.getId() ) );
        final Content targetContent2 = layerContext.callWith( () -> contentService.getById( sourceContent2.getId() ) );

        assertEquals( "/content1", targetContent1.getPath().toString() );
        assertEquals( "/content1/content2", targetContent2.getPath().toString() );

        projectContext.runWith(
            () -> contentService.delete( DeleteContentParams.create().contentPath( sourceContent1.getPath() ).build() ) );

        handleEvents(); // not synced

        assertTrue( layerContext.callWith( () -> contentService.contentExists( sourceContent1.getId() ) ) );
        assertTrue( layerContext.callWith( () -> contentService.contentExists( sourceContent2.getId() ) ) );
    }

    @Test
    public void testMovedLocally()
    {
        final Content sourceContent1 = projectContext.callWith( () -> createContent( ContentPath.ROOT, "content1" ) );
        final Content sourceContent2 = projectContext.callWith( () -> createContent( ContentPath.ROOT, "content2" ) );
        final Content sourceContent3 = projectContext.callWith( () -> createContent( ContentPath.ROOT, "content3" ) );

        handleEvents();

        layerContext.runWith( () -> contentService.move(
            MoveContentParams.create().contentId( sourceContent1.getId() ).parentContentPath( sourceContent2.getPath() ).build() ) );

        final Content targetMovedContent = layerContext.callWith( () -> contentService.getById( sourceContent1.getId() ) );

        assertEquals( 3, targetMovedContent.getInherit().size() );
        assertFalse( targetMovedContent.getInherit().contains( ContentInheritType.PARENT ) );

        projectContext.runWith( () -> contentService.move(
            MoveContentParams.create().contentId( sourceContent1.getId() ).parentContentPath( sourceContent3.getPath() ).build() ) );

        assertEquals( "/content3/content1",
                      projectContext.callWith( () -> contentService.getById( sourceContent1.getId() ) ).getPath().toString() );
        assertEquals( "/content2/content1",
                      layerContext.callWith( () -> contentService.getById( sourceContent1.getId() ) ).getPath().toString() );
    }

    @Test
    public void testMovedToExistedPath()
    {
        final Content sourceContent1 = projectContext.callWith( () -> createContent( ContentPath.ROOT, "content" ) );
        final Content sourceContent2 = projectContext.callWith( () -> createContent( ContentPath.ROOT, "content2" ) );

        handleEvents();

        layerContext.callWith( () -> createContent( sourceContent2.getPath(), "content" ) );

        projectContext.runWith( () -> contentService.move(
            MoveContentParams.create().contentId( sourceContent1.getId() ).parentContentPath( sourceContent2.getPath() ).build() ) );

        handleEvents();

        final Content targetMovedContent = layerContext.callWith( () -> contentService.getById( sourceContent1.getId() ) );

        assertEquals( "/content2/content-1", targetMovedContent.getPath().toString() );
    }

    @Test
    public void testArchived()
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "content" ) );
        final Content sourceChild = projectContext.callWith( () -> createContent( sourceContent.getPath(), "child" ) );

        handleEvents();

        projectContext.runWith( () -> contentService.archive( ArchiveContentParams.create().contentId( sourceContent.getId() ).build() ) );

        handleEvents();

        final Content targetContent = layerArchiveContext.callWith( () -> contentService.getById( sourceContent.getId() ) );
        final Content targetChild = layerArchiveContext.callWith( () -> contentService.getById( sourceChild.getId() ) );

        assertEquals( "/content", targetContent.getPath().toString() );
        assertEquals( "/content/child", targetChild.getPath().toString() );
    }

    @Test
    public void testMovedArchivedAndRestored()
    {
        final Content sourceContent1 = projectContext.callWith( () -> createContent( ContentPath.ROOT, "content1" ) );
        final Content sourceContent2 = secondProjectContext.callWith( () -> createContent( ContentPath.ROOT, "content2" ) );

        handleEvents();

        layerContext.runWith( () -> contentService.move(
            MoveContentParams.create().contentId( sourceContent2.getId() ).parentContentPath( sourceContent1.getPath() ).build() ) );

        projectContext.runWith( () -> contentService.archive( ArchiveContentParams.create().contentId( sourceContent1.getId() ).build() ) );

        handleEvents();

        Content targetContent1 = layerArchiveContext.callWith( () -> contentService.getById( sourceContent1.getId() ) );
        Content targetContent2 = layerArchiveContext.callWith( () -> contentService.getById( sourceContent2.getId() ) );

        assertEquals( "/content1", targetContent1.getPath().toString() );
        assertEquals( "/content1/content2", targetContent2.getPath().toString() );

        projectContext.runWith( () -> contentService.restore( RestoreContentParams.create().contentId( sourceContent1.getId() ).build() ) );
        handleEvents();

        targetContent1 = layerContext.callWith( () -> contentService.getById( sourceContent1.getId() ) );
        targetContent2 = layerContext.callWith( () -> contentService.getById( sourceContent2.getId() ) );

        assertEquals( "/content1", targetContent1.getPath().toString() );
        assertEquals( "/content1/content2", targetContent2.getPath().toString() );
    }

    @Test
    public void testArchivedNotInherited()
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "content" ) );

        handleEvents();

        layerContext.runWith( () -> contentService.update(
            new UpdateContentParams().contentId( sourceContent.getId() ).editor( edit -> edit.data = new PropertyTree() ) ) );

        handleEvents();

        projectContext.runWith( () -> contentService.archive( ArchiveContentParams.create().contentId( sourceContent.getId() ).build() ) );

        handleEvents();

        final Content targetContent = layerContext.callWith( () -> contentService.getById( sourceContent.getId() ) );

        assertEquals( "/content", targetContent.getPath().toString() );
    }

    @Test
    public void testArchivedAndRestoreAlreadyArchived()
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "content" ) );

        handleEvents();

        layerContext.runWith( () -> contentService.archive( ArchiveContentParams.create().contentId( sourceContent.getId() ).build() ) );

        handleEvents();

        projectContext.runWith( () -> contentService.archive( ArchiveContentParams.create().contentId( sourceContent.getId() ).build() ) );

        handleEvents();

        projectContext.runWith( () -> contentService.restore( RestoreContentParams.create().contentId( sourceContent.getId() ).build() ) );

        handleEvents();

        final Content targetContent = layerArchiveContext.callWith( () -> contentService.getById( sourceContent.getId() ) );

        assertEquals( "/content", targetContent.getPath().toString() );
    }

    @Test
    public void testArchivePublishedInLayer()
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "content" ) );

        handleEvents();

        layerContext.runWith(
            () -> contentService.publish( PushContentParams.create().contentIds( ContentIds.from( sourceContent.getId() ) ).build() ) );

        handleEvents();

        projectContext.runWith( () -> contentService.archive( ArchiveContentParams.create().contentId( sourceContent.getId() ).build() ) );

        handleEvents();

        projectContext.runWith( () -> contentService.restore( RestoreContentParams.create().contentId( sourceContent.getId() ).build() ) );

        handleEvents();

        final Content targetContent = layerContext.callWith( () -> contentService.getById( sourceContent.getId() ) );
        final ContentPublishInfo publishInfo = targetContent.getPublishInfo();

        assertNotNull( publishInfo.getFirst() );
        assertNull( publishInfo.getFrom() );
        assertNull( publishInfo.getTo() );
    }

    @Test
    public void testArchiveAndCreateChild()
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "content" ) );

        handleEvents();

        layerContext.runWith( () -> contentService.archive( ArchiveContentParams.create().contentId( sourceContent.getId() ).build() ) );

        handleEvents();

        final Content sourceChild = projectContext.callWith( () -> createContent( sourceContent.getPath(), "child" ) );

        handleEvents();

        assertEquals( "/content/child",
                      layerArchiveContext.callWith( () -> contentService.getById( sourceChild.getId() ) ).getPath().toString() );

        layerContext.runWith( () -> contentService.restore( RestoreContentParams.create().contentId( sourceContent.getId() ).build() ) );

        handleEvents();

        assertEquals( "/content/child", layerContext.callWith( () -> contentService.getById( sourceChild.getId() ) ).getPath().toString() );
    }

    @Test
    public void testRestored()
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "content" ) );
        final Content sourceChild = projectContext.callWith( () -> createContent( sourceContent.getPath(), "child" ) );

        handleEvents();

        projectContext.runWith( () -> contentService.archive( ArchiveContentParams.create().contentId( sourceContent.getId() ).build() ) );

        handleEvents();

        final Content targetChild = layerArchiveContext.callWith( () -> contentService.getById( sourceChild.getId() ) );

        projectContext.runWith( () -> contentService.restore( RestoreContentParams.create().contentId( targetChild.getId() ).build() ) );

        handleEvents();

        final Content targetRestoredChild = layerContext.callWith( () -> contentService.getById( sourceChild.getId() ) );

        projectContext.runWith( () -> contentService.restore(
            RestoreContentParams.create().contentId( sourceContent.getId() ).path( targetRestoredChild.getPath() ).build() ) );

        handleEvents();

        final Content targetRestoredContent = layerContext.callWith( () -> contentService.getById( sourceContent.getId() ) );

        assertEquals( "/child", targetRestoredChild.getPath().toString() );
        assertEquals( "/child/content", targetRestoredContent.getPath().toString() );
    }

    @Test
    public void testSorted()
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "content" ) );
        final Content sourceChild1 = projectContext.callWith( () -> createContent( sourceContent.getPath(), "child1" ) );
        final Content sourceChild2 = projectContext.callWith( () -> createContent( sourceContent.getPath(), "child2" ) );
        final Content sourceChild3 = projectContext.callWith( () -> createContent( sourceContent.getPath(), "child3" ) );

        handleEvents();

        projectContext.runWith( () -> contentService.sort( SortContentParams.create()
                                                                        .contentId( sourceContent.getId() )
                                                                        .childOrder( ChildOrder.from( "_name DESC" ) )
                                                                        .build() ) );

        handleEvents();

        layerContext.runWith( () -> {
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
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "content" ) );

        handleEvents();

        final SortContentResult sortedInChild = layerContext.callWith( () -> contentService.sort( SortContentParams.create()
                                                                                                     .contentId( sourceContent.getId() )
                                                                                                     .childOrder(
                                                                                                         ChildOrder.from( "_name DESC" ) )
                                                                                                     .build() ) );

        assertEquals( 3, sortedInChild.getContent().getInherit().size() );
        assertFalse( sortedInChild.getContent().getInherit().contains( ContentInheritType.SORT ) );

        final SortContentResult  sortedInParent = projectContext.callWith( () -> contentService.sort(
            SortContentParams.create().contentId( sourceContent.getId() ).childOrder( ChildOrder.from( "_name ASC" ) ).build() ) );

        handleEvents();

        assertNotEquals( sortedInParent.getContent().getChildOrder(), layerContext.callWith( () -> contentService.getById( sortedInChild.getContent().getId() ) ).getChildOrder() );

    }

    @Test
    public void testManualOrderUpdated()
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "content" ) );
        final Content sourceChild1 = projectContext.callWith( () -> createContent( sourceContent.getPath(), "child1" ) );
        final Content sourceChild2 = projectContext.callWith( () -> createContent( sourceContent.getPath(), "child2" ) );
        final Content sourceChild3 = projectContext.callWith( () -> createContent( sourceContent.getPath(), "child3" ) );

        handleEvents();

        projectContext.runWith( () -> contentService.sort( SortContentParams.create()
                                                                        .contentId( sourceContent.getId() )
                                                                        .childOrder( ChildOrder.from( "_name DESC" ) )
                                                                        .build() ) );

        handleEvents();

        layerContext.runWith( () -> {
            final FindContentByParentResult result =
                contentService.findByParent( FindContentByParentParams.create().parentId( sourceContent.getId() ).build() );

            assertThat( result.getContents() ).map( Content::getName )
                .containsExactly( sourceChild3.getName(), sourceChild2.getName(), sourceChild1.getName() );
        } );

        projectContext.runWith( () -> contentService.sort( SortContentParams.create()
                                                               .contentId( sourceContent.getId() )
                                                               .childOrder( ChildOrder.manualOrder() )
                                                               .addManualOrder( ReorderChildContentParams.create()
                                                                                    .contentToMove( sourceChild2.getId() )
                                                                                    .contentToMoveBefore( sourceChild3.getId() )
                                                                                    .build() )
                                                               .addManualOrder( ReorderChildContentParams.create()
                                                                                    .contentToMove( sourceChild1.getId() )
                                                                                    .contentToMoveBefore( sourceChild3.getId() )
                                                                                    .build() )
                                                               .build() ) );

        handleEvents();

        layerContext.runWith( () -> {
            final FindContentByParentResult result =
                contentService.findByParent( FindContentByParentParams.create().parentId( sourceContent.getId() ).build() );

            assertThat( result.getContents() ).map( Content::getName ).containsExactly( sourceChild2.getName(), sourceChild1.getName(), sourceChild3.getName() );
        } );
    }

    @Test
    public void testManualOrderLocally()
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "content" ) );
        final Content sourceChild1 = projectContext.callWith( () -> createContent( sourceContent.getPath(), "child1" ) );
        final Content sourceChild2 = projectContext.callWith( () -> createContent( sourceContent.getPath(), "child2" ) );
        final Content sourceChild3 = projectContext.callWith( () -> createContent( sourceContent.getPath(), "child3" ) );

        handleEvents();

        projectContext.runWith( () -> contentService.sort( SortContentParams.create()
                                                                        .contentId( sourceContent.getId() )
                                                                        .childOrder( ChildOrder.from( "_name DESC" ) )
                                                                        .build() ) );

        handleEvents();

        layerContext.runWith( () -> contentService.sort( SortContentParams.create()
                                                             .contentId( sourceContent.getId() )
                                                             .childOrder( ChildOrder.manualOrder() )
                                                             .addManualOrder( ReorderChildContentParams.create()
                                                                                  .contentToMove( sourceChild2.getId() )
                                                                                  .contentToMoveBefore( sourceChild3.getId() )
                                                                                  .build() )
                                                             .addManualOrder( ReorderChildContentParams.create()
                                                                                  .contentToMove( sourceChild1.getId() )
                                                                                  .contentToMoveBefore( sourceChild3.getId() )
                                                                                  .build() )
                                                             .build() ) );

        handleEvents();

        projectContext.runWith( () -> contentService.sort( SortContentParams.create()
                                                                        .contentId( sourceContent.getId() )
                                                                        .childOrder( ChildOrder.from( "_name DESC" ) )
                                                                        .build() ) );

        handleEvents();

        layerContext.runWith( () -> {
            final FindContentByParentResult result =
                contentService.findByParent( FindContentByParentParams.create().parentId( sourceContent.getId() ).build() );

            final Iterator<Content> iterator = result.getContents().iterator();

            assertEquals( sourceChild2.getId(), iterator.next().getId() );
            assertEquals( sourceChild1.getId(), iterator.next().getId() );
            assertEquals( sourceChild3.getId(), iterator.next().getId() );

            assertTrue( contentService.getById( sourceContent.getId() ).getChildOrder().isManualOrder() );
        } );
    }

    @Test
    public void testRenamed()
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "content" ) );
        final Content sourceChild1 = projectContext.callWith( () -> createContent( sourceContent.getPath(), "child1" ) );
        final Content sourceChild2 = projectContext.callWith( () -> createContent( sourceChild1.getPath(), "child2" ) );

        handleEvents();

        projectContext.runWith( () -> contentService.rename(
            RenameContentParams.create().contentId( sourceContent.getId() ).newName( ContentName.from( "content-new" ) ).build() ) );

        handleEvents();

        layerContext.runWith( () -> {
            assertEquals( "/content-new", contentService.getById( sourceContent.getId() ).getPath().toString() );
            assertEquals( "/content-new/child1", contentService.getById( sourceChild1.getId() ).getPath().toString() );
            assertEquals( "/content-new/child1/child2", contentService.getById( sourceChild2.getId() ).getPath().toString() );
        } );

        projectContext.runWith( () -> contentService.rename(
            RenameContentParams.create().contentId( sourceChild1.getId() ).newName( ContentName.from( "child1-new" ) ).build() ) );

        handleEvents();

        layerContext.runWith( () -> {
            assertEquals( "/content-new", contentService.getById( sourceContent.getId() ).getPath().toString() );
            assertEquals( "/content-new/child1-new", contentService.getById( sourceChild1.getId() ).getPath().toString() );
            assertEquals( "/content-new/child1-new/child2", contentService.getById( sourceChild2.getId() ).getPath().toString() );
        } );

        projectContext.runWith( () -> contentService.rename(
            RenameContentParams.create().contentId( sourceChild2.getId() ).newName( ContentName.from( "child2-new" ) ).build() ) );

        handleEvents();

        layerContext.runWith( () -> {
            assertEquals( "/content-new", contentService.getById( sourceContent.getId() ).getPath().toString() );
            assertEquals( "/content-new/child1-new", contentService.getById( sourceChild1.getId() ).getPath().toString() );
            assertEquals( "/content-new/child1-new/child2-new", contentService.getById( sourceChild2.getId() ).getPath().toString() );
        } );
    }

    @Test
    public void testRenameToExisted()
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "content" ) );

        handleEvents();

        layerContext.runWith( () -> {
            createContent( ContentPath.ROOT, "newName" );
        } );

        projectContext.runWith( () -> {
            contentService.rename(
                RenameContentParams.create().contentId( sourceContent.getId() ).newName( ContentName.from( "newName" ) ).build() );
        } );
        handleEvents();

        assertEquals( "newName-1", layerContext.callWith( () -> contentService.getById( sourceContent.getId() ) ).getName().toString() );
    }

    @Test
    public void testDeleted()
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "content" ) );
        final Content sourceChild1 = projectContext.callWith( () -> createContent( sourceContent.getPath(), "child1" ) );
        final Content sourceChild2 = projectContext.callWith( () -> createContent( sourceChild1.getPath(), "child2" ) );

        handleEvents();

        projectContext.runWith(
            () -> contentService.delete( DeleteContentParams.create().contentPath( sourceContent.getPath() ).build() ) );

        handleEvents();

        layerContext.runWith( () -> {
            assertFalse( contentService.contentExists( sourceContent.getId() ) );
            assertFalse( contentService.contentExists( sourceChild1.getId() ) );
            assertFalse( contentService.contentExists( sourceChild2.getId() ) );
        } );
    }

    @Test
    public void testDeletedInherited()
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "content" ) );
        final Content sourceChild1 = projectContext.callWith( () -> createContent( sourceContent.getPath(), "child1" ) );
        final Content sourceChild2 = projectContext.callWith( () -> createContent( sourceChild1.getPath(), "child2" ) );

        handleEvents();

        layerContext.runWith(
            () -> contentService.delete( DeleteContentParams.create().contentPath( sourceContent.getPath() ).build() ) );

        handleEvents();

        layerContext.runWith( () -> {
            assertTrue( contentService.contentExists( sourceContent.getId() ) );
            assertTrue( contentService.contentExists( sourceChild1.getId() ) );
            assertTrue( contentService.contentExists( sourceChild2.getId() ) );
        } );
    }

    @Test
    public void testDeletedAndRestoredFromTheCorrectParent()
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "content" ) );

        handleEvents();

        childLayerContext.runWith( () -> {
            contentService.rename(
                RenameContentParams.create().contentId( sourceContent.getId() ).newName( ContentName.from( "newName1" ) ).build() );
        } );

        secondChildLayerContext.runWith( () -> {
            contentService.rename(
                RenameContentParams.create().contentId( sourceContent.getId() ).newName( ContentName.from( "newName2" ) ).build() );
        } );

        mixedChildLayerContext.runWith( () -> {
            syncContentService.syncProject( ProjectSyncParams.create().targetProject( mixedChildLayer.getName() ).build() );
            assertEquals( "newName1", contentService.getById( sourceContent.getId() ).getName().toString() );
        } );

        handleEvents();

        mixedChildLayerContext.runWith( () -> {
            contentService.delete( DeleteContentParams.create().contentPath( ContentPath.from( "/newName1" ) ).build() );
        } );

        handleEvents();

        mixedChildLayerContext.runWith( () -> {
            syncContentService.syncProject( ProjectSyncParams.create().targetProject( mixedChildLayer.getName() ).build() );
            assertEquals( "newName1", contentService.getById( sourceContent.getId() ).getName().toString() );
        } );
    }

    @Test
    public void repoIsNotProject()
    {
        eventPublisher.publish( Event.create( "node.created" )
                                    .value( "nodes", List.of( ImmutableMap.builder()
                                                                  .put( "id", "123" )
                                                                  .put( "path", "/content/something" )
                                                                  .put( "branch", "draft" )
                                                                  .put( "repo", "not-a-project" )
                                                                  .build() ) )
                                    .build() );

        assertDoesNotThrow( this::handleEvents );
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

        PartComponent component =
            PartComponent.create().descriptor( DescriptorKey.from( "mainapplication:partTemplateName" ) ).config( componentConfig ).build();

        Region region = Region.create().name( "my-region" ).add( component ).build();

        PageRegions regions = PageRegions.create().add( region ).build();

        PropertyTree pageConfig = new PropertyTree();
        pageConfig.setString( "background-color", "blue" );

        Mockito.when( partDescriptorService.getByKey( DescriptorKey.from( "mainapplication:partTemplateName" ) ) )
            .thenReturn( PartDescriptor.create()
                             .key( DescriptorKey.from( "mainapplication:partTemplateName" ) )
                             .displayName( "my-component" )
                             .config( Form.empty() )
                             .build() );

        return Page.create().template( PageTemplateKey.from( "mypagetemplate" ) ).regions( regions ).build();
    }
}
