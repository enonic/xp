package com.enonic.xp.core.content;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.attachment.AttachmentNames;
import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.DeleteContentParams;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.content.FindContentByParentParams;
import com.enonic.xp.content.FindContentByParentResult;
import com.enonic.xp.content.MoveContentParams;
import com.enonic.xp.content.PatchContentParams;
import com.enonic.xp.content.ReorderChildContentParams;
import com.enonic.xp.content.ResetContentInheritParams;
import com.enonic.xp.content.SortContentParams;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.UpdateMediaParams;
import com.enonic.xp.content.UpdateWorkflowParams;
import com.enonic.xp.content.ValidationError;
import com.enonic.xp.content.ValidationErrorCode;
import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.content.WorkflowState;
import com.enonic.xp.core.impl.content.ContentEventsSyncParams;
import com.enonic.xp.core.impl.content.ContentSyncEventType;
import com.enonic.xp.core.impl.content.ContentSyncParams;
import com.enonic.xp.core.impl.content.ParentContentSynchronizer;
import com.enonic.xp.core.impl.content.SyncContentServiceImpl;
import com.enonic.xp.core.impl.content.XDataMappingServiceImpl;
import com.enonic.xp.core.impl.schema.xdata.XDataServiceImpl;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.region.RegionDescriptors;
import com.enonic.xp.region.Regions;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.util.BinaryReferences;

import static com.enonic.xp.media.MediaInfo.IMAGE_INFO_IMAGE_HEIGHT;
import static com.enonic.xp.media.MediaInfo.IMAGE_INFO_IMAGE_WIDTH;
import static com.enonic.xp.media.MediaInfo.IMAGE_INFO_PIXEL_SIZE;
import static com.enonic.xp.media.MediaInfo.MEDIA_INFO_BYTE_SIZE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class ParentContentSynchronizerTest
    extends AbstractContentSynchronizerTest
{
    private ParentContentSynchronizer synchronizer;

    private SyncContentServiceImpl syncContentService;

    @BeforeEach
    void setUp()
    {
        synchronizer = new ParentContentSynchronizer( this.layersContentService );

        syncContentService =
            new SyncContentServiceImpl( contentTypeService, nodeService, eventPublisher, projectService, contentService, synchronizer,
                                        contentAuditLogSupport );
    }

    private Optional<Content> syncPatched( final ContentId contentId )
    {
        synchronizer.sync( ContentEventsSyncParams.create()
                               .addContentId( contentId )
                               .sourceProject( project.getName() )
                               .targetProject( layer.getName() )
                               .syncEventType( ContentSyncEventType.UPDATED )
                               .build() );

        return layerContext.callWith( () -> layersContentService.getById( contentId ) );
    }

    private Optional<Content> syncDeleted( final ContentId contentId )
    {
        synchronizer.sync( ContentEventsSyncParams.create()
                               .addContentId( contentId )
                               .sourceProject( project.getName() )
                               .targetProject( layer.getName() )
                               .syncEventType( ContentSyncEventType.DELETED )
                               .build() );

        return layerContext.callWith( () -> layersContentService.getById( contentId ) );
    }

    private void sync( final ContentId contentId, final boolean includeChildren )
    {
        final ContentSyncParams.Builder builder = ContentSyncParams.create()
            .sourceProject( project.getName() )
            .targetProject( layer.getName() )
            .includeChildren( includeChildren );

        if ( contentId != null )
        {
            builder.addContentId( contentId );
        }
        synchronizer.sync( builder.build() );
    }

    private void sync( final ContentId contentId, final ProjectName sourceProject, final ProjectName targetProject )
    {
        final ContentSyncParams.Builder builder = ContentSyncParams.create().sourceProject( sourceProject ).targetProject( targetProject );

        if ( contentId != null )
        {
            builder.addContentId( contentId );
        }
        synchronizer.sync( builder.build() );
    }


    @Test
    void testCreatedChild()
    {
        final Content sourceParent = projectContext.callWith( () -> createContent( ContentPath.ROOT ) );
        final Content sourceChild = projectContext.callWith( () -> createContent( sourceParent.getPath() ) );

        syncCreated( sourceParent.getId() );
        final Content targetChild = syncCreated( sourceChild.getId() ).orElseThrow();

        layerContext.runWith( () -> {
            assertEquals( contentService.getById( sourceChild.getId() ).getParentPath(),
                          contentService.getById( sourceParent.getId() ).getPath() );

            compareSynched( sourceChild, targetChild );
        } );

    }

    @Test
    void syncCreatedWithChildren()
    {
        final CreateContentParams createContentParams = CreateContentParams.create()
            .contentData( new PropertyTree() )
            .displayName( "This is my content" )
            .createAttachments( CreateAttachments.create()
                                    .add( CreateAttachment.create()
                                              .byteSource( ByteSource.wrap( "bytes".getBytes() ) )
                                              .label( "attachment" )
                                              .name( "attachmentName" )
                                              .mimeType( "image/png" )
                                              .build() )
                                    .build() )
            .parent( ContentPath.ROOT )
            .type( ContentTypeName.folder() )
            .build();

        final Content sourceContent = projectContext.callWith( () -> this.contentService.create( createContentParams ) );
        final Content targetContent = syncCreated( sourceContent.getId() ).orElseThrow();

        compareSynched( sourceContent, targetContent );
    }

    @Test
    void testCreateExisted()
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT ) );

        syncCreated( sourceContent.getId() );
        // second "create" that might appear in multi inheritance must not throw, but targetContent should remain equal to sourceContent
        final Content targetContent = syncCreated( sourceContent.getId() ).orElseThrow();

        compareSynched( sourceContent, targetContent );
    }

    @Test
    void testCreatedWithoutSynchedParent()
    {
        final Content sourceParent = projectContext.callWith( () -> createContent( ContentPath.ROOT ) );
        final Content sourceChild = projectContext.callWith( () -> createContent( sourceParent.getPath() ) );

        assertFalse( syncCreated( sourceChild.getId() ).isPresent() );

    }

    @Test
    void testCreatedWithSetOriginProject()
    {
        final PropertyTree data = new PropertyTree();
        data.setString( ContentPropertyNames.ORIGIN_PROJECT, "first" );

        final CreateContentParams createContentParams = CreateContentParams.create()
            .contentData( data )
            .displayName( "This is my content" )
            .createAttachments( CreateAttachments.create()
                                    .add( CreateAttachment.create()
                                              .byteSource( ByteSource.wrap( "bytes".getBytes() ) )
                                              .label( "attachment" )
                                              .name( "attachmentName" )
                                              .mimeType( "image/png" )
                                              .build() )
                                    .build() )
            .parent( ContentPath.ROOT )
            .type( ContentTypeName.folder() )
            .build();

        final Content sourceContent = projectContext.callWith( () -> this.contentService.create( createContentParams ) );
        final Content targetContent = syncCreated( sourceContent.getId() ).orElseThrow();

        compareSynched( sourceContent, targetContent );

        assertEquals( "source_project", targetContent.getOriginProject().toString() );
    }

    @Test
    void updateNotCreated()
    {
        assertFalse( syncUpdated( ContentId.from( "source" ) ).isPresent() );
    }

    @Test
    void syncInvalidParent()
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "content1" ) );
        syncCreated( sourceContent.getId() );

        final Content updatedContent = projectContext.callWith( () -> contentService.update(
            new UpdateContentParams().contentId( sourceContent.getId() ).editor( edit -> edit.data.addString( "a", "b" ) ) ) );

        sync( sourceContent.getId(), nonRelatedProject.getName(), layer.getName() );

        assertNotEquals( layerContext.callWith( () -> contentService.getById( sourceContent.getId() ).getData() ),
                         updatedContent.getData() );
    }

    @Test
    void updateNotChanged()
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT ) );

        final Content targetContent1 = syncCreated( sourceContent.getId() ).orElseThrow();
        final Content targetContent2 = syncUpdated( sourceContent.getId() ).orElseThrow();

        assertEquals( targetContent1, targetContent2 );
    }

    @Test
    void updateDataChanged()
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "content1" ) );
        final Content targetContent = syncCreated( sourceContent.getId() ).orElseThrow();

        projectContext.runWith( () -> {
            contentService.update(
                new UpdateContentParams().contentId( sourceContent.getId() ).editor( ( edit -> edit.data = new PropertyTree() ) ) );
            contentService.updateWorkflow( UpdateWorkflowParams.create()
                                               .contentId( sourceContent.getId() )
                                               .editor( edit -> edit.state = WorkflowState.READY )
                                               .build() );
        } );

        final Content targetContentUpdated = syncUpdated( sourceContent.getId() ).orElseThrow();

        assertNotEquals( targetContent.getData(), targetContentUpdated.getData() );
        assertNotEquals( targetContent.getModifiedTime(), targetContentUpdated.getModifiedTime() );
    }

    @Test
    void updateMediaChanged()
    {
        xDataService = new XDataServiceImpl( mock( ApplicationService.class ), resourceService );
        xDataMappingService = new XDataMappingServiceImpl( siteService, xDataService );
        contentService.setxDataService( xDataService );
        contentService.setXDataMappingService( xDataMappingService );

        final Content sourceContent = projectContext.callWith( () -> createMedia( "media", ContentPath.ROOT ) );
        final Content targetContent = syncCreated( sourceContent.getId() ).orElseThrow();

        projectContext.runWith( () -> {
            contentService.update( new UpdateMediaParams().content( sourceContent.getId() )
                                       .name( "new name" )
                                       .byteSource( loadImage( "darth-small.jpg" ) )
                                       .mimeType( "image/jpeg" )
                                       .artist( List.of( "artist" ) )
                                       .altText( "alt text" )
                                       .copyright( "copy" )
                                       .tags( List.of( "my new tags" ) )
                                       .caption( "caption" ) );
            contentService.updateWorkflow( UpdateWorkflowParams.create()
                                               .contentId( sourceContent.getId() )
                                               .editor( edit -> edit.state = WorkflowState.READY )
                                               .build() );
        } );

        final Content targetContentUpdated = syncUpdated( sourceContent.getId() ).orElseThrow();

        assertNotEquals( targetContent.getAttachments().first().getBinaryReference(),
                         targetContentUpdated.getAttachments().first().getBinaryReference() );
        assertEquals( "new name", targetContentUpdated.getAttachments().first().getName() );
        assertEquals( "artist", targetContentUpdated.getData().getString( "artist" ) );
        assertEquals( "caption", targetContentUpdated.getData().getString( "caption" ) );
        assertEquals( "copy", targetContentUpdated.getData().getString( "copyright" ) );
        assertEquals( "my new tags", targetContentUpdated.getData().getString( "tags" ) );
    }

    @Test
    void patch()
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "content1" ) );
        syncCreated( sourceContent.getId() );

        projectContext.callWith( () -> contentService.patch( PatchContentParams.create().patcher( edit -> {
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


        } ).contentId( sourceContent.getId() ).build() ) );

        final Optional<Content> patchedContent =
            projectContext.callWith( () -> this.layersContentService.getById( sourceContent.getId() ) );
        final Optional<Content> targetContent = syncPatched( sourceContent.getId() );

        compareSynched( patchedContent.orElseThrow(), targetContent.orElseThrow() );
    }


    @Test
    void updateAttachmentsChanged()
    {
        xDataService = new XDataServiceImpl( mock( ApplicationService.class ), resourceService );
        xDataMappingService = new XDataMappingServiceImpl( siteService, xDataService );
        contentService.setxDataService( xDataService );
        contentService.setXDataMappingService( xDataMappingService );

        final Content sourceContent = projectContext.callWith( () -> createMedia( "media", ContentPath.ROOT ) );
        syncCreated( sourceContent.getId() );

        Content sourceContentUpdated = projectContext.callWith( () -> {
            contentService.update( new UpdateContentParams().contentId( sourceContent.getId() )
                                       .createAttachments( CreateAttachments.create()
                                                               .add( CreateAttachment.create()
                                                                         .name( "new name" )
                                                                         .byteSource( loadImage( "darth-small.jpg" ) )
                                                                         .mimeType( "image/jpeg" )
                                                                         .build() )
                                                               .build() ) );
            return contentService.updateWorkflow( UpdateWorkflowParams.create()
                                                      .contentId( sourceContent.getId() )
                                                      .editor( edit -> edit.state = WorkflowState.READY )
                                                      .build() ).getContent();
        } );

        Content targetContentUpdated = syncUpdated( sourceContent.getId() ).orElseThrow();

        assertEquals( sourceContentUpdated.getAttachments(), targetContentUpdated.getAttachments() );

        sourceContentUpdated = projectContext.callWith( () -> {
            contentService.update( new UpdateContentParams().contentId( sourceContent.getId() )
                                       .clearAttachments( true )
                                       .createAttachments( CreateAttachments.create()
                                                               .add( CreateAttachment.create()
                                                                         .name( "new name 1" )
                                                                         .byteSource( loadImage( "darth-small.jpg" ) )
                                                                         .mimeType( "image/jpeg" )
                                                                         .build() )
                                                               .build() ) );
            return contentService.updateWorkflow( UpdateWorkflowParams.create()
                                                      .contentId( sourceContent.getId() )
                                                      .editor( edit -> edit.state = WorkflowState.READY )
                                                      .build() ).getContent();
        } );

        targetContentUpdated = syncUpdated( sourceContent.getId() ).orElseThrow();

        assertEquals( sourceContentUpdated.getAttachments(), targetContentUpdated.getAttachments() );
    }

    @Test
    void updateThumbnailCreated()
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "content" ) );
        final Content targetContent = syncCreated( sourceContent.getId() ).orElseThrow();

        projectContext.runWith( () -> {
            final UpdateContentParams updateContentParams = new UpdateContentParams();
            updateContentParams.contentId( targetContent.getId() )
                .editor( edit -> edit.displayName = "new display name" )
                .createAttachments( CreateAttachments.from( CreateAttachment.create()
                                                                .byteSource( loadImage( "darth-small.jpg" ) )
                                                                .name( AttachmentNames.THUMBNAIL )
                                                                .mimeType( "image/jpeg" )
                                                                .build() ) );

            contentService.update( updateContentParams );
            contentService.updateWorkflow( UpdateWorkflowParams.create()
                                               .contentId( sourceContent.getId() )
                                               .editor( edit -> edit.state = WorkflowState.READY )
                                               .build() );
        } );

        final Content targetContentUpdated = syncUpdated( sourceContent.getId() ).orElseThrow();

        assertNotNull( targetContentUpdated.getAttachments().byName( AttachmentNames.THUMBNAIL ) );
    }

    @Test
    void updateThumbnailUpdated()
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "content" ) );
        final Content targetContent = syncCreated( sourceContent.getId() ).orElseThrow();

        projectContext.runWith( () -> {
            final UpdateContentParams updateContentParams = new UpdateContentParams();
            updateContentParams.contentId( targetContent.getId() )
                .editor( edit -> edit.displayName = "new display name" )
                .createAttachments( CreateAttachments.from( CreateAttachment.create()
                                                                .byteSource( loadImage( "darth-small.jpg" ) )
                                                                .name( AttachmentNames.THUMBNAIL )
                                                                .mimeType( "image/jpeg" )
                                                                .build() ) );

            contentService.update( updateContentParams );
            contentService.updateWorkflow( UpdateWorkflowParams.create()
                                               .contentId( sourceContent.getId() )
                                               .editor( edit -> edit.state = WorkflowState.READY )
                                               .build() );
        } );

        final Content thumbnailCreated = syncUpdated( sourceContent.getId() ).orElseThrow();

        projectContext.runWith( () -> {
            final UpdateContentParams updateContentParams = new UpdateContentParams();
            updateContentParams.contentId( targetContent.getId() )
                .editor( edit -> edit.displayName = "new display name" )
                .createAttachments( CreateAttachments.from( CreateAttachment.create()
                                                                .byteSource( loadImage( "cat-small.jpg" ) )
                                                                .name( AttachmentNames.THUMBNAIL )
                                                                .mimeType( "image/jpeg" )
                                                                .build() ) );

            contentService.update( updateContentParams );
            contentService.updateWorkflow( UpdateWorkflowParams.create()
                                               .contentId( sourceContent.getId() )
                                               .editor( edit -> edit.state = WorkflowState.READY )
                                               .build() );
        } );

        final Content thumbnailUpdated = syncUpdated( sourceContent.getId() ).orElseThrow();

        assertNotEquals( thumbnailCreated.getAttachments().byName( AttachmentNames.THUMBNAIL ).getSize(),
                         thumbnailUpdated.getAttachments().byName( AttachmentNames.THUMBNAIL ).getSize() );
    }

    @Test
    void updateBinaryChanged()
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "content1" ) );
        syncCreated( sourceContent.getId() );

        projectContext.runWith( () -> {
            contentService.update( new UpdateContentParams().contentId( sourceContent.getId() )
                                       .createAttachments( CreateAttachments.create()
                                                               .add( CreateAttachment.create()
                                                                         .name( AttachmentNames.THUMBNAIL )
                                                                         .byteSource( ByteSource.wrap( "this is image".getBytes() ) )
                                                                         .mimeType( "image/png" )
                                                                         .text( "This is the image" )
                                                                         .build() )
                                                               .build() )
                                       .editor( _ -> {
                                       } ) );
            contentService.updateWorkflow( UpdateWorkflowParams.create()
                                               .contentId( sourceContent.getId() )
                                               .editor( edit -> edit.state = WorkflowState.READY )
                                               .build() );
        } );

        final Content targetContentWithThumbnail = syncUpdated( sourceContent.getId() ).orElseThrow();

        assertNotNull( targetContentWithThumbnail.getAttachments().byName( AttachmentNames.THUMBNAIL ) );

        projectContext.runWith( () -> contentService.update( new UpdateContentParams().contentId( sourceContent.getId() )
                                                                 .removeAttachments( BinaryReferences.from( AttachmentNames.THUMBNAIL ) )
                                                                 .editor( _ -> {
                                                                 } ) ) );
        contentService.updateWorkflow(
            UpdateWorkflowParams.create().contentId( sourceContent.getId() ).editor( edit -> edit.state = WorkflowState.READY ).build() );

        final Content targetContentWithoutThumbnail = syncUpdated( sourceContent.getId() ).orElseThrow();

        assertNull( targetContentWithoutThumbnail.getAttachments().byName( AttachmentNames.THUMBNAIL ) );
    }


    @Test
    void sortNotExisted()
    {
        assertFalse( syncSorted( ContentId.from( "source" ) ).isPresent() );
    }

    @Test
    void sortNotSynched()
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );
        assertThrows( IllegalArgumentException.class, () -> syncSorted( sourceContent.getId() ) );
    }

    @Test
    void sortNotChanged()
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );
        final Content targetContent = syncCreated( sourceContent.getId() ).orElseThrow();

        syncSorted( sourceContent.getId() );
        assertEquals( targetContent, layerContext.callWith( () -> contentService.getById( sourceContent.getId() ) ) );
    }

    @Test
    void sortChanged()
    {
        final Content sourceParent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );
        final Content targetParent = syncCreated( sourceParent.getId() ).orElseThrow();

        projectContext.runWith( () -> contentService.sort(
            SortContentParams.create().contentId( sourceParent.getId() ).childOrder( ChildOrder.from( "modifiedTime ASC" ) ).build() ) );

        final Content targetContentSorted = syncSorted( sourceParent.getId() ).orElseThrow();

        assertNotEquals( targetParent.getChildOrder(), targetContentSorted.getChildOrder() );
    }

    @Test
    void sortRestoredToManual()
    {
        final Content sourceParent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "parent" ) );
        final Content sourceChild1 = projectContext.callWith( () -> createContent( sourceParent.getPath(), "name1" ) );
        final Content sourceChild2 = projectContext.callWith( () -> createContent( sourceParent.getPath(), "name2" ) );
        final Content sourceChild3 = projectContext.callWith( () -> createContent( sourceParent.getPath(), "name3" ) );

        syncCreated( sourceParent.getId() );
        syncCreated( sourceChild1.getId() );
        syncCreated( sourceChild2.getId() );
        syncCreated( sourceChild3.getId() );

        projectContext.runWith( () -> contentService.sort( SortContentParams.create()
                                                               .contentId( sourceParent.getId() )
                                                               .childOrder( ChildOrder.manualOrder() )
                                                               .addManualOrder( ReorderChildContentParams.create()
                                                                                    .contentToMove( sourceChild1.getId() )
                                                                                    .contentToMoveBefore( sourceChild3.getId() )
                                                                                    .build() )
                                                               .build() ) );

        syncSorted( sourceParent.getId() );
        syncUpdated( sourceChild1.getId() );
        syncUpdated( sourceChild2.getId() );
        syncUpdated( sourceChild3.getId() );

        layerContext.runWith( () -> {
            contentService.sort( SortContentParams.create()
                                     .contentId( sourceParent.getId() )
                                     .childOrder( ChildOrder.manualOrder() )
                                     .addManualOrder( ReorderChildContentParams.create()
                                                          .contentToMove( sourceChild1.getId() )
                                                          .contentToMoveBefore( sourceChild2.getId() )
                                                          .build() )
                                     .build() );

            syncContentService.resetInheritance( ResetContentInheritParams.create()
                                                     .contentId( sourceParent.getId() )
                                                     .inherit( List.of( ContentInheritType.SORT ) )
                                                     .projectName( ProjectName.from( layerContext.getRepositoryId() ) )
                                                     .build() );
        } );

        syncSorted( sourceParent.getId() );

        refresh();

        final FindContentByParentResult sourceOrderedChildren = projectContext.callWith(
            () -> contentService.findByParent( FindContentByParentParams.create().parentId( sourceParent.getId() ).build() ) );
        final FindContentByParentResult targetOrderedChildren = layerContext.callWith(
            () -> contentService.findByParent( FindContentByParentParams.create().parentId( sourceParent.getId() ).build() ) );

        assertThat( sourceOrderedChildren.getContents() ).map( Content::getId )
            .containsExactly( targetOrderedChildren.getContents().stream().map( Content::getId ).toArray( ContentId[]::new ) );
    }


    @Test
    void moveNotExisted()
    {
        assertFalse( syncMoved( ContentId.from( "source" ) ).isPresent() );
    }

    @Test
    void moveNotSynched()
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );

        assertThrows( IllegalArgumentException.class, () -> syncMoved( sourceContent.getId() ), "sourceContent must be set." );
    }

    @Test
    void moveNotChanged()
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );
        final Content targetContent = syncCreated( sourceContent.getId() ).orElseThrow();

        final Content result = syncMoved( sourceContent.getId() ).orElseThrow();
        assertEquals( targetContent, result );
    }

    @Test
    void moveChanged()
    {
        final Content sourceContent1 = projectContext.callWith( () -> createContent( ContentPath.ROOT, "name1" ) );
        final Content sourceContent2 = projectContext.callWith( () -> createContent( ContentPath.ROOT, "name2" ) );

        final Content targetContent1 = syncCreated( sourceContent1.getId() ).orElseThrow();
        final Content targetContent2 = syncCreated( sourceContent2.getId() ).orElseThrow();

        assertEquals( "/name1", targetContent1.getPath().toString() );
        assertEquals( "/name2", targetContent2.getPath().toString() );

        projectContext.runWith( () -> contentService.move(
            MoveContentParams.create().contentId( sourceContent1.getId() ).parentContentPath( sourceContent2.getPath() ).build() ) );

        final Content targetContentSorted = syncMoved( sourceContent1.getId() ).orElseThrow();

        assertEquals( "/name2/name1", targetContentSorted.getPath().toString() );
    }


    @Test
    void deleteNotExisted()
    {
        assertTrue( syncDeleted( ContentId.from( "source" ) ).isEmpty() );
    }

    @Test
    void deleteNotSynched()
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );

        assertThrows( IllegalArgumentException.class, () -> syncDeleted( sourceContent.getId() ), "targetContent must be set." );
    }

    @Test
    void deleteNotDeletedInParent()
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );
        final Content targetContent = syncCreated( sourceContent.getId() ).orElseThrow();

        assertTrue( syncDeleted( targetContent.getId() ).isPresent() );
    }

    @Test
    void deleteDeletedInParent()
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );
        final Content targetContent = syncCreated( sourceContent.getId() ).orElseThrow();

        projectContext.runWith(
            () -> contentService.delete( DeleteContentParams.create().contentPath( sourceContent.getPath() ).build() ) );

        assertTrue( syncDeleted( targetContent.getId() ).isEmpty() );
    }

    @Test
    void syncDeletedInParent()
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );
        syncCreated( sourceContent.getId() );

        projectContext.runWith(
            () -> contentService.delete( DeleteContentParams.create().contentPath( sourceContent.getPath() ).build() ) );

        sync( sourceContent.getId(), false );

        assertFalse( layerContext.callWith( () -> {
            final ContentId id = sourceContent.getId();
            return layersContentService.getById( id ).isPresent();
        } ) );
    }

    @Test
    void syncDeletionInMiddleLayer()
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );
        syncCreated( sourceContent.getId() );

        layerContext.runWith( () -> contentService.delete( DeleteContentParams.create().contentPath( sourceContent.getPath() ).build() ) );

        refresh();

        sync( sourceContent.getId(), layer.getName(), childLayer.getName() );

        assertFalse( childLayerContext.callWith( () -> {
            final ContentId id = sourceContent.getId();
            return layersContentService.getById( id ).isPresent();
        } ) );
        assertFalse( layerContext.callWith( () -> {
            final ContentId id = sourceContent.getId();
            return layersContentService.getById( id ).isPresent();
        } ) );

        sync( sourceContent.getId(), project.getName(), layer.getName() );

        assertTrue( layerContext.callWith( () -> {
            final ContentId id = sourceContent.getId();
            return layersContentService.getById( id ).isPresent();
        } ) );
        assertFalse( childLayerContext.callWith( () -> {
            final ContentId id = sourceContent.getId();
            return layersContentService.getById( id ).isPresent();
        } ) );

        sync( sourceContent.getId(), layer.getName(), childLayer.getName() );

        assertTrue( childLayerContext.callWith( () -> {
            final ContentId id = sourceContent.getId();
            return layersContentService.getById( id ).isPresent();
        } ) );
        assertTrue( layerContext.callWith( () -> {
            final ContentId id = sourceContent.getId();
            return layersContentService.getById( id ).isPresent();
        } ) );
    }

    @Test
    void syncWithChildren()
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );
        final Content sourceChild1 = projectContext.callWith( () -> createContent( sourceContent.getPath(), "child1" ) );

        sync( sourceContent.getId(), true );

        assertTrue( layerContext.callWith( () -> {
            final ContentId id = sourceChild1.getId();
            return layersContentService.getById( id ).isPresent();
        } ) );

        final Content sourceChild1_1 = projectContext.callWith( () -> createContent( sourceContent.getPath(), "child1_1" ) );

        sync( sourceContent.getId(), true );

        assertTrue( layerContext.callWith( () -> {
            final ContentId id = sourceChild1_1.getId();
            return layersContentService.getById( id ).isPresent();
        } ) );

        projectContext.callWith( () -> contentService.move(
            MoveContentParams.create().contentId( sourceChild1_1.getId() ).parentContentPath( ContentPath.ROOT ).build() ) );
        projectContext.callWith(
            () -> contentService.delete( DeleteContentParams.create().contentPath( sourceChild1.getPath() ).build() ) );
        projectContext.callWith( () -> contentService.update(
            new UpdateContentParams().contentId( sourceContent.getId() ).editor( edit -> edit.data = new PropertyTree() ) ) );

        sync( null, true );

        assertEquals( "/child1_1", layerContext.callWith( () -> contentService.getById( sourceChild1_1.getId() ).getPath().toString() ) );
        assertFalse( layerContext.callWith( () -> {
            final ContentId id = sourceChild1.getId();
            return layersContentService.getById( id ).isPresent();
        } ) );

    }

    @Test
    void syncWithoutChildren()
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );
        final Content sourceChild1 = projectContext.callWith( () -> createContent( sourceContent.getPath(), "child1" ) );

        sync( sourceContent.getId(), false );

        assertTrue( layerContext.callWith( () -> {
            final ContentId id = sourceContent.getId();
            return layersContentService.getById( id ).isPresent();
        } ) );
        assertFalse( layerContext.callWith( () -> {
            final ContentId id = sourceChild1.getId();
            return layersContentService.getById( id ).isPresent();
        } ) );
    }

    @Test
    void syncByRoot()
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );
        final Content sourceChild1 = projectContext.callWith( () -> createContent( sourceContent.getPath(), "child1" ) );
        final Content sourceChild1_1 = projectContext.callWith( () -> createContent( sourceContent.getPath(), "child1_1" ) );

        sync( null, false );

        assertTrue( layerContext.callWith( () -> {
            final ContentId id = sourceContent.getId();
            return layersContentService.getById( id ).isPresent();
        } ) );
        assertTrue( layerContext.callWith( () -> {
            final ContentId id = sourceChild1.getId();
            return layersContentService.getById( id ).isPresent();
        } ) );
        assertTrue( layerContext.callWith( () -> {
            final ContentId id = sourceChild1_1.getId();
            return layersContentService.getById( id ).isPresent();
        } ) );
    }


    @Test
    void updateManualOrderNotExisted()
    {
        assertFalse( syncUpdated( ContentId.from( "source" ) ).isPresent() );
    }

    @Test
    void updateManualOrderNotSynched()
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );

        assertThrows( IllegalArgumentException.class, () -> syncUpdated( sourceContent.getId() ), "sourceContent must be set." );
    }

    @Test
    void updateManualOrderNotChanged()
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );
        final Content targetContent = syncCreated( sourceContent.getId() ).orElseThrow();

        assertEquals( targetContent, syncUpdated( sourceContent.getId() ).orElseThrow() );
    }

    @Test
    void updateManualOrderValue()
    {
        final Content sourceParent = projectContext.callWith( () -> createContent( ContentPath.ROOT ) );
        final Content sourceChild1 = projectContext.callWith( () -> createContent( sourceParent.getPath(), "child1" ) );
        final Content sourceChild2 = projectContext.callWith( () -> createContent( sourceParent.getPath(), "child2" ) );

        syncCreated( sourceParent.getId() );
        syncCreated( sourceChild1.getId() );
        syncCreated( sourceChild2.getId() );

        projectContext.runWith( () -> {
            contentService.sort( SortContentParams.create()
                                     .contentId( sourceParent.getId() )
                                     .childOrder( ChildOrder.manualOrder() )
                                     .addManualOrder( ReorderChildContentParams.create()
                                                          .contentToMove( sourceChild1.getId() )
                                                          .contentToMoveBefore( sourceChild2.getId() )
                                                          .build() )
                                     .build() );

            assertTrue( syncSorted( sourceParent.getId() ).orElseThrow().getChildOrder().isManualOrder() );

            Long newManualOrderValue1 = syncUpdated( sourceChild1.getId() ).orElseThrow().getManualOrderValue();
            Long newManualOrderValue2 = syncUpdated( sourceChild2.getId() ).orElseThrow().getManualOrderValue();

            assertThat( newManualOrderValue1 ).isGreaterThan( newManualOrderValue2 );

            contentService.sort( SortContentParams.create()
                                     .contentId( sourceParent.getId() )
                                     .childOrder( ChildOrder.manualOrder() )
                                     .addManualOrder( ReorderChildContentParams.create()
                                                          .contentToMove( sourceChild2.getId() )
                                                          .contentToMoveBefore( sourceChild1.getId() )
                                                          .build() )
                                     .build() );

            newManualOrderValue1 = syncUpdated( sourceChild1.getId() ).orElseThrow().getManualOrderValue();
            newManualOrderValue2 = syncUpdated( sourceChild2.getId() ).orElseThrow().getManualOrderValue();

            assertThat( newManualOrderValue1 ).isLessThan( newManualOrderValue2 );
        } );
    }

    private Optional<Content> syncCreated( final ContentId contentId )
    {
        synchronizer.sync( ContentEventsSyncParams.create()
                               .addContentId( contentId )
                               .sourceProject( project.getName() )
                               .targetProject( layer.getName() )
                               .syncEventType( ContentSyncEventType.CREATED )
                               .build() );

        synchronizer.sync( ContentEventsSyncParams.create()
                               .addContentId( contentId )
                               .sourceProject( layer.getName() )
                               .targetProject( childLayer.getName() )
                               .syncEventType( ContentSyncEventType.CREATED )
                               .build() );

        return layerContext.callWith( () -> layersContentService.getById( contentId ) );
    }

    private Optional<Content> syncUpdated( final ContentId contentId )
    {
        synchronizer.sync( ContentEventsSyncParams.create()
                               .addContentId( contentId )
                               .sourceProject( project.getName() )
                               .targetProject( layer.getName() )
                               .syncEventType( ContentSyncEventType.UPDATED )
                               .build() );

        return layerContext.callWith( () -> layersContentService.getById( contentId ) );
    }

    private Optional<Content> syncMoved( final ContentId contentId )
    {
        synchronizer.sync( ContentEventsSyncParams.create()
                               .addContentId( contentId )
                               .sourceProject( project.getName() )
                               .targetProject( layer.getName() )
                               .syncEventType( ContentSyncEventType.MOVED )
                               .build() );

        return layerContext.callWith( () -> layersContentService.getById( contentId ) );
    }

    private Optional<Content> syncSorted( final ContentId contentId )
    {
        synchronizer.sync( ContentEventsSyncParams.create()
                               .addContentId( contentId )
                               .sourceProject( project.getName() )
                               .targetProject( layer.getName() )
                               .syncEventType( ContentSyncEventType.SORTED )
                               .build() );

        return layerContext.callWith( () -> layersContentService.getById( contentId ) );
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
                             .regions( RegionDescriptors.create().build() )
                             .key( DescriptorKey.from( "module:landing-page" ) )
                             .build() );

        return Page.create().descriptor( pageDescriptorKey ).config( config ).regions( Regions.create().build() ).build();
    }
}
