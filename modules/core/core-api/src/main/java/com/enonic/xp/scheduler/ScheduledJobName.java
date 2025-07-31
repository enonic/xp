package com.enonic.xp.scheduler;

import java.io.Serializable;
import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.node.NodeName;

@PublicApi
public final class ScheduledJobName
    implements Serializable
{
    private static final long serialVersionUID = 0;

    private final String value;

    private ScheduledJobName( final Builder builder )
    {
        this.value = builder.value;
    }

    public static ScheduledJobName from( final String scheduledJobName )
    {
        return create().value( scheduledJobName ).build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public String getValue()
    {
        return value;
    }

    @Override
    public boolean equals( final Object o )
    {
        return ( o instanceof ScheduledJobName ) && Objects.equals( this.value, ( (ScheduledJobName) o ).value );
    }

    @Override
    public int hashCode()
    {
        return this.value.hashCode();
    }

    @Override
    public String toString()
    {
        return this.value;
    }

    public static final class Builder
    {
        private String value;

        private Builder()
        {
        }

        public Builder value( final String value )
        {
            this.value = value;
            return this;
        }

        private void validate()
        {
            NodeName.from( value );
        }

        public ScheduledJobName build()
        {
            validate();
            return new ScheduledJobName( this );
        }
    }
}
