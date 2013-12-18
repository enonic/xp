package com.enonic.wem.admin.rest.resource.blob;

import java.io.InputStream;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.joda.time.DateTime;

import com.google.common.collect.Lists;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;

import com.enonic.wem.admin.json.JsonResult;
import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.blob.CreateBlob;
import com.enonic.wem.api.command.content.blob.GetBlob;

@Path("blob")
@Produces(MediaType.APPLICATION_JSON)
public final class BlobResource
    extends AbstractResource
{
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("upload")
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
        final GetBlob getBlob = Commands.blob().get( new BlobKey( id ) );
        final Blob blob = client.execute( getBlob );
        MediaType mediaType;
        try
        {
            mediaType = MediaType.valueOf( "image/png" );
        }
        catch ( IllegalArgumentException e )
        {
            mediaType = MediaType.WILDCARD_TYPE;
        }
        return Response.ok( blob.getStream(), mediaType ).build();
    }

    private void upload( final List<UploadItem> items, final InputStream fileInputStream, final FormDataBodyPart formDataBodyPart )
        throws Exception
    {
        final String name = formDataBodyPart.getContentDisposition().getFileName();
        final String mediaType = formDataBodyPart.getMediaType().toString();

        final CreateBlob createBlob = Commands.blob().create( fileInputStream );
        final Blob blob = client.execute( createBlob );
        final UploadItem item = UploadItem.newUploadItem().
            mimeType( mediaType ).
            size( blob.getLength() ).
            name( name ).
            uploadTime( DateTime.now().getMillis() ).
            blobKey( blob.getKey() ).
            build();

        items.add( item );
    }

}
