package com.enonic.xp.core.content;

import java.time.Instant;
import java.util.EnumSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.archive.ArchiveContentException;
import com.enonic.xp.archive.ArchiveContentListener;
import com.enonic.xp.archive.ArchiveContentParams;
import com.enonic.xp.archive.ArchiveContentsResult;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentAccessException;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.DeleteContentParams;
import com.enonic.xp.content.ImportContentParams;
import com.enonic.xp.content.MoveContentParams;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

import static com.enonic.xp.content.ContentConstants.CONTENT_ROOT_PATH_ATTRIBUTE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContentServiceImplTest_archive
    extends AbstractContentServiceTest
{
    private TestListener listener;

    @BeforeEach
    void init()
    {
        listener = new TestListener();
    }

    @Test
    public void archive_tree()
        throws Exception
    {
        final Content parent = createContent( ContentPath.ROOT, "archive" );
        final Content child1 = createContent( parent.getPath(), "child1" );
        final Content child1_1 = createContent( child1.getPath(), "child1_1" );
        final Content child1_2 = createContent( child1.getPath(), "child2_1" );

        final ArchiveContentParams params =
            ArchiveContentParams.create().contentId( parent.getId() ).archiveContentListener( listener ).build();

        final ArchiveContentsResult result = this.contentService.archive( params );

        assertEquals( 4, result.getArchivedContents().getSize() );
        assertEquals( 4, listener.getArchived() );

        assertFalse( this.contentService.contentExists( child1.getId() ) );
        assertFalse( this.contentService.contentExists( child1.getPath() ) );
        assertFalse( this.contentService.contentExists( child1_1.getId() ) );
        assertFalse( this.contentService.contentExists( child1_1.getPath() ) );
        assertFalse( this.contentService.contentExists( child1_2.getId() ) );
        assertFalse( this.contentService.contentExists( child1_2.getPath() ) );

        archiveContext().runWith( () -> {
            assertTrue( this.contentService.contentExists( child1.getId() ) );
            assertTrue( this.contentService.contentExists( child1.getPath() ) );
            assertTrue( this.contentService.contentExists( child1_1.getId() ) );
            assertTrue( this.contentService.contentExists( child1_1.getPath() ) );
            assertTrue( this.contentService.contentExists( child1_2.getId() ) );
            assertTrue( this.contentService.contentExists( child1_2.getPath() ) );
        } );
    }

    @Test
    public void archive_child()
        throws Exception
    {
        final Content parent = createContent( ContentPath.ROOT, "parent" );
        final Content child = createContent( parent.getPath(), "child" );

        final ArchiveContentParams params = ArchiveContentParams.create().contentId( child.getId() ).build();
        this.contentService.archive( params );

        assertTrue( this.contentService.contentExists( parent.getId() ) );
        assertFalse( this.contentService.contentExists( child.getPath() ) );
        assertFalse( this.contentService.contentExists( child.getId() ) );

        archiveContext().runWith( () -> {
            assertTrue( this.contentService.contentExists( child.getId() ) );
            assertTrue( this.contentService.contentExists( ContentPath.from( "/child" ) ) );
            assertFalse( this.contentService.contentExists( parent.getId() ) );
        } );
    }

    @Test
    public void archive_the_same_name()
        throws Exception
    {
        final Content content1 = createContent( ContentPath.ROOT, "content" );
        this.contentService.archive( ArchiveContentParams.create().contentId( content1.getId() ).build() );

        final Content content2 = createContent( ContentPath.ROOT, "content" );
        this.contentService.archive( ArchiveContentParams.create().contentId( content2.getId() ).build() );

        archiveContext().runWith( () -> {
            assertEquals( content1.getId(), this.contentService.getByPath( ContentPath.from( "/content" ) ).getId() );
            assertTrue( this.contentService.getById( content2.getId() ).getName().toString().matches( "content\\s(\\d{2}-){3}\\d{3}" ) );
        } );
    }

    @Test
    public void archive_inherited()
        throws Exception
    {
        final Content content = createContent( ContentPath.ROOT, "content" );
        this.contentService.deleteWithoutFetch( DeleteContentParams.create().contentPath( content.getPath() ).build() );

        this.contentService.importContent( ImportContentParams.create()
                                               .importContent( content )
                                               .targetPath( content.getPath() )
                                               .inherit( EnumSet.allOf( ContentInheritType.class ) )
                                               .build() );

        this.contentService.archive( ArchiveContentParams.create().contentId( content.getId() ).build() );

        archiveContext().runWith( () -> {
            final Content archived = this.contentService.getById( content.getId() );
            assertEquals( 2, archived.getInherit().size() );
            assertTrue( archived.getInherit().contains( ContentInheritType.SORT ) );
            assertTrue( archived.getInherit().contains( ContentInheritType.NAME ) );
        } );
    }

    @Test
    public void archive_dont_stop_inherited()
        throws Exception
    {
        final Content content = createContent( ContentPath.ROOT, "content" );
        this.contentService.deleteWithoutFetch( DeleteContentParams.create().contentPath( content.getPath() ).build() );

        this.contentService.importContent( ImportContentParams.create()
                                               .importContent( content )
                                               .targetPath( content.getPath() )
                                               .inherit( EnumSet.allOf( ContentInheritType.class ) )
                                               .build() );

        this.contentService.archive( ArchiveContentParams.create().contentId( content.getId() ).stopInherit( false ).build() );

        archiveContext().runWith( () -> {
            final Content archived = this.contentService.getById( content.getId() );
            assertEquals( 4, archived.getInherit().size() );
        } );
    }

    @Test
    public void archive_already_archived()
        throws Exception
    {
        final Content content = createContent( ContentPath.ROOT, "content" );
        this.contentService.archive( ArchiveContentParams.create().contentId( content.getId() ).build() );

        final ArchiveContentException ex = archiveContext().callWith( () -> assertThrows( ArchiveContentException.class,
                                                                                          () -> this.contentService.archive(
                                                                                              ArchiveContentParams.create()
                                                                                                  .contentId( content.getId() )
                                                                                                  .build() ) ) );

        assertEquals( "/content", ex.getPath().toString() );
    }

    @Test
    public void archive_with_long_name()
        throws Exception
    {
        final Content content = createContent( ContentPath.ROOT, "content-1234567890-1234567890-1234567890-1234567890-1234567890" );
        this.contentService.archive( ArchiveContentParams.create().contentId( content.getId() ).build() );

        archiveContext().runWith( () -> {
            final Content archived = this.contentService.getById( content.getId() );
            assertEquals( "content-1234567890-1234567890-1234567890-1234567890-1234567890", archived.getDisplayName() );
            assertEquals( "content-1234567890-1234567890-1234567890-...4567890", archived.getName().toString() );
            assertEquals( "content-1234567890-1234567890-1234567890-1234567890-1234567890", archived.getOriginalName().toString() );
            assertEquals( "/", archived.getOriginalParentPath().toString() );
        } );
    }

    @Test
    public void archive_published()
        throws Exception
    {
        final Content content = createContent( ContentPath.ROOT, "content" );
        this.contentService.publish( PushContentParams.create().contentIds( ContentIds.from( content.getId() ) ).build() );

        ctxMasterSu().runWith( () -> {
            assertEquals( content.getId(), this.contentService.getById( content.getId() ).getId() );
        } );

        this.contentService.archive( ArchiveContentParams.create().contentId( content.getId() ).build() );

        ctxMasterSu().runWith( () -> {
            assertFalse( this.contentService.contentExists( content.getId() ) );
        } );
    }

    @Test
    void archive_published_and_moved_is_unpublished()
    {
        final Content content1 = createContent( ContentPath.ROOT, "content1" );
        final Content content2 = createContent( ContentPath.ROOT, "content2" );

        this.contentService.publish( PushContentParams.create().contentIds( ContentIds.from( content2.getId() ) ).build() );

        this.contentService.move( MoveContentParams.create().contentId( content2.getId() ).parentContentPath( content1.getPath() ).build() );

        final ArchiveContentsResult archiveContentsResult =
            this.contentService.archive( ArchiveContentParams.create().contentId( content1.getId() ).build() );

        assertThat(archiveContentsResult.getUnpublishedContents()).containsExactly( content2.getId() );

        assertThat(archiveContentsResult.getArchivedContents()).containsExactly( content1.getId(), content2.getId() );

        ctxMasterSu().runWith( () -> {
            assertFalse( this.contentService.contentExists( content2.getId() ) );
        } );
    }

    @Test
    public void archive_without_modify_permission()
        throws Exception
    {
        final Content content = createContent( ContentPath.ROOT, "content", AccessControlList.create()
            .add( AccessControlEntry.create()
                      .principal( TEST_DEFAULT_USER.getKey() )
                      .allow( Permission.CREATE, Permission.READ, Permission.DELETE, Permission.PUBLISH )
                      .build() )
            .build() );

        assertThrows( ContentAccessException.class,
                      () -> this.contentService.archive( ArchiveContentParams.create().contentId( content.getId() ).build() ) );
    }

    private Context archiveContext()
    {
        return ContextBuilder.from( ContextAccessor.current() )
            .attribute( CONTENT_ROOT_PATH_ATTRIBUTE, new NodePath( "/archive" ) )
            .build();
    }

    @Test
    public void archive_check_properties()
        throws Exception
    {
        final Content parent = createContent( ContentPath.ROOT, "archive" );
        final Content child = createContent( parent.getPath(), "content" );

        final ArchiveContentParams params =
            ArchiveContentParams.create().contentId( parent.getId() ).archiveContentListener( listener ).build();

        this.contentService.archive( params );

        archiveContext().runWith( () -> {
            final Content archivedParent = contentService.getById( parent.getId() );

            assertEquals( "/", archivedParent.getOriginalParentPath().toString() );
            assertEquals( "archive", archivedParent.getOriginalName().toString() );
            assertTrue( archivedParent.getArchivedTime().isBefore( Instant.now() ) );
            assertEquals( "user:system:test-user", archivedParent.getArchivedBy().toString() );

            final Content archivedChild = contentService.getById( child.getId() );

            assertNull( archivedChild.getOriginalParentPath() );
            assertNull( archivedChild.getOriginalName() );
            assertTrue( archivedChild.getArchivedTime().isBefore( Instant.now() ) );
            assertEquals( "user:system:test-user", archivedParent.getArchivedBy().toString() );

        } );
    }

    private static final class TestListener
        implements ArchiveContentListener
    {

        private int total = 0;

        private int archived = 0;

        @Override
        public void contentArchived( final int count )
        {
            archived++;
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

        public int getArchived()
        {
            return archived;
        }

    }
}
