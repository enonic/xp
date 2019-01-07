package com.enonic.xp.vacuum;

public final class VacuumParameters
{
    private final VacuumListener vacuumProgressListener;

    private final VacuumTaskListener vacuumTaskListener;

    private VacuumParameters( final Builder builder )
    {
        this.vacuumProgressListener = builder.vacuumProgressListener;
        this.vacuumTaskListener = builder.vacuumTaskListener;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public VacuumListener getVacuumProgressListener()
    {
        return vacuumProgressListener;
    }

    public VacuumTaskListener getVacuumTaskListener()
    {
        return vacuumTaskListener;
    }

    public static final class Builder
    {
        private VacuumListener vacuumProgressListener;

        private VacuumTaskListener vacuumTaskListener;

        private Builder()
        {
        }

        public Builder vacuumProgressListener( final VacuumListener vacuumProgressListener )
        {
            this.vacuumProgressListener = vacuumProgressListener;
            return this;
        }

        public Builder vacuumTaskListener( final VacuumTaskListener vacuumTaskListener )
        {
            this.vacuumTaskListener = vacuumTaskListener;
            return this;
        }

        public VacuumParameters build()
        {
            return new VacuumParameters( this );
        }
    }
}
