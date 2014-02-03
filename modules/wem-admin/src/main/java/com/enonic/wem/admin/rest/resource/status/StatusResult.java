package com.enonic.wem.admin.rest.resource.status;

public final class StatusResult
{
    private String version;

    private String installation;

    public String getVersion()
    {
        return version;
    }

    public void setVersion( final String version )
    {
        this.version = version;
    }

    public String getInstallation()
    {
        return installation;
    }

    public void setInstallation( final String installation )
    {
        this.installation = installation;
    }
}
