package com.enonic.xp.portal.impl.url2;

import com.enonic.xp.content.ContentService;

public class Main
{
    public static void main( String[] args )
    {
        ContentService contentService = null;

        MediaService mediaService = new MediaService( contentService );

        ImageMediaUrlParams params = new ImageMediaUrlParams();
        params.scale = "block-100-100";

        String url = mediaService.mediaImageUrl( params );

        System.out.println( url );
    }
}
