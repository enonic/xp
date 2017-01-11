package com.enonic.xp.impl.server.rest;

import javax.ws.rs.core.MediaType;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.export.ExportError;
import com.enonic.xp.export.ExportNodesParams;
import com.enonic.xp.export.ExportService;
import com.enonic.xp.export.ImportNodesParams;
import com.enonic.xp.export.NodeExportResult;
import com.enonic.xp.export.NodeImportResult;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repository.NodeRepositoryService;
import com.enonic.xp.repository.Repositories;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.security.SystemConstants;

import static org.mockito.Matchers.isA;

public class SystemDumpResourceTest
    extends ServerRestTestSupport
{
    private ExportService exportService;

    private RepositoryService repositoryService;

    private NodeRepositoryService nodeRepositoryService;

    @BeforeClass
    public static void setHomeDir()
    {
        System.setProperty( "xp.home", "~" );
    }

    @AfterClass
    public static void unsetHomeDir()
    {
        System.clearProperty( "xp.home" );
    }


    @Test
    public void dump()
        throws Exception
    {
        final NodeExportResult exportResult = NodeExportResult.create().
            addNodePath( NodePath.create( "/path/to/node" ).build() ).
            addError( new ExportError( "Error" ) ).
            dryRun( true ).
            build();
        Mockito.when( this.exportService.exportNodes( isA( ExportNodesParams.class ) ) ).thenReturn( exportResult );

        Mockito.when( this.repositoryService.list() ).
            thenReturn( Repositories.from( SystemConstants.SYSTEM_REPO, ContentConstants.CONTENT_REPO ) );

        final String result = request().path( "system/dump" ).
            entity( readFromFile( "dump_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "dump.json", result );
    }

    @Ignore
    @Test
    public void load()
        throws Exception
    {
        final NodeImportResult importResult = NodeImportResult.create().
            added( NodePath.create( "/path/to/node1" ).build() ).
            updated( NodePath.create( "/path/to/node2" ).build() ).
            dryRun( true ).
            build();
        Mockito.when( this.exportService.importNodes( isA( ImportNodesParams.class ) ) ).thenReturn( importResult );

        final String result = request().path( "system/load" ).
            entity( readFromFile( "load_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "load.json", result );
    }

    @Override
    protected Object getResourceInstance()
    {
        this.exportService = Mockito.mock( ExportService.class );
        this.repositoryService = Mockito.mock( RepositoryService.class );
        this.nodeRepositoryService = Mockito.mock( NodeRepositoryService.class );

        final SystemDumpResource resource = new SystemDumpResource();
        resource.setExportService( exportService );
        resource.setRepositoryService( repositoryService );
        resource.setNodeRepositoryService( nodeRepositoryService );
        return resource;
    }
}
