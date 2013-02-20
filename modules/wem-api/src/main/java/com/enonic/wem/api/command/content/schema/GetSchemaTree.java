package com.enonic.wem.api.command.content.schema;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.schema.Schema;
import com.enonic.wem.api.content.schema.SchemaKind;
import com.enonic.wem.api.support.tree.Tree;

public final class GetSchemaTree
    extends Command<Tree<Schema>>
{
    private EnumSet<SchemaKind> schemas;

    public GetSchemaTree()
    {
        schemas = EnumSet.allOf( SchemaKind.class );
    }

    public GetSchemaTree includeTypes( final SchemaKind... schemaKinds )
    {
        schemas.clear();
        schemas.addAll( Arrays.asList( schemaKinds ) );
        return this;
    }

    public GetSchemaTree includeKind( final Set<SchemaKind> schemaKinds )
    {
        schemas.clear();
        schemas.addAll( schemaKinds );
        return this;
    }

    public boolean isIncludingKind( final SchemaKind schemaKind )
    {
        return schemas.contains( schemaKind );
    }

    @Override
    public void validate()
    {
    }
}
