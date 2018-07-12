package com.enonic.xp.impl.server.rest.model;

import java.time.Instant;
import java.util.Objects;

import com.enonic.xp.app.Application;

public class ApplicationJson
{
    final Application application;

    final boolean local;

    public ApplicationJson( final Application application, final boolean local )
    {
        this.application = application;
        this.local = local;
    }

    public String getKey()
    {
        return application.getKey().toString();
    }

    public String getVersion()
    {
        return application.getVersion().toString();
    }

    public String getDisplayName()
    {
        return application.getDisplayName();
    }

    public String getMaxSystemVersion()
    {
        return application.getMaxSystemVersion();
    }

    public String getMinSystemVersion()
    {
        return application.getMinSystemVersion();
    }

    public String getUrl()
    {
        return application.getUrl();
    }

    public String getVendorName()
    {
        return application.getVendorName();
    }

    public String getVendorUrl()
    {
        return application.getVendorUrl();
    }

    public Instant getModifiedTime()
    {
        return this.application.getModifiedTime();
    }

    public String getState()
    {
        return this.application.isStarted() ? "started" : "stopped";
    }

    public boolean getLocal()
    {
        return local;
    }

    public boolean getDeletable()
    {
        return false;
    }

    public boolean getEditable()
    {
        return false;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final ApplicationJson that = (ApplicationJson) o;
        return local == that.local && Objects.equals( application, that.application );
    }

    @Override
    public int hashCode()
    {

        return Objects.hash( application, local );
    }
}
