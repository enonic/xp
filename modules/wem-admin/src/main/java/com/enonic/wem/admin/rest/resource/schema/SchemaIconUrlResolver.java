package com.enonic.wem.admin.rest.resource.schema;

import com.google.common.hash.Hashing;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.schema.Schema;
import com.enonic.wem.core.web.servlet.ServletRequestUrlHelper;

public final class SchemaIconUrlResolver
{
    public static String resolve( final Schema schema )
    {
        final StringBuilder str = new StringBuilder( "/admin/rest/schema/image/" );
        str.append( schema.getSchemaKey().toString() );

        final Icon icon = schema.getIcon();
        if ( ( icon != null ) && ( icon.toByteArray() != null ) )
        {
            str.append( "?hash=" ).append( Hashing.md5().hashBytes( icon.toByteArray() ).toString() );
        }

        return ServletRequestUrlHelper.createUri( str.toString() );
    }
}
