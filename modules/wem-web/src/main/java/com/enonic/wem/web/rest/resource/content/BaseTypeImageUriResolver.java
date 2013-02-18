package com.enonic.wem.web.rest.resource.content;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.enonic.wem.api.content.BaseTypeKey;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;

public final class BaseTypeImageUriResolver
{

    public static String resolve( final BaseTypeKey baseTypeKey )
    {
        final String baseTypeValue = baseTypeKey.toString();
        return ServletUriComponentsBuilder.fromCurrentContextPath().
            path( "/admin/rest/basetype/image/" ).
            path( String.valueOf( baseTypeValue ) ).
            build().toString();
    }

    public static String resolve( final QualifiedContentTypeName contentTypeName )
    {
        final BaseTypeKey baseTypeKey = BaseTypeKey.from( contentTypeName );
        return resolve( baseTypeKey );
    }
}
