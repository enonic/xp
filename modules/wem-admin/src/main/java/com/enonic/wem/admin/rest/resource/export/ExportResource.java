package com.enonic.wem.admin.rest.resource.export;

import java.net.URI;

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
import com.enonic.wem.servlet.ServletRequestUrlHelper;
import com.enonic.wem.servlet.jaxrs.JaxRsComponent;

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
    public Response importNodes( @QueryParam("exportName") final String exportName, @QueryParam("importRoot") final String importRoot )
        throws Exception
    {
        final NodeImportResult nodeImportResult = this.exportService.importNodes( ImportNodesParams.create().
            exportName( exportName ).
            importRootPath( NodePath.newPath( importRoot ).build() ).
            build() );

        final String uri = ServletRequestUrlHelper.createUriWithHost( "/" );
        return Response.temporaryRedirect( new URI( uri ) ).build();
    }

    public void setExportService( final ExportService exportService )
    {
        this.exportService = exportService;
    }

}

