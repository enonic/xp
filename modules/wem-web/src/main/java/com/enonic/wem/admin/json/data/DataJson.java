package com.enonic.wem.admin.json.data;

import com.enonic.wem.api.data.Data;

public abstract class DataJson
{
    private Data data;

    protected DataJson( final Data data )
    {
        this.data = data;
    }

    public String getName()
    {
        return data.getName();
    }

    public String getPath()
    {
        return data.getPath().toString();
    }
}
