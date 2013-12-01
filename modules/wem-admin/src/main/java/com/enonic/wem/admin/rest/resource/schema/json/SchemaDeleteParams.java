package com.enonic.wem.admin.rest.resource.schema.json;

import java.util.List;

public class SchemaDeleteParams
{
    private List<String> names;

    public List<String> getNames()
    {
        return names;
    }

    public void setNames( final List<String> names )
    {
        this.names = names;
    }
}
