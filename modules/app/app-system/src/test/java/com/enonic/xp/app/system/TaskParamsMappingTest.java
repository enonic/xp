package com.enonic.xp.app.system;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ProjectSyncParams;
import com.enonic.xp.content.SyncContentService;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.dump.DumpService;
import com.enonic.xp.dump.SystemDumpParams;
import com.enonic.xp.dump.SystemDumpResult;
import com.enonic.xp.dump.SystemLoadParams;
import com.enonic.xp.dump.SystemLoadResult;
import com.enonic.xp.export.ExportNodesParams;
import com.enonic.xp.export.ExportService;
import com.enonic.xp.export.ImportNodesParams;
import com.enonic.xp.export.NodeExportResult;
import com.enonic.xp.export.NodeImportResult;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.index.ReindexParams;
import com.enonic.xp.index.ReindexResult;
import com.enonic.xp.lib.common.PropertyTreeMapper;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.project.Projects;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryIds;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskProgressReporterContext;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.testing.ScriptTestSupport;
import com.enonic.xp.vacuum.VacuumParameters;
import com.enonic.xp.vacuum.VacuumResult;
import com.enonic.xp.vacuum.VacuumService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Drives every task script with a {@link PropertyTree} built the way the management endpoint builds it, converted by
 * the same {@link PropertyTreeMapper} the task runner uses. Guards the conversions that differ between the two sides:
 * a multi-valued property with one value arrives as a scalar, booleans arrive as booleans, longs as numbers.
 */
@ExtendWith(MockitoExtension.class)
class TaskParamsMappingTest
    extends ScriptTestSupport
{
    private static final ApplicationKey SYSTEM = ApplicationKey.from( "com.enonic.xp.app.system" );

    private static final TaskId TASK_ID = TaskId.from( "task" );

    @Mock
    private IndexService indexService;

    @Mock
    private RepositoryService repositoryService;

    @Mock
    private DumpService dumpService;

    @Mock
    private ExportService exportService;

    @Mock
    private ProjectService projectService;

    @Mock
    private SyncContentService syncContentService;

    @Mock
    private VacuumService vacuumService;

    @Mock
    private TaskService taskService;

    @Mock
    private ProgressReporter progressReporter;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();
        setAppKey( SYSTEM.toString() );
        addService( IndexService.class, indexService );
        addService( RepositoryService.class, repositoryService );
        addService( DumpService.class, dumpService );
        addService( ExportService.class, exportService );
        addService( ProjectService.class, projectService );
        addService( SyncContentService.class, syncContentService );
        addService( VacuumService.class, vacuumService );
        addService( TaskService.class, taskService );
    }

    @Test
    void reindex_singleBranch_and_booleans()
    {
        task( "reindex" );
        when( indexService.reindex( any() ) ).thenReturn( reindexResult() );

        final PropertyTree data = new PropertyTree();
        data.addString( "repository", "my-repo" );
        data.addStrings( "branches", List.of( "master" ) );
        data.addBoolean( "initialize", true );

        run( "reindex", data );

        final ArgumentCaptor<ReindexParams> captor = ArgumentCaptor.forClass( ReindexParams.class );
        verify( indexService ).reindex( captor.capture() );
        assertEquals( List.of( Branch.from( "master" ) ), List.copyOf( captor.getValue().getBranches().getSet() ) );
        assertTrue( captor.getValue().isInitialize() );
    }

    @Test
    void reindex_severalBranches_and_falseBoolean()
    {
        task( "reindex" );
        when( indexService.reindex( any() ) ).thenReturn( reindexResult() );

        final PropertyTree data = new PropertyTree();
        data.addString( "repository", "my-repo" );
        data.addStrings( "branches", List.of( "draft", "master" ) );
        data.addBoolean( "initialize", false );

        run( "reindex", data );

        final ArgumentCaptor<ReindexParams> captor = ArgumentCaptor.forClass( ReindexParams.class );
        verify( indexService ).reindex( captor.capture() );
        assertEquals( 2, captor.getValue().getBranches().getSize() );
        assertFalse( captor.getValue().isInitialize() );
    }

    @Test
    void projectSync_singleProject()
    {
        task( "project-sync" );
        final Project parent = Project.create().name( ProjectName.from( "parent" ) ).displayName( "p" ).build();
        final Project child = Project.create().name( ProjectName.from( "child" ) ).displayName( "c" ).addParent( parent.getName() ).build();
        when( projectService.list() ).thenReturn( Projects.create().addAll( List.of( parent, child ) ).build() );

        final PropertyTree data = new PropertyTree();
        data.addStrings( "projects", List.of( "child" ) );

        run( "project-sync", data );

        final ArgumentCaptor<ProjectSyncParams> captor = ArgumentCaptor.forClass( ProjectSyncParams.class );
        verify( syncContentService ).syncProject( captor.capture() );
        assertEquals( child.getName(), captor.getValue().getTargetProject() );
    }

    @Test
    void dump_singleRepository_longs_and_booleans()
    {
        task( "dump" );
        when( dumpService.dump( any() ) ).thenReturn( SystemDumpResult.create().build() );

        final PropertyTree data = new PropertyTree();
        data.addString( "name", "full" );
        data.addBoolean( "includeVersions", true );
        data.addLong( "maxAge", 30L );
        data.addLong( "maxVersions", 5L );
        data.addStrings( "repositories", List.of( "my-repo" ) );

        run( "dump", data );

        final ArgumentCaptor<SystemDumpParams> captor = ArgumentCaptor.forClass( SystemDumpParams.class );
        verify( dumpService ).dump( captor.capture() );
        assertEquals( "full", captor.getValue().getDumpName() );
        assertTrue( captor.getValue().isIncludeVersions() );
        assertEquals( 30, captor.getValue().getMaxAge() );
        assertEquals( 5, captor.getValue().getMaxVersions() );
        assertEquals( RepositoryIds.from( RepositoryId.from( "my-repo" ) ), captor.getValue().getRepositories() );
    }

    @Test
    void dump_noOptionalParams()
    {
        task( "dump" );
        when( dumpService.dump( any() ) ).thenReturn( SystemDumpResult.create().build() );

        final PropertyTree data = new PropertyTree();
        data.addString( "name", "full" );
        data.addBoolean( "includeVersions", false );

        run( "dump", data );

        final ArgumentCaptor<SystemDumpParams> captor = ArgumentCaptor.forClass( SystemDumpParams.class );
        verify( dumpService ).dump( captor.capture() );
        assertFalse( captor.getValue().isIncludeVersions() );
        assertEquals( null, captor.getValue().getMaxAge() );
        assertTrue( captor.getValue().getRepositories().isEmpty() );
    }

    @Test
    void load_singleRepository()
    {
        task( "load" );
        when( dumpService.load( any() ) ).thenReturn( SystemLoadResult.create().build() );

        final PropertyTree data = new PropertyTree();
        data.addString( "name", "full" );
        data.addBoolean( "upgrade", true );
        data.addStrings( "repositories", List.of( "my-repo" ) );

        run( "load", data );

        final ArgumentCaptor<SystemLoadParams> captor = ArgumentCaptor.forClass( SystemLoadParams.class );
        verify( dumpService ).load( captor.capture() );
        assertTrue( captor.getValue().isUpgrade() );
        assertEquals( RepositoryIds.from( RepositoryId.from( "my-repo" ) ), captor.getValue().getRepositories() );
    }

    @Test
    void export_batchSizeLong()
    {
        when( exportService.exportNodes( any() ) ).thenReturn( NodeExportResult.create().build() );

        final PropertyTree data = new PropertyTree();
        data.addString( "repository", "my-repo" );
        data.addString( "branch", "draft" );
        data.addString( "nodePath", "/content" );
        data.addString( "exportName", "nightly" );
        data.addLong( "batchSize", 50L );

        run( "export", data );

        final ArgumentCaptor<ExportNodesParams> captor = ArgumentCaptor.forClass( ExportNodesParams.class );
        verify( exportService ).exportNodes( captor.capture() );
        assertEquals( "nightly", captor.getValue().getExportName() );
        assertEquals( new NodePath( "/content" ), captor.getValue().getSourceNodePath() );
        assertEquals( 50, captor.getValue().getBatchSize() );
    }

    @Test
    void import_booleans()
    {
        when( exportService.importNodes( any() ) ).thenReturn( NodeImportResult.create().build() );

        final PropertyTree data = new PropertyTree();
        data.addString( "exportName", "nightly" );
        data.addString( "repository", "my-repo" );
        data.addString( "branch", "draft" );
        data.addString( "nodePath", "/content" );
        data.addBoolean( "importWithIds", false );
        data.addBoolean( "importWithPermissions", true );

        run( "import", data );

        final ArgumentCaptor<ImportNodesParams> captor = ArgumentCaptor.forClass( ImportNodesParams.class );
        verify( exportService ).importNodes( captor.capture() );
        assertEquals( "nightly", captor.getValue().getExportName() );
        assertFalse( captor.getValue().isImportNodeIds() );
        assertTrue( captor.getValue().isImportPermissions() );
    }

    @Test
    void vacuum_singleTask()
    {
        task( "vacuum" );
        when( vacuumService.vacuum( any() ) ).thenReturn( VacuumResult.create().build() );

        final PropertyTree data = new PropertyTree();
        data.addString( "ageThreshold", "PT48H" );
        data.addStrings( "tasks", List.of( "BinaryBlobVacuumTask" ) );

        run( "vacuum", data );

        final ArgumentCaptor<VacuumParameters> captor = ArgumentCaptor.forClass( VacuumParameters.class );
        verify( vacuumService ).vacuum( captor.capture() );
        assertEquals( List.of( "BinaryBlobVacuumTask" ), List.copyOf( captor.getValue().getTaskNames() ) );
    }

    private void run( final String task, final PropertyTree data )
    {
        final ResourceKey script = ResourceKey.from( SYSTEM, "/tasks/" + task + "/" + task + ".js" );
        TaskProgressReporterContext.withContext(
            ( id, reporter ) -> runScriptMethod( script, "run", new PropertyTreeMapper( data ), id.toString() ) ).run( TASK_ID, progressReporter );
    }

    private void task( final String name )
    {
        when( taskService.getTaskInfo( TASK_ID ) ).thenReturn( TaskInfo.create()
                                                                   .id( TASK_ID )
                                                                   .name( SYSTEM + ":" + name )
                                                                   .application( SYSTEM )
                                                                   .startTime( Instant.now() )
                                                                   .build() );
    }

    private static ReindexResult reindexResult()
    {
        return ReindexResult.create()
            .repositoryId( RepositoryId.from( "my-repo" ) )
            .startTime( Instant.EPOCH )
            .endTime( Instant.EPOCH )
            .duration( java.time.Duration.ZERO )
            .branches( com.enonic.xp.branch.Branches.from( Branch.from( "master" ) ) )
            .build();
    }
}
