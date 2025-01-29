package com.enonic.xp.portal.impl.url3;

import com.enonic.xp.portal.impl.url2.ImageMediaUrlParams;

public class Main
{
    public static void main( String[] args )
    {
        final MediaService mediaService = new MediaService(null);
        final ImageMediaUrlParams params = new ImageMediaUrlParams();
        params.id = "1233456";
        System.out.println( "ImageUrl: " + mediaService.imageMediaUrl( params ) );
        System.out.println( "AttachmentUrl: " + mediaService.attachmentMediaUrl( params ) );
    }
}
