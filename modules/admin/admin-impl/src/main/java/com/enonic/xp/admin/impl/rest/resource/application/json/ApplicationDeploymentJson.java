package com.enonic.xp.admin.impl.rest.resource.application.json;

public class ApplicationDeploymentJson
{
    final Boolean local;

    final String url;

    public ApplicationDeploymentJson( final String url, final Boolean local )
    {
        this.url = url;
        this.local = local;
    }

    public Boolean getLocal()
    {
        return local;
    }

    public String getUrl()
    {
        return url;
    }
}
