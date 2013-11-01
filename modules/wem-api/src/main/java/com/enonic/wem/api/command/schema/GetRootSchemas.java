package com.enonic.wem.api.command.schema;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.schema.Schemas;

public final class GetRootSchemas
    extends Command<Schemas>
{

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof GetRootSchemas ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public void validate()
    {
    }
}
