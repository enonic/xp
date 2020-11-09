package com.enonic.xp.impl.server.rest.model;

import java.util.List;

public class ListApplicationJson
{

    private final List<ApplicationInfoJson> applications;

    public ListApplicationJson( final List<ApplicationInfoJson> applications )
    {
        this.applications = applications;
    }

    public List<ApplicationInfoJson> getApplications()
    {
        return applications;
    }

}
