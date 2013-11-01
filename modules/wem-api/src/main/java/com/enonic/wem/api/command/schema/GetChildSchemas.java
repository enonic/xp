package com.enonic.wem.api.command.schema;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.schema.SchemaName;
import com.enonic.wem.api.schema.Schemas;

public final class GetChildSchemas
    extends Command<Schemas>
{
    private SchemaName parentName;

    public SchemaName getParentName()
    {
        return this.parentName;
    }

    public GetChildSchemas parentName( final SchemaName parentName )
    {
        this.parentName = parentName;
        return this;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof GetChildSchemas ) )
        {
            return false;
        }

        final GetChildSchemas that = (GetChildSchemas) o;
        return Objects.equal( this.parentName, that.parentName );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.parentName );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.parentName, "Parent name cannot be null" );
    }
}
