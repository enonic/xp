package com.enonic.wem.admin.rest.resource.export;

import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.enonic.wem.admin.rest.resource.ResourceConstants;
import com.enonic.wem.api.export.ExportService;
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
    public Response exportNodes( @QueryParam("path") final String path )
        throws Exception
    {
        final NodePath nodePath = NodePath.newPath( path ).build();

        final NodeExportResult result = this.exportService.exportNodes( nodePath );

        final String uri = ServletRequestUrlHelper.createUriWithHost( "/" );
        return Response.temporaryRedirect( new URI( uri ) ).build();
    }

    @GET
    @Path("import")
    public Response importNodes( @QueryParam("importName") final String importName, @QueryParam("importRoot") final String importPath )
        throws Exception
    {
        final NodePath importRoot = NodePath.newPath( importPath ).build();

        final NodeImportResult nodeImportResult = this.exportService.importNodes( importName, importRoot );

        final String uri = ServletRequestUrlHelper.createUriWithHost( "/" );
        return Response.temporaryRedirect( new URI( uri ) ).build();
    }

    public void setExportService( final ExportService exportService )
    {
        this.exportService = exportService;
    }

}

