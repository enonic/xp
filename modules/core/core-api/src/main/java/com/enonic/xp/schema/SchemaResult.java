package com.enonic.xp.schema;

import com.enonic.xp.resource.Resource;

public final class SchemaResult<T>
{
    private final T schema;

    private final Resource resource;

    public SchemaResult( final T schema, final Resource resource )
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
