package com.enonic.wem.web.rest.resource.space;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.enonic.wem.api.space.SpaceName;

public final class SpaceImageUriResolver
{

    public static String resolve( final SpaceName spaceName )
    {
        final String spaceValue = spaceName.toString();
        return ServletUriComponentsBuilder.fromCurrentContextPath().
            path( "/admin/rest/space/image/" ).
            path( String.valueOf( spaceValue ) ).
            build().toString();
    }

}
