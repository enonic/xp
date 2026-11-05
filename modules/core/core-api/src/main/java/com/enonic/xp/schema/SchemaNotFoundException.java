package com.enonic.xp.schema;

import com.enonic.xp.exception.NotFoundException;

public final class SchemaNotFoundException
    extends NotFoundException
{
    public SchemaNotFoundException( final String message )
    {
        super( message );
    }
}
