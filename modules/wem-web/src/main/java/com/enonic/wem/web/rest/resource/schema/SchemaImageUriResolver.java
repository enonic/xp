package com.enonic.wem.web.rest.resource.schema;

import com.enonic.wem.api.schema.SchemaKey;
import com.enonic.wem.api.schema.content.QualifiedContentTypeName;
import com.enonic.wem.web.servlet.ServletRequestUrlHelper;

public final class SchemaImageUriResolver
{

    public static String resolve( final SchemaKey schemaKey )
    {
        final String schemaValue = schemaKey.toString();
        return ServletRequestUrlHelper.createUrl( "/admin/rest/schema/image/" + schemaValue );
    }

    public static String resolve( final QualifiedContentTypeName qualifiedName )
    {
        final SchemaKey schemaKey = SchemaKey.from( qualifiedName );
        return resolve( schemaKey );
    }
}
