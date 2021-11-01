package com.enonic.xp.core.content;

import java.util.EnumSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.archive.ArchiveContentException;
import com.enonic.xp.archive.ArchiveContentListener;
import com.enonic.xp.archive.ArchiveContentParams;
import com.enonic.xp.archive.ArchiveContentsResult;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentAccessException;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.DeleteContentParams;
import com.enonic.xp.content.ImportContentParams;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

        assertEquals( 1, result.getArchivedContents().getSize() );
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

        refresh();

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

        refresh();

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

        archiveContext().runWith( () -> {
            assertThrows( ArchiveContentException.class,
                          () -> this.contentService.archive( ArchiveContentParams.create().contentId( content.getId() ).build() ) );
        } );
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
        this.contentService.publish(
            PushContentParams.create().contentIds( ContentIds.from( content.getId() ) ).target( ContentConstants.BRANCH_MASTER ).build() );

        refresh();

        authorizedMasterContext().runWith( () -> {
            assertEquals( content.getId(), this.contentService.getById( content.getId() ).getId() );
        } );

        this.contentService.archive( ArchiveContentParams.create().contentId( content.getId() ).build() );

        authorizedMasterContext().runWith( () -> {
            assertFalse( this.contentService.contentExists( content.getId() ) );
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
            .attribute( "contentRootPath", NodePath.create( "archive" ).build() )
            .build();
    }

/*
    private Context contextWithoutAdmin()
    {
        AuthenticationInfo authenticationInfo =
            AuthenticationInfo.create().principals( RoleKeys.AUTHENTICATED ).user( TEST_DEFAULT_USER ).build();

        return ContextBuilder.create()
            .branch( ContextAccessor.current().getBranch() )
            .repositoryId( ContextAccessor.current().getRepositoryId() )
            .authInfo( authenticationInfo )
            .build();
    }
*/

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
