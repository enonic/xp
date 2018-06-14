package com.enonic.xp.vacuum;

public final class VacuumParameters
{
    private final VacuumListener listener;

    private VacuumParameters( final Builder builder )
    {
        this.listener = builder.listener;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public VacuumListener getListener()
    {
        return listener;
    }

    public static final class Builder
    {
        private VacuumListener listener;

        private Builder()
        {
        }

        public Builder listener( final VacuumListener listener )
        {
            this.listener = listener;
            return this;
        }

        public VacuumParameters build()
        {
            return new VacuumParameters( this );
        }
    }
}
