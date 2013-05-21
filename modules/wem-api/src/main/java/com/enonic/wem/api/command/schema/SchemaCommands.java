package com.enonic.wem.api.command.schema;

public final class SchemaCommands
{
    public SchemaTypes get()
    {
        return new SchemaTypes();
    }

    public GetSchemaTree getTree()
    {
        return new GetSchemaTree();
    }
}
