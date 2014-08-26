package com.enonic.wem.portal.url;

import com.enonic.wem.api.content.ContentPath;

public interface ServiceUrlBuilder
{

    ServiceUrlBuilder mode( String mode );

    ServiceUrlBuilder contentPath( String contentPath );

    ServiceUrlBuilder contentPath( ContentPath contentPath );

    ServiceUrlBuilder module( String module );

    ServiceUrlBuilder serviceName( String name );

    ServiceUrlBuilder param( String name, Object value );

}
