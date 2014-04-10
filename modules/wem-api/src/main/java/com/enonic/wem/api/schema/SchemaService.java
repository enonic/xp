package com.enonic.wem.api.schema;

public interface SchemaService
{
    public Schemas getRoot();

    public Schemas getChildren( SchemaKey parent );

    public Schemas getTypes( SchemaTypesParams params );
}
