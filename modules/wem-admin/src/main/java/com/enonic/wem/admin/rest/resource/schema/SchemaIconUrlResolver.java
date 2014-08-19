package com.enonic.wem.admin.rest.resource.schema;

import com.google.common.hash.Hashing;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.schema.Schema;
import com.enonic.wem.api.schema.SchemaName;
import com.enonic.wem.core.web.servlet.ServletRequestUrlHelper;

public final class SchemaIconUrlResolver
{
    private SchemaIconResolver schemaIconResolver;

    public SchemaIconUrlResolver( final SchemaIconResolver schemaIconResolver )
    {
        this.schemaIconResolver = schemaIconResolver;
    }

    public String resolve( final Schema schema )
    {
        final StringBuilder str = new StringBuilder( "/admin/rest/schema/icon/" );
        str.append( schema.getSchemaKey().toString() );
        final Icon icon = schemaIconResolver.resolveFromSchema( schema );
        if ( icon != null && icon.toByteArray() != null )
        {
            str.append( "?hash=" ).append( Hashing.md5().hashBytes( icon.toByteArray() ).toString() );
        }
        return ServletRequestUrlHelper.createUri( str.toString() );
    }

    public String resolve( final SchemaName schemaName )
    {
        final StringBuilder str = new StringBuilder( "/admin/rest/schema/icon/" );
        str.append( schemaName.toSchemaKey().toString() );
        final Icon icon = schemaIconResolver.resolveFromName( schemaName );
        if ( icon != null && icon.toByteArray() != null )
        {
            str.append( "?hash=" ).append( Hashing.md5().hashBytes( icon.toByteArray() ).toString() );
        }
        return ServletRequestUrlHelper.createUri( str.toString() );
    }
}
