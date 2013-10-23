package com.enonic.wem.admin.rest.resource.schema.json;

import java.util.List;

public class SchemaDeleteParams
{
    private List<String> qualifiedNames;

    public List<String> getQualifiedNames()
    {
        return qualifiedNames;
    }

    public void setQualifiedNames( final List<String> qualifiedNames )
    {
        this.qualifiedNames = qualifiedNames;
    }
}
