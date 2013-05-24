package com.enonic.wem.admin.rest.resource.upload;

import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.common.collect.Lists;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;

import com.enonic.wem.admin.json.JsonResult;
import com.enonic.wem.admin.rest.service.upload.UploadItem;
import com.enonic.wem.admin.rest.service.upload.UploadService;

@Path("upload")
@Produces(MediaType.APPLICATION_JSON)

public final class UploadResource
{
    private UploadService uploadService;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public JsonResult upload( final FormDataMultiPart formDataMultiPart )
        throws Exception
    {
        final List<UploadItem> items = Lists.newArrayList();

        final List<FormDataBodyPart> fields = formDataMultiPart.getFields( "file" );
        if ( fields != null )
        {
            for ( FormDataBodyPart field : fields )
            {
                upload( items, field.getValueAs( InputStream.class ), field );
            }
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
            return null;
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

    private void upload( final List<UploadItem> items, final InputStream fileInputStream, final FormDataBodyPart formDataBodyPart )
        throws Exception
    {
        final String name = formDataBodyPart.getContentDisposition().getFileName();
        final String mediaType = formDataBodyPart.getMediaType().toString();

        final UploadItem item = this.uploadService.upload( name, mediaType, fileInputStream );
        items.add( item );
    }

    @Inject
    public void setUploadService( final UploadService uploadService )
    {
        this.uploadService = uploadService;
    }
}
