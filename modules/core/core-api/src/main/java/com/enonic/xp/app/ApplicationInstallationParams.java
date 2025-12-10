package com.enonic.xp.app;

@Deprecated
public final class ApplicationInstallationParams
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

        public ApplicationInstallationParams build()
        {
            return new ApplicationInstallationParams( this );
        }
    }
}
