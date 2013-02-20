package com.enonic.wem.api.command.content.schema;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.schema.SchemaKind;
import com.enonic.wem.api.content.schema.Schemas;

public final class SchemaTypes
    extends Command<Schemas>
{
    private EnumSet<SchemaKind> baseTypes;

    public SchemaTypes()
    {
        baseTypes = EnumSet.allOf( SchemaKind.class );
    }

    public SchemaTypes includeTypes( final SchemaKind... schemaKinds )
    {
        baseTypes.clear();
        baseTypes.addAll( Arrays.asList( schemaKinds ) );
        return this;
    }

    public SchemaTypes includeTypes( final Set<SchemaKind> schemaKinds )
    {
        baseTypes.clear();
        baseTypes.addAll( schemaKinds );
        return this;
    }

    public boolean isIncludeType( final SchemaKind schemaKind )
    {
        return baseTypes.contains( schemaKind );
    }

    @Override
    public void validate()
    {
        // nothing to validate
    }
}
