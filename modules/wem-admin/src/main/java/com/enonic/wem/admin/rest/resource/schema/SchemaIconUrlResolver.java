package com.enonic.wem.admin.rest.resource.schema;

import com.google.common.hash.Hashing;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.schema.Schema;
import com.enonic.wem.api.schema.SchemaKey;
import com.enonic.wem.core.web.servlet.ServletRequestUrlHelper;

public final class SchemaIconUrlResolver
{
    public static String resolve( final Schema schema )
    {
        final StringBuilder str = new StringBuilder( "/admin/rest/schema/icon/" );
        str.append( schema.getSchemaKey().toString() );

        final Icon icon = schema.getIcon();
        if ( ( icon != null ) && ( icon.toByteArray() != null ) )
        {
            str.append( "?hash=" ).append( Hashing.md5().hashBytes( icon.toByteArray() ).toString() );
        }

        return ServletRequestUrlHelper.createUri( str.toString() );
    }

    public static String resolve( final SchemaKey schemaKey, final Icon schemaIcon )
    {
        final StringBuilder str = new StringBuilder( "/admin/rest/schema/icon/" );
        str.append( schemaKey.toString() );

        if ( ( schemaIcon != null ) && ( schemaIcon.toByteArray() != null ) )
        {
            str.append( "?hash=" ).append( Hashing.md5().hashBytes( schemaIcon.toByteArray() ).toString() );
        }

        return ServletRequestUrlHelper.createUri( str.toString() );
    }
}
