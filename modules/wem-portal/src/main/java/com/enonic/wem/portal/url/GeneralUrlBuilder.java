package com.enonic.wem.portal.url;

import java.util.Map;

import com.enonic.wem.api.content.ContentPath;

public interface GeneralUrlBuilder
{

    GeneralUrlBuilder mode( String mode );

    GeneralUrlBuilder workspace( String workspace );

    GeneralUrlBuilder resourcePath( String path );

    GeneralUrlBuilder resourceType( String resourceType );

    GeneralUrlBuilder params( Map<String, Object> params );

    GeneralUrlBuilder param( String name, Object value );

    GeneralUrlBuilder contentPath( String contentPath );

    GeneralUrlBuilder contentPath( ContentPath contentPath );

    GeneralUrlBuilder module( String module );

}
