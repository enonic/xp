package com.enonic.xp.admin.impl.rest.resource.application.json;

import java.util.ArrayList;
import java.util.List;

public final class ListApplicationJson
{
    private final List<ApplicationJson> list;

    public ListApplicationJson()
    {
        this.list = new ArrayList<>();
    }

    public void add( final ApplicationJson applicationJson )
    {
        this.list.add( applicationJson );
    }

    public int getTotal()
    {
        return this.list.size();
    }

    public List<ApplicationJson> getApplications()
    {
        return this.list;
    }
}
