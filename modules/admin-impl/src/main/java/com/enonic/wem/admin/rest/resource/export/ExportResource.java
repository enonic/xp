package com.enonic.wem.admin.rest.resource.export;

import java.nio.file.Paths;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.admin.AdminResource;
import com.enonic.wem.admin.rest.resource.ResourceConstants;
import com.enonic.wem.api.export.ExportNodesParams;
import com.enonic.wem.api.export.ExportService;
import com.enonic.wem.api.export.ImportNodesParams;
import com.enonic.wem.api.export.NodeExportResult;
import com.enonic.wem.api.export.NodeImportResult;
import com.enonic.wem.api.vfs.VirtualFiles;

@Path(ResourceConstants.REST_ROOT + "export")
@Produces(MediaType.APPLICATION_JSON)
@Component(immediate = true)
public class ExportResource
    implements AdminResource
{
    private ExportService exportService;

    private Logger LOG = LoggerFactory.getLogger( ExportResource.class );

    @POST
    @Path("export")
    public NodeExportResultJson exportNodes( final ExportNodesRequestJson request )
        throws Exception
    {
        final NodeExportResult result = this.exportService.exportNodes( ExportNodesParams.create().
            exportRoot( request.getExportRoot() ).
            exportName( request.getExportName() ).
            dryRun( request.isDryRun() ).
            includeNodeIds( request.isIncludeIds() ).
            build() );

        return NodeExportResultJson.from( result );
    }

    @POST
    @Path("import")
    public NodeImportResultJson importNodes( final ImportNodesRequestJson request )
        throws Exception
    {
        final NodeImportResult result = this.exportService.importNodes( ImportNodesParams.create().
            targetNodePath( request.getTargetNodePath() ).
            source( VirtualFiles.from( Paths.get( request.getExportFilePath() ) ) ).
            dryRun( request.isDryRun() ).
            includeNodeIds( request.isImportWithIds() ).
            build() );

        return NodeImportResultJson.from( result );
    }

    @SuppressWarnings("UnusedDeclaration")
    @Reference
    public void setExportService( final ExportService exportService )
    {
        this.exportService = exportService;
    }
}

