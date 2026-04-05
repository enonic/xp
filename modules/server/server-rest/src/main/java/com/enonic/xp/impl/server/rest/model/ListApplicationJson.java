package com.enonic.xp.impl.server.rest.model;

import java.util.List;

public class ListApplicationJson
{
    private final List<ApplicationJson> applications;

    public ListApplicationJson( final List<ApplicationJson> applications )
    {
        this.applications = applications;
    }

    public List<ApplicationJson> getApplications()
    {
        return applications;
    }
}
