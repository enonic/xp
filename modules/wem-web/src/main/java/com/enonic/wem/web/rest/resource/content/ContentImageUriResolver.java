package com.enonic.wem.web.rest.resource.content;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.enonic.wem.api.content.Content;

public final class ContentImageUriResolver
{

    public static String resolve( final Content content )
    {
        final String contentId = content.getId().toString();
        return ServletUriComponentsBuilder.fromCurrentContextPath().
            path( "/admin/rest/content/image/" ).
            path( contentId ).
            build().
            toString();
    }

}
