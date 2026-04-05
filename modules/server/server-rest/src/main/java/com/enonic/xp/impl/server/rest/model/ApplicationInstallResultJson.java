package com.enonic.xp.impl.server.rest.model;

public class ApplicationInstallResultJson
{
    private final ApplicationInstalledJson applicationInstalledJson;

    private final String failure;

    private ApplicationInstallResultJson( final ApplicationInstalledJson applicationInstalledJson, final String failure )
    {
        this.applicationInstalledJson = applicationInstalledJson;
        this.failure = failure;
    }

    public static ApplicationInstallResultJson success( final ApplicationInfoJson application )
    {
        return new ApplicationInstallResultJson( new ApplicationInstalledJson( new ApplicationJson( application ) ), null );
    }

    public static ApplicationInstallResultJson failure( final String failure )
    {
        return new ApplicationInstallResultJson( null, failure );
    }

    public String getFailure()
    {
        return failure;
    }

    public ApplicationInstalledJson getApplicationInstalledJson()
    {
        return applicationInstalledJson;
    }

    public static class ApplicationInstalledJson
    {
        private final ApplicationJson application;

        private ApplicationInstalledJson( final ApplicationJson application )
        {
            this.application = application;
        }

        public ApplicationJson getApplication()
        {
            return application;
        }
    }
}
