package com.enonic.xp.core.content;

import java.time.Instant;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.RejectedExecutionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.archive.ArchiveContentParams;
import com.enonic.xp.archive.RestoreContentParams;
import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
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
import com.enonic.xp.content.PatchContentParams;
import com.enonic.xp.content.PatchContentResult;
import com.enonic.xp.content.ProjectSyncParams;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.content.RenameContentParams;
import com.enonic.xp.content.ReorderChildContentsParams;
import com.enonic.xp.content.ReorderChildParams;
import com.enonic.xp.content.SetContentChildOrderParams;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.ValidationError;
import com.enonic.xp.content.ValidationErrorCode;
import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.content.WorkflowInfo;
import com.enonic.xp.content.WorkflowState;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.impl.content.ParentContentSynchronizer;
import com.enonic.xp.core.impl.content.ProjectContentEventListener;
import com.enonic.xp.core.impl.content.SyncContentServiceImpl;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventConstants;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.page.PageRegions;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.region.RegionDescriptors;
import com.enonic.xp.repo.impl.NodeEvents;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.security.PrincipalKey;

import static com.enonic.xp.media.MediaInfo.IMAGE_INFO_IMAGE_HEIGHT;
import static com.enonic.xp.media.MediaInfo.IMAGE_INFO_IMAGE_WIDTH;
import static com.enonic.xp.media.MediaInfo.IMAGE_INFO_PIXEL_SIZE;
import static com.enonic.xp.media.MediaInfo.MEDIA_INFO_BYTE_SIZE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProjectContentEventListenerTest
    extends AbstractContentSynchronizerTest
{
    private ProjectContentEventListener listener;

    private ArgumentCaptor<Event> eventCaptor;

    private Set<Event> handledEvents;


    private SyncContentServiceImpl syncContentService;


    @BeforeEach
    void setUp()
    {
        final ParentContentSynchronizer synchronizer = new ParentContentSynchronizer( contentService );
        listener = new ProjectContentEventListener( this.projectService, synchronizer );

        eventCaptor = ArgumentCaptor.forClass( Event.class );
        handledEvents = new HashSet<>();

        syncContentService =
            new SyncContentServiceImpl( contentTypeService, nodeService, eventPublisher, projectService, contentService, synchronizer );
    }

    @Test
    public void testCreatedDiffParentsSameName()
        throws InterruptedException
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
        throws InterruptedException
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
        throws InterruptedException
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
        throws InterruptedException
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
        throws InterruptedException
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
        throws InterruptedException
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
    public void syncPatchedFields()
        throws InterruptedException
    {
        final Content parentContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "parent" ) );
        final Content sourceContent = projectContext.callWith( () -> createContent( parentContent.getPath(), "name" ) );

        projectContext.callWith( () -> pushNodes( ContentConstants.BRANCH_MASTER, NodeId.from( sourceContent.getId() ) ) );

        handleEvents();

        final PatchContentResult patchedContentResult = projectContext.callWith( () -> {

            final PatchContentResult result =
                contentService.patch( PatchContentParams.create().contentId( sourceContent.getId() ).patcher( ( edit -> {
                    edit.data.setValue( new PropertyTree() );
                    edit.displayName.setValue( "newDisplayName" );
                    edit.extraDatas.setValue( ExtraDatas.create().add( createExtraData() ).build() );
                    edit.owner.setValue( PrincipalKey.from( "user:system:newOwner" ) );
                    edit.language.setValue( Locale.forLanguageTag( "no" ) );
                    edit.page.setValue( createPage() );

                    edit.valid.setValue( false );
                    edit.validationErrors.setValue( ValidationErrors.create()
                                                        .add( ValidationError.dataError(
                                                            ValidationErrorCode.from( ApplicationKey.SYSTEM, "errorCode" ),
                                                            PropertyPath.from( "/property" ) ).build() )
                                                        .build() );

                    edit.modifiedTime.setValue( Instant.parse( "2023-10-01T12:00:00Z" ) );
                    edit.modifier.setValue( PrincipalKey.from( "user:system:modifier1" ) );

                    edit.childOrder.setValue( ChildOrder.from( "modifiedtime ASC" ) );

                    edit.originProject.setValue( ProjectName.from( "new-origin-project" ) );

                    edit.originalParentPath.setValue( ContentPath.from( "/newOriginalParent" ) );

                    edit.originalName.setValue( ContentName.from( "newOriginalName" ) );

                    edit.archivedTime.setValue( Instant.parse( "2023-10-02T12:00:00Z" ) );

                    edit.archivedBy.setValue( PrincipalKey.from( "user:system:archivedBy" ) );
                } ) ).build() );

            return result;

        } );

        final Content patchedContent = patchedContentResult.getResult( ContentConstants.BRANCH_DRAFT );

        handleEvents();

        final Content targetContent = layerContext.callWith( () -> contentService.getById( sourceContent.getId() ) );

        compareSynched( patchedContent, targetContent );

        //fields to not sync
        assertNotEquals( patchedContent.getChildOrder(), targetContent.getChildOrder() );
        assertNotEquals( patchedContent.getOriginProject(), targetContent.getOriginProject() );
        assertNotEquals( patchedContent.getOriginalParentPath(), targetContent.getOriginalParentPath() );
        assertNotEquals( patchedContent.getOriginalName(), targetContent.getOriginalName() );
    }

    @Test
    public void syncPatchedSkipSync()
        throws InterruptedException
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "parent" ) );

        handleEvents();

        final PatchContentResult patchedContentResult = ContextBuilder.copyOf( projectContext )
            .attribute( "eventMetadata", Map.of( "content.skipSync", "true" ) )
            .build()
            .callWith( () -> contentService.patch( PatchContentParams.create().contentId( sourceContent.getId() ).patcher( ( edit -> {
                final PropertyTree data = new PropertyTree();
                data.addString( "test", "value" );
                edit.data.setValue( data );
            } ) ).build() ) );

        handleEvents();

        final Content targetContent = layerContext.callWith( () -> contentService.getById( sourceContent.getId() ) );

        assertNotEquals( targetContent.getData(), patchedContentResult.getResult( ContentConstants.BRANCH_DRAFT ).getData() );
    }

    @Test
    public void syncPatchedCreateAttachments()
        throws InterruptedException
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );
        projectContext.callWith( () -> pushNodes( ContentConstants.BRANCH_MASTER, NodeId.from( sourceContent.getId() ) ) );

        projectContext.callWith( () -> {
            return contentService.patch( PatchContentParams.create()
                                             .branches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) )
                                             .createAttachments( CreateAttachments.create()
                                                                     .add( CreateAttachment.create().mimeType( "image/gif" )
                                                                               .byteSource( ByteSource.wrap( "data1".getBytes() ) )
                                                                               .name( "MyImage1.gif" )
                                                                               .build() )
                                                                     .add( CreateAttachment.create().mimeType( "image/gif" )
                                                                               .byteSource( ByteSource.wrap( "data2".getBytes() ) )
                                                                               .name( "MyImage2.gif" )
                                                                               .build() )
                                                                     .build() )
                                             .contentId( sourceContent.getId() )
                                             .patcher( ( edit -> {

                                                 final Attachment a1 = Attachment.create()
                                                     .mimeType( "image/gif" )
                                                     .label( "My Image 1" )
                                                     .name( "MyImage1.gif" )
                                                     .build();
                                                 final Attachment a2 = Attachment.create()
                                                     .mimeType( "image/gif" )
                                                     .label( "My Image 2" )
                                                     .name( "MyImage2.gif" )
                                                     .build();

                                                 edit.attachments.setValue( Attachments.create().add( a1 ).add( a2 ).build() );
                                             } ) )
                                             .build() );

        } );

        handleEvents();

        final Content targetContent = layerContext.callWith( () -> contentService.getById( sourceContent.getId() ) );
        final Content contentInMaster = ContextBuilder.from( projectContext )
            .branch( ContentConstants.BRANCH_MASTER )
            .build()
            .callWith( () -> contentService.getById( sourceContent.getId() ) );

        assertTrue( targetContent.getAttachments().hasByName( "MyImage1.gif" ) );
        assertTrue( targetContent.getAttachments().hasByName( "MyImage1.gif" ) );
        assertTrue( contentInMaster.getAttachments().hasByName( "MyImage2.gif" ) );
        assertTrue( contentInMaster.getAttachments().hasByName( "MyImage2.gif" ) );
    }

    @Test
    public void syncPatchedRemoveAttachments()
        throws InterruptedException
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );
        projectContext.callWith( () -> pushNodes( ContentConstants.BRANCH_MASTER, NodeId.from( sourceContent.getId() ) ) );

        projectContext.callWith( () -> {
            return contentService.patch( PatchContentParams.create()
                                             .branches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) )
                                             .createAttachments( CreateAttachments.create()
                                                                     .add( CreateAttachment.create().mimeType( "image/gif" )
                                                                               .byteSource( ByteSource.wrap( "data1".getBytes() ) )
                                                                               .name( "MyImage1.gif" )
                                                                               .build() )
                                                                     .add( CreateAttachment.create().mimeType( "image/gif" )
                                                                               .byteSource( ByteSource.wrap( "data2".getBytes() ) )
                                                                               .name( "MyImage2.gif" )
                                                                               .build() )
                                                                     .build() )
                                             .contentId( sourceContent.getId() )
                                             .patcher( ( edit -> {

                                                 final Attachment a1 = Attachment.create()
                                                     .mimeType( "image/gif" )
                                                     .label( "My Image 1" )
                                                     .name( "MyImage1.gif" )
                                                     .build();
                                                 final Attachment a2 = Attachment.create()
                                                     .mimeType( "image/gif" )
                                                     .label( "My Image 2" )
                                                     .name( "MyImage2.gif" )
                                                     .build();

                                                 edit.attachments.setValue( Attachments.create().add( a1 ).add( a2 ).build() );
                                             } ) )
                                             .build() );

        } );

        handleEvents();

        Content targetContent = layerContext.callWith( () -> contentService.getById( sourceContent.getId() ) );
        Content contentInMaster = ContextBuilder.from( projectContext )
            .branch( ContentConstants.BRANCH_MASTER )
            .build()
            .callWith( () -> contentService.getById( sourceContent.getId() ) );

        assertTrue( targetContent.getAttachments().hasByName( "MyImage1.gif" ) );
        assertTrue( contentInMaster.getAttachments().hasByName( "MyImage1.gif" ) );
        assertTrue( targetContent.getAttachments().hasByName( "MyImage2.gif" ) );
        assertTrue( contentInMaster.getAttachments().hasByName( "MyImage2.gif" ) );

        //remove attachment
        projectContext.callWith( () -> {
            return contentService.patch( PatchContentParams.create()
                                             .branches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) )
                                             .contentId( sourceContent.getId() )
                                             .createAttachments( CreateAttachments.create()
                                                                     .add( CreateAttachment.create().mimeType( "image/gif" )
                                                                               .byteSource( ByteSource.wrap( "new-data".getBytes() ) )
                                                                               .name( "MyImage3.gif" )
                                                                               .build() )
                                                                     .build() )
                                             .patcher( ( edit -> {
                                                 final Attachment a2 = Attachment.create().mimeType( "image/gif" )
                                                     .label( "My Image 2" )
                                                     .name( "MyImage2.gif" )
                                                     .build();

                                                 final Attachment a3 = Attachment.create().mimeType( "image/gif" )
                                                     .label( "My Image 3" )
                                                     .name( "MyImage3.gif" )
                                                     .build();

                                                 edit.attachments.setValue( Attachments.create().add( a2 ).add( a3 ).build() );
                                             } ) )
                                             .build() );

        } );

        handleEvents();

        targetContent = layerContext.callWith( () -> contentService.getById( sourceContent.getId() ) );
        contentInMaster = ContextBuilder.from( projectContext )
            .branch( ContentConstants.BRANCH_MASTER )
            .build()
            .callWith( () -> contentService.getById( sourceContent.getId() ) );

        assertFalse( targetContent.getAttachments().hasByName( "MyImage1.gif" ) );
        assertFalse( contentInMaster.getAttachments().hasByName( "MyImage1.gif" ) );
        assertTrue( targetContent.getAttachments().hasByName( "MyImage2.gif" ) );
        assertTrue( contentInMaster.getAttachments().hasByName( "MyImage2.gif" ) );
        assertTrue( targetContent.getAttachments().hasByName( "MyImage3.gif" ) );
        assertTrue( contentInMaster.getAttachments().hasByName( "MyImage3.gif" ) );
    }

    @Test
    public void testUpdated()
        throws InterruptedException
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );

        final Content updatedContent = projectContext.callWith( () -> {

            final Content updated = contentService.update( new UpdateContentParams().contentId( sourceContent.getId() ).editor( ( edit -> {
                edit.data = new PropertyTree();
                edit.displayName = "newDisplayName";
                edit.extraDatas = ExtraDatas.create().add( createExtraData() ).build();
                edit.owner = PrincipalKey.from( "user:system:newOwner" );
                edit.language = Locale.forLanguageTag( "no" );
                edit.page = createPage();

            } ) ) );

            return updated;

        } );

        handleEvents();

        final Content targetContent = layerContext.callWith( () -> contentService.getById( sourceContent.getId() ) );

        compareSynched( updatedContent, targetContent );
        assertEquals( 4, targetContent.getInherit().size() );
    }

    @Test
    public void testUpdatedInSecondParent()
        throws InterruptedException
    {
        projectContext.callWith( () -> createContent( ContentPath.ROOT, "name1" ) );
        final Content sourceContent2 = secondProjectContext.callWith( () -> createContent( ContentPath.ROOT, "name2" ) );

        final Content updatedContent = secondProjectContext.callWith( () -> {

            final Content updated = contentService.update( new UpdateContentParams().contentId( sourceContent2.getId() ).editor( ( edit -> {
                edit.data = new PropertyTree();
                edit.displayName = "newDisplayName";
                edit.extraDatas = ExtraDatas.create().add( createExtraData() ).build();
                edit.owner = PrincipalKey.from( "user:system:newOwner" );
                edit.language = Locale.forLanguageTag( "no" );
                edit.page = createPage();

            } ) ) );

            return updated;

        } );

        handleEvents();

        final Content targetContent = layerContext.callWith( () -> contentService.getById( sourceContent2.getId() ) );

        compareSynched( updatedContent, targetContent );
        assertEquals( 4, targetContent.getInherit().size() );
    }


    @Test
    public void testUpdatedFromReadyToInProgress()
        throws InterruptedException
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
        throws InterruptedException
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
        throws InterruptedException
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
        throws InterruptedException
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
        throws InterruptedException
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
        throws InterruptedException
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
        throws InterruptedException
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
        throws InterruptedException
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
        throws InterruptedException
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
        throws InterruptedException
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
        throws InterruptedException
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
        throws InterruptedException
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
        throws InterruptedException
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
        throws InterruptedException
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "content" ) );
        final Content sourceChild1 = projectContext.callWith( () -> createContent( sourceContent.getPath(), "child1" ) );
        final Content sourceChild2 = projectContext.callWith( () -> createContent( sourceContent.getPath(), "child2" ) );
        final Content sourceChild3 = projectContext.callWith( () -> createContent( sourceContent.getPath(), "child3" ) );

        handleEvents();

        projectContext.runWith( () -> contentService.setChildOrder( SetContentChildOrderParams.create()
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
        throws InterruptedException
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "content" ) );

        handleEvents();

        final Content sortedInChild = layerContext.callWith( () -> contentService.setChildOrder( SetContentChildOrderParams.create()
                                                                                                     .contentId( sourceContent.getId() )
                                                                                                     .childOrder(
                                                                                                         ChildOrder.from( "_name DESC" ) )
                                                                                                     .build() ) );

        assertEquals( 3, sortedInChild.getInherit().size() );
        assertFalse( sortedInChild.getInherit().contains( ContentInheritType.SORT ) );

        final Content sortedInParent = projectContext.callWith( () -> contentService.setChildOrder(
            SetContentChildOrderParams.create().contentId( sourceContent.getId() ).childOrder( ChildOrder.from( "_name ASC" ) ).build() ) );

        handleEvents();

        assertNotEquals( sortedInParent.getChildOrder(), layerContext.callWith( () -> contentService.getById( sortedInChild.getId() ) ).getChildOrder() );

    }

    @Test
    public void testManualOrderUpdated()
        throws InterruptedException
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "content" ) );
        final Content sourceChild1 = projectContext.callWith( () -> createContent( sourceContent.getPath(), "child1" ) );
        final Content sourceChild2 = projectContext.callWith( () -> createContent( sourceContent.getPath(), "child2" ) );
        final Content sourceChild3 = projectContext.callWith( () -> createContent( sourceContent.getPath(), "child3" ) );

        handleEvents();

        projectContext.runWith( () -> contentService.setChildOrder( SetContentChildOrderParams.create()
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

        projectContext.runWith( () -> contentService.setChildOrder(
            SetContentChildOrderParams.create().contentId( sourceContent.getId() ).childOrder( ChildOrder.manualOrder() ).build() ) );

        handleEvents();

        projectContext.runWith( () -> contentService.reorderChildren( ReorderChildContentsParams.create()
                                                                          .contentId( sourceContent.getId() )
                                                                          .add( ReorderChildParams.create()
                                                                                    .contentToMove( sourceChild2.getId() )
                                                                                    .contentToMoveBefore( sourceChild3.getId() )
                                                                                    .build() )
                                                                          .add( ReorderChildParams.create()
                                                                                    .contentToMove( sourceChild1.getId() )
                                                                                    .contentToMoveBefore( sourceChild3.getId() )
                                                                                    .build() )
                                                                          .build() ) );

        handleEvents();

        layerContext.runWith( () -> {
            final FindContentByParentResult result =
                contentService.findByParent( FindContentByParentParams.create().parentId( sourceContent.getId() ).build() );

            final Iterator<Content> iterator = result.getContents().iterator();

            assertEquals( sourceChild2.getId(), iterator.next().getId() );
            assertEquals( sourceChild1.getId(), iterator.next().getId() );
            assertEquals( sourceChild3.getId(), iterator.next().getId() );
        } );

    }

    @Test
    public void testManualOrderLocally()
        throws InterruptedException
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "content" ) );
        final Content sourceChild1 = projectContext.callWith( () -> createContent( sourceContent.getPath(), "child1" ) );
        final Content sourceChild2 = projectContext.callWith( () -> createContent( sourceContent.getPath(), "child2" ) );
        final Content sourceChild3 = projectContext.callWith( () -> createContent( sourceContent.getPath(), "child3" ) );

        handleEvents();

        projectContext.runWith( () -> contentService.setChildOrder( SetContentChildOrderParams.create()
                                                                        .contentId( sourceContent.getId() )
                                                                        .childOrder( ChildOrder.from( "_name DESC" ) )
                                                                        .build() ) );

        handleEvents();

        layerContext.runWith( () -> contentService.setChildOrder(
            SetContentChildOrderParams.create().contentId( sourceContent.getId() ).childOrder( ChildOrder.manualOrder() ).build() ) );

        layerContext.runWith( () -> contentService.reorderChildren( ReorderChildContentsParams.create()
                                                                        .contentId( sourceContent.getId() )
                                                                        .add( ReorderChildParams.create()
                                                                                  .contentToMove( sourceChild2.getId() )
                                                                                  .contentToMoveBefore( sourceChild3.getId() )
                                                                                  .build() )
                                                                        .add( ReorderChildParams.create()
                                                                                  .contentToMove( sourceChild1.getId() )
                                                                                  .contentToMoveBefore( sourceChild3.getId() )
                                                                                  .build() )
                                                                        .build() ) );

        projectContext.runWith( () -> contentService.setChildOrder( SetContentChildOrderParams.create()
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
        throws InterruptedException
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
        throws InterruptedException
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
        throws InterruptedException
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
        throws InterruptedException
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "content" ) );
        final Content sourceChild1 = projectContext.callWith( () -> createContent( sourceContent.getPath(), "child1" ) );
        final Content sourceChild2 = projectContext.callWith( () -> createContent( sourceChild1.getPath(), "child2" ) );

        handleEvents();

        layerContext.runWith( () -> contentService.delete( DeleteContentParams.create().contentPath( sourceContent.getPath() ).build() ) );

        handleEvents();

        layerContext.runWith( () -> {
            assertTrue( contentService.contentExists( sourceContent.getId() ) );
            assertTrue( contentService.contentExists( sourceChild1.getId() ) );
            assertTrue( contentService.contentExists( sourceChild2.getId() ) );
        } );
    }

    @Test
    public void testDeletedAndRestoredFromTheCorrectParent()
        throws InterruptedException
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
    public void testDeactivated()
    {
        listener.deactivate();
        projectContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );

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
        final PropertyTree config = new PropertyTree();
        config.addString( "some", "line" );

        final Form pageDescriptorForm = Form.create()
            .addFormItem( Input.create().inputType( InputTypeName.TEXT_LINE ).name( "some" ).label( "label" ).build() )
            .build();

        final DescriptorKey pageDescriptorKey = DescriptorKey.from( "abc:abc" );

        Mockito.when( pageDescriptorService.getByKey( pageDescriptorKey ) )
            .thenReturn( PageDescriptor.create()
                             .displayName( "Landing page" )
                             .config( pageDescriptorForm )
                             .key( DescriptorKey.from( "module:landing-page" ) )
                             .build() );

        return Page.create().descriptor( pageDescriptorKey ).config( config ).regions( PageRegions.create().build() ).build();
    }

    @Test
    public void repoIsNotProject()
        throws Exception
    {
        eventPublisher.publish( Event.create( NodeEvents.NODE_CREATED_EVENT )
                                    .value( EventConstants.NODES_FIELD, List.of( ImmutableMap.builder()
                                                                                     .put( "id", "123" )
                                                                                     .put( "path", "/content/something" )
                                                                                     .put( "branch", "draft" )
                                                                                     .put( "repo", "not-a-project" )
                                                                                     .build() ) )
                                    .build() );

        assertDoesNotThrow( this::handleEvents );
    }

    private void handleEvents()
        throws InterruptedException
    {
        Mockito.verify( eventPublisher, Mockito.atLeastOnce() ).publish( eventCaptor.capture() );
        eventCaptor.getAllValues().stream().filter( event -> !handledEvents.contains( event ) ).forEach( listener::onEvent );
        handledEvents.addAll( eventCaptor.getAllValues() );
        Thread.sleep( 1000 );

    }
}
