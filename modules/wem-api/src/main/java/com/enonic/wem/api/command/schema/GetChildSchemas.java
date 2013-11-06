package com.enonic.wem.api.command.schema;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.schema.SchemaKey;
import com.enonic.wem.api.schema.Schemas;

public final class GetChildSchemas
    extends Command<Schemas>
{
    private SchemaKey parentKey;

    public SchemaKey getParentKey()
    {
        return this.parentKey;
    }

    public GetChildSchemas parentKey( final SchemaKey parentName )
    {
        this.parentKey = parentName;
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
        return Objects.equal( this.parentKey, that.parentKey );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.parentKey );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.parentKey, "Parent key cannot be null" );
    }
}
