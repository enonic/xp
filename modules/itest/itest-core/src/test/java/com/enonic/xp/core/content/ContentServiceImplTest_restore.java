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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContentServiceImplTest_restore
    extends AbstractContentServiceTest
{
    private TestListener listener;

    @BeforeEach
    void init()
    {
        listener = new TestListener();
    }

    @Test
    public void restore_tree()
        throws Exception
    {
        final Content parent = createContent( ContentPath.ROOT, "archive" );
        final Content child1 = createContent( parent.getPath(), "child1" );
        final Content child1_1 = createContent( child1.getPath(), "child1_1" );
        final Content child1_2 = createContent( child1.getPath(), "child2_1" );

        this.contentService.archive( ArchiveContentParams.create().contentId( parent.getId() ).build() );

        final RestoreContentsResult result = this.contentService.restore(
            RestoreContentParams.create().contentId( parent.getId() ).restoreContentListener( listener ).build() );

        assertEquals( 1, result.getRestoredContents().getSize() );
        assertEquals( 4, listener.getRestored() );

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
    public void restore_to_tree()
        throws Exception
    {
        final Content parent = createContent( ContentPath.ROOT, "archive" );
        final Content child1 = createContent( parent.getPath(), "child1" );
        createContent( child1.getPath(), "child1_1" );
        createContent( child1.getPath(), "child2_1" );

        this.contentService.archive( ArchiveContentParams.create().contentId( child1.getId() ).build() );

        RestoreContentsResult result = this.contentService.restore(
            RestoreContentParams.create().contentId( child1.getId() ).restoreContentListener( listener ).build() );

        assertEquals( 3, listener.getRestored() );
        assertEquals( "/archive", result.getParentPath().toString() );
    }

    @Test
    public void restore_to_custom_path()
        throws Exception
    {
        final Content parent = createContent( ContentPath.ROOT, "archive" );
        final Content target = createContent( ContentPath.ROOT, "target" );
        final Content child1 = createContent( parent.getPath(), "child1" );
        createContent( child1.getPath(), "child1_1" );
        createContent( child1.getPath(), "child2_1" );

        this.contentService.archive( ArchiveContentParams.create().contentId( parent.getId() ).build() );

        RestoreContentsResult result = this.contentService.restore(
            RestoreContentParams.create().contentId( parent.getId() ).restoreContentListener( listener ).path( target.getPath() ).build() );

        assertEquals( 4, listener.getRestored() );
        assertEquals( "/target", result.getParentPath().toString() );
    }

    @Test
    public void restore_to_custom_root_path()
        throws Exception
    {
        final Content parent = createContent( ContentPath.ROOT, "archive" );
        final Content child1 = createContent( parent.getPath(), "child1" );
        createContent( child1.getPath(), "child1_1" );
        createContent( child1.getPath(), "child2_1" );

        this.contentService.archive( ArchiveContentParams.create().contentId( child1.getId() ).build() );

        RestoreContentsResult result = this.contentService.restore(
            RestoreContentParams.create().contentId( child1.getId() ).restoreContentListener( listener ).path( ContentPath.ROOT ).build() );

        assertEquals( 3, listener.getRestored() );
        assertEquals( "/", result.getParentPath().toString() );
    }

    @Test
    public void restore_to_custom_not_existed_path()
        throws Exception
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
    public void restore_to_not_existed_parent()
        throws Exception
    {
        final Content parent = createContent( ContentPath.ROOT, "archive" );
        final Content child1 = createContent( parent.getPath(), "child1" );
        createContent( child1.getPath(), "child1_1" );
        createContent( child1.getPath(), "child2_1" );

        this.contentService.archive( ArchiveContentParams.create().contentId( child1.getId() ).build() );
        this.contentService.deleteWithoutFetch( DeleteContentParams.create().contentPath( parent.getPath() ).build() );

        final RestoreContentsResult result = this.contentService.restore(
            RestoreContentParams.create().contentId( child1.getId() ).restoreContentListener( listener ).build() );

        assertEquals( 3, listener.getRestored() );
        assertEquals( ContentPath.ROOT, result.getParentPath() );
    }

    @Test
    public void restore_not_archived()
        throws Exception
    {
        final Content content = createContent( ContentPath.ROOT, "archive" );

        assertThrows( RestoreContentException.class, () -> this.contentService.restore(
            RestoreContentParams.create().contentId( content.getId() ).restoreContentListener( listener ).build() ) );
    }

    @Test
    public void restore_with_existed_name()
        throws Exception
    {
        final Content content = createContent( ContentPath.ROOT, "archive" );
        this.contentService.archive( ArchiveContentParams.create().contentId( content.getId() ).build() );

        createContent( ContentPath.ROOT, "archive" );
        this.contentService.restore(
            RestoreContentParams.create().contentId( content.getId() ).restoreContentListener( listener ).build() );

        assertEquals( "archive-1", this.contentService.getById( content.getId() ).getName().toString() );

    }

    @Test
    public void restore_not_content()
        throws Exception
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
    public void restore_inherited()
    {
        final Content content = createContent( ContentPath.ROOT, "content" );
        this.contentService.deleteWithoutFetch( DeleteContentParams.create().contentPath( content.getPath() ).build() );

        refresh();

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
        assertEquals( 2, inherit.size() );
        assertTrue( inherit.contains( ContentInheritType.SORT ) );
        assertTrue( inherit.contains( ContentInheritType.NAME ) );

    }

    @Test
    public void restore_dont_stop_inherited()
    {
        final Content content = createContent( ContentPath.ROOT, "content" );
        this.contentService.deleteWithoutFetch( DeleteContentParams.create().contentPath( content.getPath() ).build() );

        refresh();

        final ContentId importedId = archiveContext().callWith( () -> this.contentService.importContent( ImportContentParams.create()
                                                                                                             .importContent( content )
                                                                                                             .targetPath(
                                                                                                                 content.getPath() )
                                                                                                             .inherit( EnumSet.allOf(
                                                                                                                 ContentInheritType.class ) )
                                                                                                             .build() )
            .getContent()
            .getId() );

        this.contentService.restore( RestoreContentParams.create().contentId( importedId ).stopInherit( false ).build() );

        final Set<ContentInheritType> inherit = this.contentService.getById( importedId ).getInherit();
        assertEquals( 4, inherit.size() );

    }

    private Context archiveContext()
    {
        return ContextBuilder.from( ContextAccessor.current() )
            .attribute( "contentRootPath", NodePath.create( "archive" ).build() )
            .build();
    }

    private static final class TestListener
        implements RestoreContentListener
    {

        private int total = 0;

        private int restored = 0;

        @Override
        public void contentRestored( final int count )
        {
            restored++;
        }

        public int getTotal()
        {
            return total;
        }

        @Override
        public void setTotal( final int count )
        {
            total++;
        }

        public int getRestored()
        {
            return restored;
        }

    }
}
