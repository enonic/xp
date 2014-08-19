package com.enonic.wem.admin.rest.resource.schema.json;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.admin.json.schema.SchemaJson;
import com.enonic.wem.admin.rest.resource.schema.SchemaIconUrlResolver;
import com.enonic.wem.api.schema.Schema;

public final class ListSchemaJson
{
    private final List<SchemaJson> list;

    public ListSchemaJson( final List<Schema> schemas, final SchemaIconUrlResolver iconUrlResolver )
    {
        ImmutableList.Builder<SchemaJson> builder = ImmutableList.builder();
        for ( Schema schema : schemas )
        {
            builder.add( SchemaJson.from( schema, iconUrlResolver ) );
        }
        this.list = builder.build();
    }

    public int getTotal()
    {
        return this.list.size();
    }

    public List<SchemaJson> getSchemas()
    {
        return this.list;
    }
}
