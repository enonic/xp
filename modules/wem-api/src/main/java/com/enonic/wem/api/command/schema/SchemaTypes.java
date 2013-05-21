package com.enonic.wem.api.command.schema;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.schema.SchemaKind;
import com.enonic.wem.api.schema.Schemas;

public final class SchemaTypes
    extends Command<Schemas>
{
    private EnumSet<SchemaKind> schemaKinds;

    public SchemaTypes()
    {
        schemaKinds = EnumSet.allOf( SchemaKind.class );
    }

    public SchemaTypes includeTypes( final SchemaKind... schemaKinds )
    {
        this.schemaKinds.clear();
        this.schemaKinds.addAll( Arrays.asList( schemaKinds ) );
        return this;
    }

    public SchemaTypes includeTypes( final Set<SchemaKind> schemaKinds )
    {
        this.schemaKinds.clear();
        this.schemaKinds.addAll( schemaKinds );
        return this;
    }

    public boolean isIncludeType( final SchemaKind schemaKind )
    {
        return schemaKinds.contains( schemaKind );
    }

    @Override
    public void validate()
    {
        // nothing to validate
    }
}
