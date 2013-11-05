package com.enonic.wem.api.command.schema;

public final class SchemaCommands
{
    public SchemaTypes get()
    {
        return new SchemaTypes();
    }

    public GetRootSchemas getRoots()
    {
        return new GetRootSchemas();
    }

    public GetChildSchemas getChildren()
    {
        return new GetChildSchemas();
    }
}
