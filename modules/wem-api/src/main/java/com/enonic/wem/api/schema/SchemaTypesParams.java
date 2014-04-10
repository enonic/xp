package com.enonic.wem.api.schema;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

import com.enonic.wem.api.command.Command;

public final class SchemaTypesParams
    extends Command<Schemas>
{
    private EnumSet<SchemaKind> schemaKinds;

    public SchemaTypesParams()
    {
        schemaKinds = EnumSet.allOf( SchemaKind.class );
    }

    public SchemaTypesParams includeTypes( final SchemaKind... schemaKinds )
    {
        this.schemaKinds.clear();
        this.schemaKinds.addAll( Arrays.asList( schemaKinds ) );
        return this;
    }

    public SchemaTypesParams includeTypes( final Set<SchemaKind> schemaKinds )
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
