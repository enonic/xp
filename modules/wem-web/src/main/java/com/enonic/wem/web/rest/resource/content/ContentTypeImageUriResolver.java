package com.enonic.wem.web.rest.resource.content;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.enonic.wem.api.content.type.ContentType;

public final class ContentTypeImageUriResolver
{
    public static String resolve( final ContentType contentType )
    {
        final String contentTypeName = contentType.getQualifiedName().toString();
        return ServletUriComponentsBuilder.fromCurrentContextPath().
            path( "/admin/rest/content-type/image/" ).
            path( contentTypeName ).
            build().toString();
    }
}
