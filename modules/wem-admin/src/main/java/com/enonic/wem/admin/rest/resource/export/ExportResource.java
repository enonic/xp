package com.enonic.wem.admin.rest.resource.export;

import java.net.URI;
import java.nio.file.Paths;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.enonic.wem.admin.rest.resource.ResourceConstants;
import com.enonic.wem.api.export.ExportNodesParams;
import com.enonic.wem.api.export.ExportService;
import com.enonic.wem.api.export.ImportNodesParams;
import com.enonic.wem.api.export.NodeExportResult;
import com.enonic.wem.api.export.NodeImportResult;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.vfs.VirtualFiles;
import com.enonic.xp.web.jaxrs.JaxRsComponent;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;

@Path(ResourceConstants.REST_ROOT + "export")
public class ExportResource
    implements JaxRsComponent
{
    private ExportService exportService;

    @GET
    @Path("export")
    public Response exportNodes( @QueryParam("path") final String path, @QueryParam("name") final String name )
        throws Exception
    {
        final NodeExportResult result = this.exportService.exportNodes( ExportNodesParams.create().
            exportRoot( NodePath.newPath( path ).build() ).
            exportName( name ).
            build() );

        final String uri = ServletRequestUrlHelper.createUriWithHost( "/" );
        return Response.temporaryRedirect( new URI( uri ) ).build();
    }

    @GET
    @Path("import")
    public Response importNodes( @QueryParam("exportPath") final String exportName, @QueryParam("importRoot") final String importRoot )
        throws Exception
    {
        final NodeImportResult nodeImportResult = this.exportService.importNodes( ImportNodesParams.create().
            importRootPath( NodePath.newPath( importRoot ).build() ).
            exportRoot( VirtualFiles.from( Paths.get( exportName ) ) ).
            build() );

        final String uri = ServletRequestUrlHelper.createUriWithHost( "/" );
        return Response.temporaryRedirect( new URI( uri ) ).build();
    }

    public void setExportService( final ExportService exportService )
    {
        this.exportService = exportService;
    }

}

