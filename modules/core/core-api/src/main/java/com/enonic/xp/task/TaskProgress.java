package com.enonic.xp.task;

import java.util.Objects;

import com.google.common.annotations.Beta;
import com.google.common.base.MoreObjects;

@Beta
public final class TaskProgress
{
    public final static TaskProgress EMPTY = TaskProgress.create().build();

    private final int current;

    private final int total;

    private final String info;

    private TaskProgress( final Builder builder )
    {
        current = builder.current;
        total = builder.total;
        info = builder.info != null ? builder.info : "";
    }

    public int getCurrent()
    {
        return current;
    }

    public int getTotal()
    {
        return total;
    }

    public String getInfo()
    {
        return info;
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
        final TaskProgress that = (TaskProgress) o;
        return current == that.current && total == that.total && Objects.equals( info, that.info );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( current, total, info );
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this ).add( "current", current ).add( "total", total ).add( "info", info ).toString();
    }

    public Builder copy()
    {
        return new Builder( this );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private int current;

        private int total;

        private String info;

        private Builder()
        {
        }

        private Builder( final TaskProgress source )
        {
            current = source.current;
            total = source.total;
            info = source.info;
        }

        public Builder current( final int current )
        {
            this.current = current;
            return this;
        }

        public Builder total( final int total )
        {
            this.total = total;
            return this;
        }

        public Builder info( final String info )
        {
            this.info = info;
            return this;
        }

        public TaskProgress build()
        {
            return new TaskProgress( this );
        }
    }
}
