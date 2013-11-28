package com.enonic.wem.admin.rest.resource.upload;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.common.collect.Lists;

import com.enonic.wem.admin.json.JsonResult;
import com.enonic.wem.admin.rest.service.upload.UploadItem;
import com.enonic.wem.admin.rest.service.upload.UploadService;
import com.enonic.wem.core.servlet.MultipartHelper;

@Path("upload")
@Produces(MediaType.APPLICATION_JSON)
public final class UploadResource
{
    private UploadService uploadService;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public JsonResult upload( final HttpServletRequest req )
        throws Exception
    {
        final List<UploadItem> items = Lists.newArrayList();
        final Part part = req.getPart( "file" );

        if ( part != null )
        {
            upload( items, part );
        }

        return new UploadResult( items );
    }

    @GET
    @Path("{id}")
    public Response getUploadedContent( @PathParam("id") final String id )
        throws Exception
    {
        final UploadItem item = this.uploadService.getItem( id );
        if ( item == null )
        {
            throw new WebApplicationException( Response.Status.NOT_FOUND );
        }

        MediaType mediaType;
        try
        {
            mediaType = MediaType.valueOf( item.getMimeType() );
        }
        catch ( IllegalArgumentException e )
        {
            mediaType = MediaType.WILDCARD_TYPE;
        }
        return Response.ok( item.getFile(), mediaType ).build();
    }

    private void upload( final List<UploadItem> items, final Part part )
        throws Exception
    {
        final String name = MultipartHelper.getFileName( part );
        final String mediaType = part.getContentType();

        final UploadItem item = this.uploadService.upload( name, mediaType, part.getInputStream() );
        items.add( item );
    }

    @Inject
    public void setUploadService( final UploadService uploadService )
    {
        this.uploadService = uploadService;
    }
}
