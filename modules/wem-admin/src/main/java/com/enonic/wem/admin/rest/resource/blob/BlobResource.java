package com.enonic.wem.admin.rest.resource.blob;

import java.time.Instant;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.fileupload.FileItem;

import com.google.common.collect.Lists;

import com.enonic.wem.admin.json.JsonResult;
import com.enonic.wem.admin.rest.multipart.MultipartForm;
import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.blob.BlobService;

@Path("blob")
@Produces(MediaType.APPLICATION_JSON)
public final class BlobResource
{
    private BlobService blobService;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("upload")
    public JsonResult upload( final MultipartForm form )
        throws Exception
    {
        try
        {
            final List<UploadItem> items = Lists.newArrayList();
            for ( final FileItem file : form )
            {
                upload( items, file );
            }

            return new UploadResult( items );
        }
        finally
        {
            form.delete();
        }
    }

    @GET
    @Path("{id}")
    public Response getUploadedContent( @PathParam("id") final String id, @QueryParam("mimeType") String mimeType )
        throws Exception
    {
        final Blob blob = blobService.get( new BlobKey( id ) );
        return Response.ok( blob.getStream(), mimeType ).build();
    }

    private void upload( final List<UploadItem> items, final FileItem file )
        throws Exception
    {
        if ( file.isFormField() )
        {
            return;
        }

        final String name = file.getName();
        final String mediaType = file.getContentType();

        final Blob blob = blobService.create( file.getInputStream() );
        final UploadItem item = UploadItem.newUploadItem().
            mimeType( mediaType ).
            size( blob.getLength() ).
            name( name ).
            uploadTime( Instant.now().toEpochMilli() ).
            blobKey( blob.getKey() ).
            build();

        items.add( item );
    }

    @Inject
    public void setBlobService( final BlobService blobService )
    {
        this.blobService = blobService;
    }
}
