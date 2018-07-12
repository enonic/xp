package com.enonic.xp.impl.server.rest.model;

import java.util.Objects;

import com.enonic.xp.app.Application;

public class ApplicationInstalledJson
{
    private ApplicationJson application;

    public ApplicationInstalledJson( final Application application, final boolean local )
    {
        this.application = new ApplicationJson( application, local );
    }

    @SuppressWarnings("unused")
    public ApplicationJson getApplication()
    {
        return application;
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
        final ApplicationInstalledJson that = (ApplicationInstalledJson) o;
        return Objects.equals( application, that.application );
    }

    @Override
    public int hashCode()
    {

        return Objects.hash( application );
    }
}
