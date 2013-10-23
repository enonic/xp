package com.enonic.wem.admin.json.system;

public final class SystemInfoJson
{
    private String installationName;

    private String version;

    private String title;

    public String getInstallationName()
    {
        return installationName;
    }

    public void setInstallationName( final String installationName )
    {
        this.installationName = installationName;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion( final String version )
    {
        this.version = version;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle( final String title )
    {
        this.title = title;
    }
}
