package com.enonic.wem.portal.service;

import javax.inject.Inject;

import com.enonic.wem.portal.request.ImageRequest;

public class ImageServiceImpl
    extends AbstractPortalService
    implements ImageService
{

    private ImageService imageService;

    @Override
    public String getImage( final ImageRequest imageRequest )
    {

        return imageRequest.toString();
    }


    @Inject
    public void setImageService( final ImageService imageService )
    {
        this.imageService = imageService;
    }
}
