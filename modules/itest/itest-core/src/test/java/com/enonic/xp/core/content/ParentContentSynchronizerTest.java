package com.enonic.xp.core.content;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;

import com.enonic.xp.attachment.AttachmentNames;
import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.DeleteContentParams;
import com.enonic.xp.content.MoveContentParams;
import com.enonic.xp.content.RenameContentParams;
import com.enonic.xp.content.ReorderChildContentsParams;
import com.enonic.xp.content.ReorderChildParams;
import com.enonic.xp.content.SetContentChildOrderParams;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.UpdateMediaParams;
import com.enonic.xp.core.impl.content.ContentEventsSyncParams;
import com.enonic.xp.core.impl.content.ContentSyncEventType;
import com.enonic.xp.core.impl.content.ContentSyncParams;
import com.enonic.xp.core.impl.content.ParentContentSynchronizer;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.util.BinaryReferences;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParentContentSynchronizerTest
    extends AbstractContentSynchronizerTest
{
    private ParentContentSynchronizer synchronizer;

    @BeforeEach
    protected void setUpNode()
        throws Exception
    {
        super.setUpNode();

        synchronizer = new ParentContentSynchronizer( this.contentService, this.mediaInfoService );
    }

    private Content syncCreated( final ContentId contentId )
    {
        synchronizer.sync( ContentEventsSyncParams.create()
                               .addContentId( contentId )
                               .sourceProject( sourceProject.getName() )
                               .targetProject( targetProject.getName() )
                               .syncEventType( ContentSyncEventType.CREATED )
                               .build() );

        return targetContext.callWith( () -> contentService.getById( contentId ) );
    }

    private Content syncUpdated( final ContentId contentId )
    {
        synchronizer.sync( ContentEventsSyncParams.create()
                               .addContentId( contentId )
                               .sourceProject( sourceProject.getName() )
                               .targetProject( targetProject.getName() )
                               .syncEventType( ContentSyncEventType.UPDATED )
                               .build() );

        return targetContext.callWith( () -> contentService.getById( contentId ) );

    }

    private Content syncRenamed( final ContentId contentId )
    {
        synchronizer.sync( ContentEventsSyncParams.create()
                               .addContentId( contentId )
                               .sourceProject( sourceProject.getName() )
                               .targetProject( targetProject.getName() )
                               .syncEventType( ContentSyncEventType.RENAMED )
                               .build() );

        return targetContext.callWith( () -> contentService.getById( contentId ) );

    }

    private Content syncMoved( final ContentId contentId )
    {
        synchronizer.sync( ContentEventsSyncParams.create()
                               .addContentId( contentId )
                               .sourceProject( sourceProject.getName() )
                               .targetProject( targetProject.getName() )
                               .syncEventType( ContentSyncEventType.MOVED )
                               .build() );

        return targetContext.callWith( () -> contentService.getById( contentId ) );

    }

    private Content syncSorted( final ContentId contentId )
    {
        synchronizer.sync( ContentEventsSyncParams.create()
                               .addContentId( contentId )
                               .sourceProject( sourceProject.getName() )
                               .targetProject( targetProject.getName() )
                               .syncEventType( ContentSyncEventType.SORTED )
                               .build() );

        return targetContext.callWith( () -> contentService.getById( contentId ) );

    }

    private boolean syncDeleted( final ContentId contentId )
    {
        synchronizer.sync( ContentEventsSyncParams.create()
                               .addContentId( contentId )
                               .sourceProject( sourceProject.getName() )
                               .targetProject( targetProject.getName() )
                               .syncEventType( ContentSyncEventType.DELETED )
                               .build() );

        return !targetContext.callWith( () -> contentService.contentExists( contentId ) );
    }

    private Content syncManualOrderUpdated( final ContentId contentId )
    {
        synchronizer.sync( ContentEventsSyncParams.create()
                               .addContentId( contentId )
                               .sourceProject( sourceProject.getName() )
                               .targetProject( targetProject.getName() )
                               .syncEventType( ContentSyncEventType.MANUAL_ORDER_UPDATED )
                               .build() );

        return targetContext.callWith( () -> contentService.getById( contentId ) );
    }

    private void sync( final ContentId contentId, final boolean includeChildren )
    {
        final ContentSyncParams.Builder builder = ContentSyncParams.create()
            .sourceProject( sourceProject.getName() )
            .targetProject( targetProject.getName() )
            .includeChildren( includeChildren );

        if ( contentId != null )
        {
            builder.addContentId( contentId );
        }
        synchronizer.sync( builder.build() );
    }

    @Test
    public void testCreatedChild()
        throws Exception
    {
        final Content sourceParent = sourceContext.callWith( () -> createContent( ContentPath.ROOT ) );
        final Content sourceChild = sourceContext.callWith( () -> createContent( sourceParent.getPath() ) );

        syncCreated( sourceParent.getId() );
        final Content targetChild = syncCreated( sourceChild.getId() );

        targetContext.runWith( () -> {
            assertEquals( contentService.getById( sourceChild.getId() ).getParentPath(),
                          contentService.getById( sourceParent.getId() ).getPath() );

            compareSynched( sourceChild, targetChild );
        } );

    }

    @Test
    public void syncCreatedWithChildren()
        throws Exception
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

        final Content sourceContent = sourceContext.callWith( () -> this.contentService.create( createContentParams ) );
        final Content targetContent = syncCreated( sourceContent.getId() );

        compareSynched( sourceContent, targetContent );
    }

    @Test
    public void testCreateExisted()
        throws Exception
    {
        final Content sourceContent = sourceContext.callWith( () -> createContent( ContentPath.ROOT ) );

        final Content targetContent1 = syncCreated( sourceContent.getId() );

        assertThrows( IllegalArgumentException.class, () -> syncCreated( sourceContent.getId() ), "targetContent must be set." );

        final Content targetContent2 = targetContext.callWith( () -> contentService.getById( sourceContent.getId() ) );

        assertEquals( targetContent1, targetContent2 );

        compareSynched( sourceContent, targetContent1 );
    }

    @Test
    public void testCreatedWithoutSynchedParent()
        throws Exception
    {
        final Content sourceParent = sourceContext.callWith( () -> createContent( ContentPath.ROOT ) );
        final Content sourceChild = sourceContext.callWith( () -> createContent( sourceParent.getPath() ) );

        assertThrows( ContentNotFoundException.class, () -> syncCreated( sourceChild.getId() ) );

    }

    @Test
    public void testCreatedWithSetOriginProject()
        throws Exception
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

        final Content sourceContent = sourceContext.callWith( () -> this.contentService.create( createContentParams ) );
        final Content targetContent = syncCreated( sourceContent.getId() );

        compareSynched( sourceContent, targetContent );

        assertEquals( "source_project", targetContent.getOriginProject().toString() );
    }

    @Test
    public void updateNotCreated()
        throws Exception
    {
        assertThrows( NullPointerException.class, () -> syncUpdated( ContentId.from( "source" ) ), "sourceContent must be set." );
    }

    @Test
    public void updateNotSynched()
        throws Exception
    {
        final Content targetContent = targetContext.callWith( () -> contentService.getByPath( ContentPath.ROOT ) );

        assertThrows( IllegalArgumentException.class, () -> syncUpdated( targetContent.getId() ), "sourceContent must be set." );
    }

    @Test
    public void updateNotChanged()
        throws Exception
    {
        final Content sourceContent = sourceContext.callWith( () -> createContent( ContentPath.ROOT ) );

        final Content targetContent1 = syncCreated( sourceContent.getId() );
        final Content targetContent2 = syncUpdated( sourceContent.getId() );

        assertEquals( targetContent1, targetContent2 );
    }

    @Test
    public void updateDataChanged()
        throws Exception
    {
        final Content sourceContent = sourceContext.callWith( () -> createContent( ContentPath.ROOT, "content1" ) );
        final Content targetContent = syncCreated( sourceContent.getId() );

        sourceContext.runWith( () -> {
            contentService.update(
                new UpdateContentParams().contentId( sourceContent.getId() ).editor( ( edit -> edit.data = new PropertyTree() ) ) );
        } );

        final Content targetContentUpdated = syncUpdated( sourceContent.getId() );

        assertNotEquals( targetContent.getData(), targetContentUpdated.getData() );
        assertNotEquals( targetContent.getModifiedTime(), targetContentUpdated.getModifiedTime() );
    }

    @Test
    public void updateMediaChanged()
        throws Exception
    {
        final Content sourceContent = sourceContext.callWith( () -> createMedia( "media", ContentPath.ROOT ) );
        final Content targetContent = syncCreated( sourceContent.getId() );

        sourceContext.runWith( () -> {
            try
            {
                contentService.update( new UpdateMediaParams().content( sourceContent.getId() )
                                           .name( "new name" )
                                           .byteSource( loadImage( "darth-small.jpg" ) )
                                           .mimeType( "image/jpeg" )
                                           .artist( "artist" )
                                           .copyright( "copy" )
                                           .tags( "my new tags" )
                                           .caption( "caption" ) );
            }
            catch ( IOException e )
            {
                throw new RuntimeException( e );
            }
        } );

        final Content targetContentUpdated = syncUpdated( sourceContent.getId() );

        assertNotEquals( targetContent.getAttachments().first().getBinaryReference(),
                         targetContentUpdated.getAttachments().first().getBinaryReference() );
        assertEquals( "new name", targetContentUpdated.getAttachments().first().getName() );
        assertEquals( "artist", targetContentUpdated.getData().getString( "artist" ) );
        assertEquals( "caption", targetContentUpdated.getData().getString( "caption" ) );
        assertEquals( "copy", targetContentUpdated.getData().getString( "copyright" ) );
        assertEquals( "my new tags", targetContentUpdated.getData().getString( "tags" ) );
    }

    @Test
    public void updateThumbnailCreated()
        throws Exception
    {
        final Content sourceContent = sourceContext.callWith( () -> createContent( ContentPath.ROOT, "content" ) );
        final Content targetContent = syncCreated( sourceContent.getId() );

        sourceContext.runWith( () -> {
            try
            {
                final UpdateContentParams updateContentParams = new UpdateContentParams();
                updateContentParams.contentId( targetContent.getId() )
                    .editor( edit -> {
                        edit.displayName = "new display name";
                    } )
                    .createAttachments( CreateAttachments.from( CreateAttachment.create()
                                                                    .byteSource( loadImage( "darth-small.jpg" ) )
                                                                    .name( AttachmentNames.THUMBNAIL )
                                                                    .mimeType( "image/jpeg" )
                                                                    .build() ) );

                this.contentService.update( updateContentParams );
            }
            catch ( IOException e )
            {
                throw new RuntimeException( e );
            }
        } );

        final Content targetContentUpdated = syncUpdated( sourceContent.getId() );

        assertNotNull( targetContentUpdated.getThumbnail() );
    }

    @Test
    public void updateThumbnailUpdated()
        throws Exception
    {
        final Content sourceContent = sourceContext.callWith( () -> createContent( ContentPath.ROOT, "content" ) );
        final Content targetContent = syncCreated( sourceContent.getId() );

        sourceContext.runWith( () -> {
            try
            {
                final UpdateContentParams updateContentParams = new UpdateContentParams();
                updateContentParams.contentId( targetContent.getId() )
                    .editor( edit -> {
                        edit.displayName = "new display name";
                    } )
                    .createAttachments( CreateAttachments.from( CreateAttachment.create()
                                                                    .byteSource( loadImage( "darth-small.jpg" ) )
                                                                    .name( AttachmentNames.THUMBNAIL )
                                                                    .mimeType( "image/jpeg" )
                                                                    .build() ) );

                this.contentService.update( updateContentParams );
            }
            catch ( IOException e )
            {
                throw new RuntimeException( e );
            }
        } );

        final Content thumbnailCreated = syncUpdated( sourceContent.getId() );

        sourceContext.runWith( () -> {
            try
            {
                final UpdateContentParams updateContentParams = new UpdateContentParams();
                updateContentParams.contentId( targetContent.getId() )
                    .editor( edit -> {
                        edit.displayName = "new display name";
                    } )
                    .createAttachments( CreateAttachments.from( CreateAttachment.create()
                                                                    .byteSource( loadImage( "cat-small.jpg" ) )
                                                                    .name( AttachmentNames.THUMBNAIL )
                                                                    .mimeType( "image/jpeg" )
                                                                    .build() ) );

                this.contentService.update( updateContentParams );
            }
            catch ( IOException e )
            {
                throw new RuntimeException( e );
            }
        } );

        final Content thumbnailUpdated = syncUpdated( sourceContent.getId() );

        assertNotEquals( thumbnailCreated.getThumbnail().getSize(), thumbnailUpdated.getThumbnail().getSize() );
    }

    @Test
    public void updateBinaryChanged()
        throws Exception
    {
        final Content sourceContent = sourceContext.callWith( () -> createContent( ContentPath.ROOT, "content1" ) );
        syncCreated( sourceContent.getId() );

        sourceContext.runWith( () -> {
            contentService.update( new UpdateContentParams().contentId( sourceContent.getId() )
                                       .createAttachments( CreateAttachments.create()
                                                               .add( CreateAttachment.create()
                                                                         .name( AttachmentNames.THUMBNAIL )
                                                                         .byteSource( ByteSource.wrap( "this is image".getBytes() ) )
                                                                         .mimeType( "image/png" )
                                                                         .text( "This is the image" )
                                                                         .build() )
                                                               .build() )
                                       .editor( edit -> {
                                       } ) );
        } );

        final Content targetContentWithThumbnail = syncUpdated( sourceContent.getId() );

        assertTrue( targetContentWithThumbnail.hasThumbnail() );

        sourceContext.runWith( () -> {
            contentService.update( new UpdateContentParams().contentId( sourceContent.getId() )
                                       .removeAttachments( BinaryReferences.from( AttachmentNames.THUMBNAIL ) )
                                       .editor( edit -> {
                                       } ) );
        } );

        final Content targetContentWithoutThumbnail = syncUpdated( sourceContent.getId() );

        assertFalse( targetContentWithoutThumbnail.hasThumbnail() );

    }

    @Test
    public void renameNotSynched()
        throws Exception
    {
        final Content targetContent = targetContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );

        assertThrows( IllegalArgumentException.class, () -> syncRenamed( targetContent.getId() ), "sourceContent must be set." );
    }

    @Test
    public void renameNotExisted()
        throws Exception
    {
        assertThrows( NullPointerException.class, () -> syncRenamed( ContentId.from( "123" ) ), "sourceContent must be set." );
    }

    @Test
    public void renameNotChanged()
        throws Exception
    {
        final Content sourceContent = sourceContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );

        final Content targetContent1 = syncCreated( sourceContent.getId() );
        final Content targetContent2 = syncRenamed( sourceContent.getId() );

        assertEquals( targetContent1, targetContent2 );
    }

    @Test
    public void renameChanged()
        throws Exception
    {
        final Content sourceContent = sourceContext.callWith( () -> createContent( ContentPath.ROOT ) );
        final Content targetContent = syncCreated( sourceContent.getId() );

        sourceContext.runWith( () -> contentService.rename(
            RenameContentParams.create().contentId( sourceContent.getId() ).newName( ContentName.from( "newName" ) ).build() ) );

        final Content targetContentRenamed = syncRenamed( sourceContent.getId() );

        assertNotEquals( targetContent.getName(), targetContentRenamed.getName() );
        assertEquals( "newName", targetContentRenamed.getName().toString() );
    }


    @Test
    public void sortNotExisted()
        throws Exception
    {
        assertThrows( NullPointerException.class, () -> syncSorted( ContentId.from( "source" ) ) );
    }

    @Test
    public void sortNotSynched()
        throws Exception
    {
        final Content sourceContent = sourceContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );
        assertThrows( IllegalArgumentException.class, () -> syncSorted( sourceContent.getId() ) );
    }

    @Test
    public void sortNotChanged()
        throws Exception
    {
        final Content sourceContent = sourceContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );
        final Content targetContent = syncCreated( sourceContent.getId() );

        syncSorted( sourceContent.getId() );
        assertEquals( targetContent, targetContext.callWith( () -> contentService.getById( sourceContent.getId() ) ) );
    }

    @Test
    public void sortChanged()
        throws Exception
    {
        final Content sourceParent = sourceContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );
        final Content targetParent = syncCreated( sourceParent.getId() );

        sourceContext.runWith( () -> contentService.setChildOrder( SetContentChildOrderParams.create()
                                                                       .contentId( sourceParent.getId() )
                                                                       .childOrder( ChildOrder.from( "modifiedTime ASC" ) )
                                                                       .build() ) );

        final Content targetContentSorted = syncSorted( sourceParent.getId() );

        assertNotEquals( targetParent.getChildOrder(), targetContentSorted.getChildOrder() );
    }


    @Test
    public void moveNotExisted()
        throws Exception
    {
        assertThrows( NullPointerException.class, () -> syncMoved( ContentId.from( "source" ) ), "sourceContent must be set." );
    }

    @Test
    public void moveNotSynched()
        throws Exception
    {
        final Content sourceContent = sourceContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );

        assertThrows( IllegalArgumentException.class, () -> syncMoved( sourceContent.getId() ), "sourceContent must be set." );
    }

    @Test
    public void moveNotChanged()
        throws Exception
    {
        final Content sourceContent = sourceContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );
        final Content targetContent = syncCreated( sourceContent.getId() );

        assertEquals( targetContent, syncMoved( sourceContent.getId() ) );

    }

    @Test
    public void moveChanged()
        throws Exception
    {
        final Content sourceContent1 = sourceContext.callWith( () -> createContent( ContentPath.ROOT, "name1" ) );
        final Content sourceContent2 = sourceContext.callWith( () -> createContent( ContentPath.ROOT, "name2" ) );

        final Content targetContent1 = syncCreated( sourceContent1.getId() );
        final Content targetContent2 = syncCreated( sourceContent2.getId() );

        assertEquals( "/name1", targetContent1.getPath().toString() );
        assertEquals( "/name2", targetContent2.getPath().toString() );

        sourceContext.runWith( () -> contentService.move(
            MoveContentParams.create().contentId( sourceContent1.getId() ).parentContentPath( sourceContent2.getPath() ).build() ) );

        final Content targetContentSorted = syncMoved( sourceContent1.getId() );

        assertEquals( "/name2/name1", targetContentSorted.getPath().toString() );
    }


    @Test
    public void deleteNotExisted()
        throws Exception
    {
        assertThrows( NullPointerException.class, () -> syncDeleted( ContentId.from( "source" ) ) );
    }

    @Test
    public void deleteNotSynched()
        throws Exception
    {
        final Content sourceContent = sourceContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );

        assertThrows( IllegalArgumentException.class, () -> syncDeleted( sourceContent.getId() ), "targetContent must be set." );
    }

    @Test
    public void deleteNotDeletedInParent()
        throws Exception
    {
        final Content sourceContent = sourceContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );
        final Content targetContent = syncCreated( sourceContent.getId() );

        assertThrows( IllegalArgumentException.class, () -> syncDeleted( targetContent.getId() ) );
    }

    @Test
    public void deleteDeletedInParent()
        throws Exception
    {
        final Content sourceContent = sourceContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );
        final Content targetContent = syncCreated( sourceContent.getId() );

        sourceContext.runWith(
            () -> contentService.deleteWithoutFetch( DeleteContentParams.create().contentPath( sourceContent.getPath() ).build() ) );

        assertTrue( syncDeleted( targetContent.getId() ) );
    }

    @Test
    public void syncDeletedInParent()
        throws Exception
    {
        final Content sourceContent = sourceContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );
        syncCreated( sourceContent.getId() );

        sourceContext.runWith(
            () -> contentService.deleteWithoutFetch( DeleteContentParams.create().contentPath( sourceContent.getPath() ).build() ) );

        refresh();

        sync( sourceContent.getId(), false );

        assertFalse( targetContext.callWith( () -> contentService.contentExists( sourceContent.getId() ) ) );
    }

    @Test
    public void syncWithChildren()
        throws Exception
    {
        final Content sourceContent = sourceContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );
        final Content sourceChild1 = sourceContext.callWith( () -> createContent( sourceContent.getPath(), "child1" ) );

        sync( sourceContent.getId(), true );

        assertTrue( targetContext.callWith( () -> contentService.contentExists( sourceChild1.getId() ) ) );

        final Content sourceChild1_1 = sourceContext.callWith( () -> createContent( sourceContent.getPath(), "child1_1" ) );

        sync( sourceContent.getId(), true );

        assertTrue( targetContext.callWith( () -> contentService.contentExists( sourceChild1_1.getId() ) ) );

        sourceContext.callWith( () -> contentService.move(
            MoveContentParams.create().contentId( sourceChild1_1.getId() ).parentContentPath( ContentPath.ROOT ).build() ) );
        sourceContext.callWith(
            () -> contentService.deleteWithoutFetch( DeleteContentParams.create().contentPath( sourceChild1.getPath() ).build() ) );
        sourceContext.callWith( () -> contentService.update(
            new UpdateContentParams().contentId( sourceContent.getId() ).editor( edit -> edit.data = new PropertyTree() ) ) );

        sync( null, true );

        refresh();

        assertEquals( "/child1_1", targetContext.callWith( () -> contentService.getById( sourceChild1_1.getId() ).getPath().toString() ) );
        assertFalse( targetContext.callWith( () -> contentService.contentExists( sourceChild1.getId() ) ) );

    }

    @Test
    public void syncWithoutChildren()
        throws Exception
    {
        final Content sourceContent = sourceContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );
        final Content sourceChild1 = sourceContext.callWith( () -> createContent( sourceContent.getPath(), "child1" ) );

        sync( sourceContent.getId(), false );

        assertTrue( targetContext.callWith( () -> contentService.contentExists( sourceContent.getId() ) ) );
        assertFalse( targetContext.callWith( () -> contentService.contentExists( sourceChild1.getId() ) ) );
    }

    @Test
    public void syncByRoot()
        throws Exception
    {
        final Content sourceContent = sourceContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );
        final Content sourceChild1 = sourceContext.callWith( () -> createContent( sourceContent.getPath(), "child1" ) );
        final Content sourceChild1_1 = sourceContext.callWith( () -> createContent( sourceContent.getPath(), "child1_1" ) );

        sync( null, false );

        assertTrue( targetContext.callWith( () -> contentService.contentExists( sourceContent.getId() ) ) );
        assertTrue( targetContext.callWith( () -> contentService.contentExists( sourceChild1.getId() ) ) );
        assertTrue( targetContext.callWith( () -> contentService.contentExists( sourceChild1_1.getId() ) ) );
    }


    @Test
    public void updateManualOrderNotExisted()
        throws Exception
    {
        assertThrows( NullPointerException.class, () -> syncManualOrderUpdated( ContentId.from( "source" ) ),
                      "sourceContent must be set." );
    }

    @Test
    public void updateManualOrderNotSynched()
        throws Exception
    {
        final Content sourceContent = sourceContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );

        assertThrows( IllegalArgumentException.class, () -> syncManualOrderUpdated( sourceContent.getId() ), "sourceContent must be set." );
    }

    @Test
    public void updateManualOrderNotChanged()
        throws Exception
    {
        final Content sourceContent = sourceContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );
        final Content targetContent = syncCreated( sourceContent.getId() );

        assertEquals( targetContent, syncManualOrderUpdated( sourceContent.getId() ) );

    }

    @Test
    public void updateManualOrderValue()
        throws Exception
    {
        final Content sourceParent = sourceContext.callWith( () -> createContent( ContentPath.ROOT ) );
        final Content sourceChild1 = sourceContext.callWith( () -> createContent( sourceParent.getPath(), "child1" ) );
        final Content sourceChild2 = sourceContext.callWith( () -> createContent( sourceParent.getPath(), "child2" ) );

        syncCreated( sourceParent.getId() );
        syncCreated( sourceChild1.getId() );
        syncCreated( sourceChild2.getId() );

        sourceContext.runWith( () -> {
            contentService.setChildOrder(
                SetContentChildOrderParams.create().contentId( sourceParent.getId() ).childOrder( ChildOrder.manualOrder() ).build() );

            assertTrue( syncSorted( sourceParent.getId() ).getChildOrder().isManualOrder() );

            contentService.reorderChildren( ReorderChildContentsParams.create()
                                                .contentId( sourceParent.getId() )
                                                .add( ReorderChildParams.create()
                                                          .contentToMove( sourceChild1.getId() )
                                                          .contentToMoveBefore( sourceChild2.getId() )
                                                          .build() )
                                                .build() );

            Long newManualOrderValue1 = syncManualOrderUpdated( sourceChild1.getId() ).getManualOrderValue();
            Long newManualOrderValue2 = syncManualOrderUpdated( sourceChild2.getId() ).getManualOrderValue();

            assertTrue( newManualOrderValue1 > newManualOrderValue2 );

            contentService.reorderChildren( ReorderChildContentsParams.create()
                                                .contentId( sourceParent.getId() )
                                                .add( ReorderChildParams.create()
                                                          .contentToMove( sourceChild2.getId() )
                                                          .contentToMoveBefore( sourceChild1.getId() )
                                                          .build() )
                                                .build() );

            newManualOrderValue1 = syncManualOrderUpdated( sourceChild1.getId() ).getManualOrderValue();
            newManualOrderValue2 = syncManualOrderUpdated( sourceChild2.getId() ).getManualOrderValue();

            assertTrue( newManualOrderValue1 < newManualOrderValue2 );
        } );

    }
}
