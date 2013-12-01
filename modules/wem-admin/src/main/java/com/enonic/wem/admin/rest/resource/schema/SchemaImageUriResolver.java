package com.enonic.wem.admin.rest.resource.schema;

import com.enonic.wem.api.schema.SchemaKey;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.core.servlet.ServletRequestUrlHelper;

public final class SchemaImageUriResolver
{

    public static String resolve( final SchemaKey schemaKey )
    {
        final String schemaValue = schemaKey.toString();
        return ServletRequestUrlHelper.createUrl( "/admin/rest/schema/image/" + schemaValue );
    }

    public static String resolve( final ContentTypeName contentTypeName )
    {
        final SchemaKey schemaKey = SchemaKey.from( contentTypeName );
        return resolve( schemaKey );
    }
}
