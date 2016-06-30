package com.enonic.xp.admin.impl.rest.resource.application.json;

public class ApplicationInstallResultJson
{
    private ApplicationInstalledJson applicationInstalledJson;

    private String failure;

    public ApplicationInstallResultJson()
    {
    }

    public String getFailure()
    {
        return failure;
    }

    public ApplicationInstalledJson getApplicationInstalledJson()
    {
        return applicationInstalledJson;
    }

    public void setFailure( final String failure )
    {
        this.failure = failure;
    }

    public void setApplicationInstalledJson( final ApplicationInstalledJson applicationInstalledJson )
    {
        this.applicationInstalledJson = applicationInstalledJson;
    }
}
