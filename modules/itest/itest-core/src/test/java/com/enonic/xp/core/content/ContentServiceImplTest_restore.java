package com.enonic.xp.core.content;

import java.util.EnumSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.archive.ArchiveContentParams;
import com.enonic.xp.archive.RestoreContentException;
import com.enonic.xp.archive.RestoreContentListener;
import com.enonic.xp.archive.RestoreContentParams;
import com.enonic.xp.archive.RestoreContentsResult;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.DeleteContentParams;
import com.enonic.xp.content.ImportContentParams;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;

import static com.enonic.xp.content.ContentConstants.CONTENT_ROOT_PATH_ATTRIBUTE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContentServiceImplTest_restore
    extends AbstractContentServiceTest
{
    private TestListener listener;

    @BeforeEach
    void init()
    {
        listener = new TestListener();
    }

    @Test
    void restore_tree()
    {
        final Content parent = createContent( ContentPath.ROOT, "archive" );
        final Content child1 = createContent( parent.getPath(), "child1" );
        final Content child1_1 = createContent( child1.getPath(), "child1_1" );
        final Content child1_2 = createContent( child1.getPath(), "child2_1" );

        this.contentService.archive( ArchiveContentParams.create().contentId( parent.getId() ).build() );

        final RestoreContentsResult result = this.contentService.restore(
            RestoreContentParams.create().contentId( parent.getId() ).restoreContentListener( listener ).build() );

        assertEquals( 1, result.getRestoredContents().getSize() );
        assertEquals( 4, listener.restored );

        assertTrue( this.contentService.contentExists( child1.getId() ) );
        assertTrue( this.contentService.contentExists( child1.getPath() ) );
        assertTrue( this.contentService.contentExists( child1_1.getId() ) );
        assertTrue( this.contentService.contentExists( child1_1.getPath() ) );
        assertTrue( this.contentService.contentExists( child1_2.getId() ) );
        assertTrue( this.contentService.contentExists( child1_2.getPath() ) );

        archiveContext().runWith( () -> {
            assertFalse( this.contentService.contentExists( child1.getId() ) );
            assertFalse( this.contentService.contentExists( child1.getPath() ) );
            assertFalse( this.contentService.contentExists( child1_1.getId() ) );
            assertFalse( this.contentService.contentExists( child1_1.getPath() ) );
            assertFalse( this.contentService.contentExists( child1_2.getId() ) );
            assertFalse( this.contentService.contentExists( child1_2.getPath() ) );
        } );
    }

    @Test
    void restore_to_tree()
    {
        final Content parent = createContent( ContentPath.ROOT, "archive" );
        final Content child1 = createContent( parent.getPath(), "child1" );
        createContent( child1.getPath(), "child1_1" );
        createContent( child1.getPath(), "child2_1" );

        this.contentService.archive( ArchiveContentParams.create().contentId( child1.getId() ).build() );

        RestoreContentsResult result = this.contentService.restore(
            RestoreContentParams.create().contentId( child1.getId() ).restoreContentListener( listener ).build() );

        assertEquals( 3, listener.restored );
        assertEquals( "/archive", result.getParentPath().toString() );
    }

    @Test
    void restore_to_custom_path()
    {
        final Content parent = createContent( ContentPath.ROOT, "archive" );
        final Content target = createContent( ContentPath.ROOT, "target" );
        final Content child1 = createContent( parent.getPath(), "child1" );
        createContent( child1.getPath(), "child1_1" );
        createContent( child1.getPath(), "child2_1" );

        this.contentService.archive( ArchiveContentParams.create().contentId( parent.getId() ).build() );

        RestoreContentsResult result = this.contentService.restore(
            RestoreContentParams.create().contentId( parent.getId() ).restoreContentListener( listener ).path( target.getPath() ).build() );

        assertEquals( 4, listener.restored );
        assertEquals( "/target", result.getParentPath().toString() );
    }

    @Test
    void restore_to_custom_root_path()
    {
        final Content parent = createContent( ContentPath.ROOT, "archive" );
        final Content child1 = createContent( parent.getPath(), "child1" );
        createContent( child1.getPath(), "child1_1" );
        createContent( child1.getPath(), "child2_1" );

        this.contentService.archive( ArchiveContentParams.create().contentId( child1.getId() ).build() );

        RestoreContentsResult result = this.contentService.restore(
            RestoreContentParams.create().contentId( child1.getId() ).restoreContentListener( listener ).path( ContentPath.ROOT ).build() );

        assertEquals( 3, listener.restored );
        assertEquals( "/", result.getParentPath().toString() );
    }

    @Test
    void restore_to_custom_not_existed_path()
    {
        final Content parent = createContent( ContentPath.ROOT, "archive" );
        final Content child1 = createContent( parent.getPath(), "child1" );
        createContent( child1.getPath(), "child1_1" );
        createContent( child1.getPath(), "child2_1" );

        this.contentService.archive( ArchiveContentParams.create().contentId( child1.getId() ).build() );

        assertThrows( NodeNotFoundException.class, () -> this.contentService.restore( RestoreContentParams.create()
                                                                                          .contentId( child1.getId() )
                                                                                          .restoreContentListener( listener )
                                                                                          .path( ContentPath.from( "/target" ) )
                                                                                          .build() ) );
    }

    @Test
    void restore_to_not_existed_parent()
    {
        final Content parent = createContent( ContentPath.ROOT, "archive" );
        final Content child1 = createContent( parent.getPath(), "child1" );
        createContent( child1.getPath(), "child1_1" );
        createContent( child1.getPath(), "child2_1" );

        this.contentService.archive( ArchiveContentParams.create().contentId( child1.getId() ).build() );
        this.contentService.delete( DeleteContentParams.create().contentPath( parent.getPath() ).build() );

        final RestoreContentsResult result = this.contentService.restore(
            RestoreContentParams.create().contentId( child1.getId() ).restoreContentListener( listener ).build() );

        assertEquals( 3, listener.restored );
        assertEquals( ContentPath.ROOT, result.getParentPath() );
    }

    @Test
    void restore_not_archived()
    {
        final Content content = createContent( ContentPath.ROOT, "archive" );

        final RestoreContentException ex = assertThrows( RestoreContentException.class, () -> this.contentService.restore(
            RestoreContentParams.create().contentId( content.getId() ).restoreContentListener( listener ).build() ) );

        assertEquals( "/archive", ex.getPath().toString() );
    }

    @Test
    void restore_with_existed_name()
    {
        final Content content = createContent( ContentPath.ROOT, "archive" );
        this.contentService.archive( ArchiveContentParams.create().contentId( content.getId() ).build() );

        createContent( ContentPath.ROOT, "archive" );
        this.contentService.restore(
            RestoreContentParams.create().contentId( content.getId() ).restoreContentListener( listener ).build() );

        assertEquals( "archive-1", this.contentService.getById( content.getId() ).getName().toString() );

    }

    @Test
    void restore_not_content()
    {
        final Node node = this.nodeService.create(
            CreateNodeParams.create().name( "my-node" ).parent( ContentConstants.CONTENT_ROOT_PATH ).data( new PropertyTree() ).build() );

        assertThrows( ContentNotFoundException.class, () -> this.contentService.restore( RestoreContentParams.create()
                                                                                             .contentId(
                                                                                                 ContentId.from( node.id().toString() ) )
                                                                                             .restoreContentListener( listener )
                                                                                             .build() ) );
    }

    @Test
    void restore_inherited()
    {
        final Content content = createContent( ContentPath.ROOT, "content" );
        this.contentService.delete( DeleteContentParams.create().contentPath( content.getPath() ).build() );

        final ContentId importedId = archiveContext().callWith( () -> this.contentService.importContent( ImportContentParams.create()
                                                                                                             .importContent( content )
                                                                                                             .targetPath(
                                                                                                                 content.getPath() )
                                                                                                             .inherit( EnumSet.allOf(
                                                                                                                 ContentInheritType.class ) )
                                                                                                             .build() )
            .getContent()
            .getId() );

        this.contentService.restore( RestoreContentParams.create().contentId( importedId ).build() );

        final Set<ContentInheritType> inherit = this.contentService.getById( importedId ).getInherit();
        assertThat( inherit ).isEmpty();
    }

    @Test
    void restore_dont_stop_inherited()
    {
        final Content content = createContent( ContentPath.ROOT, "content" );
        this.contentService.delete( DeleteContentParams.create().contentPath( content.getPath() ).build() );

        final ContentId importedId = archiveContext().callWith( () -> this.contentService.importContent( ImportContentParams.create()
                                                                                                             .importContent( content )
                                                                                                             .targetPath( content.getPath() )
                                                                                                             .inherit( EnumSet.allOf(
                                                                                                                 ContentInheritType.class ) )
                                                                                                             .build() )
            .getContent()
            .getId() );

        this.contentService.restore( RestoreContentParams.create().contentId( importedId ).stopInherit( false ).build() );

        final Set<ContentInheritType> inherit = this.contentService.getById( importedId ).getInherit();
        assertEquals( 4, inherit.size() );
    }

    @Test
    void restore_check_properties()
    {
        final Content parent = createContent( ContentPath.ROOT, "archive" );
        final Content child = createContent( parent.getPath(), "content" );

        this.contentService.archive( ArchiveContentParams.create().contentId( parent.getId() ).build() );
        this.contentService.restore( RestoreContentParams.create().contentId( parent.getId() ).build() );

        final Content restoredParent = contentService.getById( parent.getId() );
        final Content restoredChild = contentService.getById( child.getId() );

        assertNull( restoredParent.getOriginalParentPath() );
        assertNull( restoredParent.getOriginalName() );
        assertNull( restoredParent.getArchivedTime() );
        assertNull( restoredParent.getArchivedBy() );

        assertNull( restoredChild.getOriginalParentPath() );
        assertNull( restoredChild.getOriginalName() );
        assertNull( restoredChild.getArchivedBy() );
    }

    private Context archiveContext()
    {
        return ContextBuilder.from( ContextAccessor.current() )
            .attribute( CONTENT_ROOT_PATH_ATTRIBUTE, new NodePath( "/archive" ) )
            .build();
    }

    private static final class TestListener
        implements RestoreContentListener
    {
        int restored = 0;

        @Override
        public void contentRestored( final int count )
        {
            restored++;
        }
    }
}
