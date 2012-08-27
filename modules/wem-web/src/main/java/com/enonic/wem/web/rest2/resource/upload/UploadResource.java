package com.enonic.wem.web.rest2.resource.upload;

import java.io.InputStream;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.sun.jersey.multipart.FormDataParam;
import com.sun.jersey.multipart.file.StreamDataBodyPart;

import com.enonic.wem.web.rest2.service.upload.UploadItem;
import com.enonic.wem.web.rest2.service.upload.UploadService;

@Path("upload")
@Produces(MediaType.APPLICATION_JSON)
@Component
public final class UploadResource
{
    private UploadService uploadService;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public UploadResult upload( final @FormDataParam("file") List<StreamDataBodyPart> parts )
        throws Exception
    {
        final List<UploadItem> items = Lists.newArrayList();

        if ( parts != null )
        {
            for ( final StreamDataBodyPart part : parts )
            {
                upload( items, part );
            }
        }

        return new UploadResult( items );
    }

    private void upload( final List<UploadItem> items, final StreamDataBodyPart part )
        throws Exception
    {
        final String name = part.getFilename();
        final String mediaType = part.getMediaType().toString();
        final InputStream in = part.getStreamEntity();

        final UploadItem item = this.uploadService.upload( name, mediaType, in );
        items.add( item );
    }

    @Autowired
    public void setUploadService( final UploadService uploadService )
    {
        this.uploadService = uploadService;
    }
}
