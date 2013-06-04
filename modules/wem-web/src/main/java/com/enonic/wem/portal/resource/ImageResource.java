package com.enonic.wem.portal.resource;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MultivaluedMap;

import com.enonic.wem.portal.request.ImageRequest;
import com.enonic.wem.portal.service.ImageService;

public class ImageResource
    extends AbstractResource
{

    private ImageService imageService;

    @GET
    @Path("{key}")
    public String handleNamedAttachment( @PathParam("key") String key )
    {
        final ImageRequest imageRequest = getImageRequest();
        imageRequest.setKey( key );
        populateImageRequestParams( imageRequest );

        final String image = imageService.getImage( imageRequest );

        return image;
    }

    @GET
    @Path("{key}/label/{label}")
    public String handleIdAttachment( @PathParam("key") String key, @PathParam("label") String label )
    {
        final ImageRequest imageRequest = getImageRequest();
        imageRequest.setKey( key );
        imageRequest.setLabel( label );
        populateImageRequestParams( imageRequest );

        final String image = imageService.getImage( imageRequest );

        return image;
    }

    private void populateImageRequestParams( final ImageRequest imageRequest )
    {
        final MultivaluedMap<String, String> queyParameters = this.uriInfo.getQueryParameters();

        imageRequest.setFilter( getStringParameter( queyParameters, "_filter" ) );
        imageRequest.setFormat( getStringParameter( queyParameters, "_format" ) );
        imageRequest.setQuality( getStringParameter( queyParameters, "_quality" ) );
        imageRequest.setBackground( getStringParameter( queyParameters, "_background" ) );
    }

    private String getStringParameter( final MultivaluedMap<String, String> queryParameters, final String parameterName )
    {
        final List<String> parameterValue = queryParameters.get( parameterName );

        return parameterValue == null ? null : ( parameterValue.isEmpty() ? null : parameterValue.get( 0 ) );
    }

    private ImageRequest getImageRequest()
    {
        return this.resourceContext.getResource( ImageRequest.class );
    }

    @Inject
    public void setImageService( final ImageService imageService )
    {
        this.imageService = imageService;
    }
}
