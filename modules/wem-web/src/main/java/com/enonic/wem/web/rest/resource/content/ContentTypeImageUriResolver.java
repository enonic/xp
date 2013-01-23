package com.enonic.wem.web.rest.resource.content;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.enonic.wem.api.content.type.QualifiedContentTypeName;

public final class ContentTypeImageUriResolver
{
    public static String resolve( final QualifiedContentTypeName contentTypeName )
    {
        return ServletUriComponentsBuilder.fromCurrentContextPath().
            path( "/admin/rest/content-type/image/" ).
            path( String.valueOf( contentTypeName ) ).
            build().toString();
    }
}
