package com.enonic.wem.api.schema;


import com.enonic.wem.api.Name;

public class SchemaName extends Name
{
    public SchemaName( final String name )
    {
        super( name );
    }

    public static SchemaName from( final String mixinName )
    {
        return new SchemaName( mixinName );
    }
}
