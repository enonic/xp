package com.enonic.xp.impl.server.rest;

import java.io.File;

import javax.ws.rs.core.MediaType;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.dump.BranchDumpResult;
import com.enonic.xp.dump.BranchLoadResult;
import com.enonic.xp.dump.DumpError;
import com.enonic.xp.dump.DumpService;
import com.enonic.xp.dump.LoadError;
import com.enonic.xp.dump.RepoDumpResult;
import com.enonic.xp.dump.RepoLoadResult;
import com.enonic.xp.dump.SystemDumpParams;
import com.enonic.xp.dump.SystemDumpResult;
import com.enonic.xp.dump.SystemLoadParams;
import com.enonic.xp.dump.SystemLoadResult;
import com.enonic.xp.dump.VersionsLoadResult;
import com.enonic.xp.export.ExportService;
import com.enonic.xp.export.ImportNodesParams;
import com.enonic.xp.export.NodeImportResult;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repository.NodeRepositoryService;
import com.enonic.xp.repository.Repositories;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.vacuum.VacuumParameters;
import com.enonic.xp.vacuum.VacuumResult;
import com.enonic.xp.vacuum.VacuumService;
import com.enonic.xp.vacuum.VacuumTaskResult;

import static org.mockito.Matchers.isA;

public class SystemResourceTest
    extends ServerRestTestSupport
{
    private ExportService exportService;

    private RepositoryService repositoryService;

    private NodeRepositoryService nodeRepositoryService;

    private DumpService dumpService;

    private VacuumService vacuumService;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();


    private File nameDir, dumpDir, dataDir;

    @Before
    public void setup()
        throws Exception
    {
        final File homeDir = this.temporaryFolder.newFolder( "home" );
        System.setProperty( "xp.home", homeDir.getAbsolutePath() );

        this.dataDir = createDir( homeDir, "data" );
        this.dumpDir = createDir( dataDir, "dump" );


    }

    private File createDir( final File dir, final String name )
    {
        final File file = new File( dir, name );
        Assert.assertTrue( "Failed to create directory " + name + " under " + dir.getAbsolutePath(), file.mkdirs() );
        return file;
    }

    @Test
    public void dump()
        throws Exception
    {
        final SystemDumpResult systemDumpResult = SystemDumpResult.create().
            add( RepoDumpResult.create( RepositoryId.from( "my-repo" ) ).versions( 3L ).
                add( BranchDumpResult.create( Branch.create().value( "branch-value" ).build() ).addedNodes( 3 ).error(
                    DumpError.error( "error-message" ) ).build() ).build() ).
            build();

        final SystemDumpParams params = SystemDumpParams.create().
            dumpName( "dump" ).
            includeBinaries( true ).
            includeVersions( true ).
            maxAge( 10 ).
            maxVersions( 20 ).
            build();

        Mockito.when( this.dumpService.dump( params ) ).thenReturn( systemDumpResult );

        final String result = request().path( "system/dump" ).
            entity( readFromFile( "dump_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "dump.json", result );
    }

    @Test
    public void load()
        throws Exception
    {
        this.nameDir = createDir( dumpDir, "name" );

        final NodeImportResult importResult = NodeImportResult.create().
            added( NodePath.create( "/path/to/node1" ).build() ).
            updated( NodePath.create( "/path/to/node2" ).build() ).
            dryRun( true ).
            build();

        Mockito.when( this.exportService.importNodes( isA( ImportNodesParams.class ) ) ).thenReturn( importResult );

        Mockito.when( this.repositoryService.list() ).thenReturn( Repositories.from( Repository.create().
            branches( Branch.from( "master" ) ).
            id( RepositoryId.from( "my-repo" ) ).
            build() ) );

        final File file = new File( this.nameDir, "export.properties" );
        Files.write( "a=b", file, Charsets.UTF_8 );

        SystemLoadParams params = SystemLoadParams.create().dumpName( "name" ).includeVersions( true ).build();

        SystemLoadResult systemLoadResult = SystemLoadResult.create().add( RepoLoadResult.create( RepositoryId.from( "my-repo" ) ).add(
            BranchLoadResult.create( Branch.create().value( "branch-value" ).build() ).error(
                LoadError.error( "error-message" ) ).successful( 2L ).build() ).versions(
            VersionsLoadResult.create().error( LoadError.error( "version-load-error-message" ) ).successful(
                1L ).build() ).build() ).build();

        Mockito.when( this.dumpService.load( params ) ).thenReturn( systemLoadResult );

        final String result = request().path( "system/load" ).
            entity( readFromFile( "load_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "load.json", result );
    }

    @Test
    public void load_system()
        throws Exception
    {
        this.nameDir = createDir( dumpDir, "name" );

        SystemLoadParams params = SystemLoadParams.create().dumpName( "name" ).includeVersions( true ).build();

        SystemLoadResult systemLoadResult = SystemLoadResult.create().add( RepoLoadResult.create( RepositoryId.from( "my-repo" ) ).add(
            BranchLoadResult.create( Branch.create().value( "branch-value" ).build() ).error(
                LoadError.error( "error-message" ) ).successful( 2L ).build() ).versions(
            VersionsLoadResult.create().error( LoadError.error( "version-load-error-message" ) ).successful(
                1L ).build() ).build() ).build();

        Mockito.when( this.dumpService.load( params ) ).thenReturn( systemLoadResult );

        final String result = request().path( "system/load" ).
            entity( readFromFile( "load_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "load_system.json", result );
    }

    @Test
    public void load_no_dump()
        throws Exception
    {
        expectedException.expect( IllegalArgumentException.class );
        expectedException.expectMessage( "No dump with name 'name' found in "+ dataDir.getPath() );

        request().path( "system/load" ).
            entity( readFromFile( "load_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();
    }

    @Test
    public void vacuum()
        throws Exception
    {
        VacuumResult vacuumResult = VacuumResult.create().add( VacuumTaskResult.create().
            deleted().deleted().failed().inUse().processed().
            taskName( "vacuum-task-name" ).build() ).build();

        Mockito.when( this.vacuumService.vacuum( Mockito.isA( VacuumParameters.class ) ) ).thenReturn(vacuumResult);

        final String result = request().path( "system/vacuum" ).
            entity( "{}", MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "vacuum.json", result );
    }

    @Override
    protected Object getResourceInstance()
    {
        this.exportService = Mockito.mock( ExportService.class );
        this.repositoryService = Mockito.mock( RepositoryService.class );
        this.nodeRepositoryService = Mockito.mock( NodeRepositoryService.class );
        this.dumpService = Mockito.mock( DumpService.class );
        this.vacuumService = Mockito.mock( VacuumService.class );

        final SystemResource resource = new SystemResource();
        resource.setExportService( exportService );
        resource.setRepositoryService( repositoryService );
        resource.setNodeRepositoryService( nodeRepositoryService );
        resource.setDumpService( dumpService );
        resource.setVacuumService( vacuumService );
        return resource;
    }
}
