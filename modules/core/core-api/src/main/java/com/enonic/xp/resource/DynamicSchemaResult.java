package com.enonic.xp.resource;

public final class DynamicSchemaResult<T>
{
    private final T schema;

    private final Resource resource;

    public DynamicSchemaResult( final T schema, final Resource resource )
    {
        this.schema = schema;
        this.resource = resource;
    }

    public T getSchema()
    {
        return schema;
    }

    public Resource getResource()
    {
        return resource;
    }
}
