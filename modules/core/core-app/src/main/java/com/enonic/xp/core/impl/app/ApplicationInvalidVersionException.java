package com.enonic.xp.core.impl.app;

import org.osgi.framework.Version;

public class ApplicationInvalidVersionException
    extends RuntimeException
{
    private final String appSystemVersionRange;

    private final String systemVersion;

    public ApplicationInvalidVersionException( final String appSystemVersionRange,
                                               final Version systemVersion )
    {
        super( String.format( "Cannot start application. Required system version range is [%s]. Current system version is [%s].",
                              appSystemVersionRange, systemVersion ) );
        this.appSystemVersionRange = appSystemVersionRange;
        this.systemVersion = systemVersion.toString();
    }

    public String getAppSystemVersionRange()
    {
        return appSystemVersionRange;
    }

    public String getSystemVersion()
    {
        return systemVersion;
    }
}
