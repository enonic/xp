package com.enonic.wem.portal.url;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;

public interface ImageByIdUrlBuilder
{

    ImageByIdUrlBuilder mode( String mode );

    ImageByIdUrlBuilder workspace( String workspace );

    ImageByIdUrlBuilder resourcePath( String path );

    ImageByIdUrlBuilder contentPath( String contentPath );

    ImageByIdUrlBuilder contentPath( ContentPath contentPath );

    ImageByIdUrlBuilder imageContent( ContentId id );

    ImageByIdUrlBuilder imageContent( String id );

    ImageByIdUrlBuilder filter( String... filters );

    ImageByIdUrlBuilder background( String backgroundColor );

    ImageByIdUrlBuilder quality( int quality );

}
