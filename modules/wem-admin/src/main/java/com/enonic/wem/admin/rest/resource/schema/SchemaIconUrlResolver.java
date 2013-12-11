package com.enonic.wem.admin.rest.resource.schema;

import com.enonic.wem.api.schema.SchemaKey;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.web.servlet.ServletRequestUrlHelper;

public final class SchemaIconUrlResolver
{

    public static String resolve( final SchemaKey schemaKey )
    {
        return ServletRequestUrlHelper.createUrl( "/admin/rest/schema/image/" + schemaKey.toString() );
    }

    public static String resolve( final ContentTypeName contentTypeName )
    {
        return resolve( SchemaKey.from( contentTypeName ) );
    }
}
