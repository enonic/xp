package com.enonic.xp.vacuum;

import java.util.Map;

public class VacuumTaskConfig
{
    private Map<String, Object> config;

    private VacuumTaskConfig( final Builder builder )
    {
        config = builder.config;
    }

    public static VacuumTaskConfig from( final Map<String, Object> config )
    {
        return create().
            config( config ).
            build();
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private Map<String, Object> config;

        private Builder()
        {
        }

        public Builder config( final Map<String, Object> config )
        {
            this.config = config;
            return this;
        }

        public VacuumTaskConfig build()
        {
            return new VacuumTaskConfig( this );
        }
    }
}
