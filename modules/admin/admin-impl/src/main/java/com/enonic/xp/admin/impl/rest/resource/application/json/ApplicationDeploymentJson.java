package com.enonic.xp.admin.impl.rest.resource.application.json;

public class ApplicationDeploymentJson
{
    final String url;

    public ApplicationDeploymentJson( final String url )
    {
        this.url = url;
    }

    public String getUrl()
    {
        return url;
    }
}
