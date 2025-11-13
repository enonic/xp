package com.enonic.xp.app;

import java.util.Objects;

@Deprecated
public class ApplicationInstallationParams
{
    private final boolean start;

    private final boolean triggerEvent;

    private ApplicationInstallationParams( final Builder builder )
    {
        start = builder.start;
        triggerEvent = false;
    }

    public boolean isStart()
    {
        return start;
    }

    public boolean isTriggerEvent()
    {
        return triggerEvent;
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
        final ApplicationInstallationParams that = (ApplicationInstallationParams) o;
        return start == that.start && triggerEvent == that.triggerEvent;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( start, triggerEvent );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private boolean start = true;

        private Builder()
        {
        }

        public Builder start( final boolean start )
        {
            this.start = start;
            return this;
        }

        @Deprecated
        public Builder triggerEvent( final boolean triggerEvent )
        {
            return this;
        }

        public ApplicationInstallationParams build()
        {
            return new ApplicationInstallationParams( this );
        }
    }
}
