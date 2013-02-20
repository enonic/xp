package com.enonic.wem.web.rest.resource.content.schema;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.enonic.wem.api.content.schema.SchemaKey;
import com.enonic.wem.api.content.schema.type.QualifiedContentTypeName;

public final class SchemaImageUriResolver
{

    public static String resolve( final SchemaKey schemaKey )
    {
        final String baseTypeValue = schemaKey.toString();
        return ServletUriComponentsBuilder.fromCurrentContextPath().
            path( "/admin/rest/schema/image/" ).
            path( String.valueOf( baseTypeValue ) ).
            build().toString();
    }

    public static String resolve( final QualifiedContentTypeName contentTypeName )
    {
        final SchemaKey schemaKey = SchemaKey.from( contentTypeName );
        return resolve( schemaKey );
    }
}
