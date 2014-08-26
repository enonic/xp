package com.enonic.wem.portal.url;

import com.enonic.wem.api.content.ContentPath;

public interface ImageUrlBuilder
{

    ImageUrlBuilder mode( String mode );

    ImageUrlBuilder workspace( String workspace );

    ImageUrlBuilder resourcePath( String path );

    ImageUrlBuilder contentPath( String contentPath );

    ImageUrlBuilder contentPath( ContentPath contentPath );

    ImageUrlBuilder filter( String... filters );

    ImageUrlBuilder background( String backgroundColor );

    ImageUrlBuilder quality( int quality );

}
