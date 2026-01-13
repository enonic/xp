package com.enonic.xp.core.content;

import java.util.EnumSet;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.archive.ArchiveContentParams;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.DeleteContentParams;
import com.enonic.xp.content.MoveContentParams;
import com.enonic.xp.content.ProjectSyncParams;
import com.enonic.xp.content.ResetContentInheritParams;
import com.enonic.xp.content.SortContentParams;
import com.enonic.xp.content.SortContentResult;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.UpdateWorkflowParams;
import com.enonic.xp.content.WorkflowInfo;
import com.enonic.xp.content.WorkflowState;
import com.enonic.xp.core.impl.content.ContentEventsSyncParams;
import com.enonic.xp.core.impl.content.ContentSyncEventType;
import com.enonic.xp.core.impl.content.ParentContentSynchronizer;
import com.enonic.xp.core.impl.content.SyncContentServiceImpl;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.project.ProjectName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SyncContentServiceImplTest
    extends AbstractContentSynchronizerTest
{
    private ParentContentSynchronizer synchronizer;

    private SyncContentServiceImpl syncContentService;

    @BeforeEach
    void setUp()
    {
        synchronizer = new ParentContentSynchronizer( layersContentService );

        syncContentService =
            new SyncContentServiceImpl( contentTypeService, nodeService, eventPublisher, projectService, contentService, synchronizer,
                                        contentAuditLogSupport );
    }

    @Test
    void testRestoreSort()
    {
        final Content source = projectContext.callWith( () -> createContent( ContentPath.ROOT ) );

        syncCreated( source.getId() );

        layerContext.runWith( () -> {
            final SortContentResult changed = contentService.sort(
                SortContentParams.create().contentId( source.getId() ).childOrder( ChildOrder.from( "_name ASC" ) ).build() );

            assertFalse( changed.getContent().getInherit().contains( ContentInheritType.SORT ) );
        } );

        syncContentService.resetInheritance( ResetContentInheritParams.create()
                                                 .contentId( source.getId() )
                                                 .inherit( EnumSet.of( ContentInheritType.SORT ) )
                                                 .projectName( layer.getName() )
                                                 .build() );

        layerContext.runWith( () -> {
            final Content changed = contentService.getById( source.getId() );

            assertTrue( changed.getInherit().contains( ContentInheritType.SORT ) );
            assertEquals( ChildOrder.from( "modifiedtime DESC" ), changed.getChildOrder() );
        } );

    }

    @Test
    void testWorkflowInfo()
    {
        final Content source = projectContext.callWith( () -> createContent( ContentPath.ROOT ) );

        syncCreated( source.getId() );

        layerContext.runWith( () -> contentService.updateWorkflow(
            UpdateWorkflowParams.create().contentId( source.getId() ).editor( edit -> edit.state = WorkflowState.READY ).build() ) );

        syncContentService.resetInheritance( ResetContentInheritParams.create()
                                                 .contentId( source.getId() )
                                                 .inherit( EnumSet.of( ContentInheritType.CONTENT ) )
                                                 .projectName( layer.getName() )
                                                 .build() );

        layerContext.runWith( () -> {
            final Content changed = contentService.getById( source.getId() );

            assertTrue( changed.getInherit().contains( ContentInheritType.CONTENT ) );
            assertTrue( changed.getData().hasProperty( "stringField" ) );
        } );

    }

    @Test
    void resetWithRemovedSource()
    {
        final Content source = projectContext.callWith( () -> createContent( ContentPath.ROOT ) );

        syncCreated( source.getId() );

        layerContext.runWith( () -> contentService.updateWorkflow(
            UpdateWorkflowParams.create().contentId( source.getId() ).editor( edit -> edit.state = WorkflowState.READY ).build() ) );

        projectContext.runWith( () -> contentService.delete( DeleteContentParams.create().contentPath( source.getPath() ).build() ) );

        assertThrows( IllegalArgumentException.class, () -> syncContentService.resetInheritance( ResetContentInheritParams.create()
                                                                                                     .contentId( source.getId() )
                                                                                                     .inherit( EnumSet.of(
                                                                                                         ContentInheritType.CONTENT ) )
                                                                                                     .projectName( layer.getName() )
                                                                                                     .build() ) );
    }

    @Test
    void testSyncProject()
    {
        final Content missedParent = projectContext.callWith( () -> createContent( ContentPath.ROOT ) );
        final Content missedChild = projectContext.callWith( () -> createContent( missedParent.getPath() ) );

        layerContext.runWith( () -> {
            final ContentId id1 = missedParent.getId();
            assertFalse( layersContentService.getById( id1 ).isPresent() );
            final ContentId id = missedChild.getId();
            assertFalse( layersContentService.getById( id ).isPresent() );
        } );

        projectContext.runWith( () -> syncContentService.syncProject(
            ProjectSyncParams.create().targetProject( ProjectName.from( layerContext.getRepositoryId() ) ).build() ) );

        layerContext.runWith( () -> {
            final ContentId id1 = missedParent.getId();
            assertTrue( layersContentService.getById( id1 ).isPresent() );
            final ContentId id = missedChild.getId();
            assertTrue( layersContentService.getById( id ).isPresent() );
        } );

    }

    @Test
    void testSyncProjectFromTwoParents()
    {
        final Content missedContent1 = projectContext.callWith( () -> createContent( ContentPath.ROOT ) );
        final Content missedContent2 = secondProjectContext.callWith( () -> createContent( ContentPath.ROOT ) );

        layerContext.runWith( () -> {
            final ContentId id1 = missedContent1.getId();
            assertFalse( layersContentService.getById( id1 ).isPresent() );
            final ContentId id = missedContent2.getId();
            assertFalse( layersContentService.getById( id ).isPresent() );
        } );

        projectContext.runWith( () -> syncContentService.syncProject(
            ProjectSyncParams.create().targetProject( ProjectName.from( layerContext.getRepositoryId() ) ).build() ) );

        layerContext.runWith( () -> {
            final ContentId id1 = missedContent1.getId();
            assertTrue( layersContentService.getById( id1 ).isPresent() );
            final ContentId id = missedContent2.getId();
            assertTrue( layersContentService.getById( id ).isPresent() );
        } );

    }

    @Test
    void testMoveArchiveAndResetFromTwoParents()
    {
        final Content sourceContent1 = projectContext.callWith( () -> createContent( ContentPath.ROOT ) );
        final Content sourceContent2 = secondProjectContext.callWith( () -> createContent( ContentPath.ROOT ) );

        projectContext.runWith( () -> syncContentService.syncProject(
            ProjectSyncParams.create().targetProject( ProjectName.from( layerContext.getRepositoryId() ) ).build() ) );

        layerContext.runWith( () -> contentService.move(
            MoveContentParams.create().contentId( sourceContent2.getId() ).parentContentPath( sourceContent1.getPath() ).build() ) );

        projectContext.runWith( () -> {
            contentService.archive( ArchiveContentParams.create().contentId( sourceContent1.getId() ).build() );

            syncContentService.syncProject(
                ProjectSyncParams.create().targetProject( ProjectName.from( layerContext.getRepositoryId() ) ).build() );
        } );

        layerArchiveContext.runWith( () -> {
            final ContentId id1 = sourceContent1.getId();
            assertTrue( layersContentService.getById( id1 ).isPresent() );
            final ContentId id = sourceContent2.getId();
            assertTrue( layersContentService.getById( id ).isPresent() );
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

        return layerContext.callWith( () -> layersContentService.getById( contentId ) );
    }
}
