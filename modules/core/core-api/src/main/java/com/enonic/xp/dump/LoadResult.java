package com.enonic.xp.dump;

import java.time.Duration;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

public abstract class LoadResult<T>
{
    private final Long successful;

    private final Duration duration;

    private final List<LoadError> errors;

    @SuppressWarnings("unchecked")
    LoadResult( final Builder builder )
    {
        this.successful = builder.successful;
        this.duration = builder.duration != null ? builder.duration : Duration.ofMillis( builder.endTime - builder.startTime );
        this.errors = builder.errors;
    }

    public Long getSuccessful()
    {
        return successful;
    }

    public Duration getDuration()
    {
        return duration;
    }

    public List<LoadError> getErrors()
    {
        return errors;
    }

    public static class Builder<T, B extends Builder>
    {
        private Long successful = 0L;

        private final Long startTime;

        private Long endTime;

        private Duration duration;

        private final List<LoadError> errors = Lists.newArrayList();

        protected Builder()
        {
            this.startTime = System.currentTimeMillis();
        }

        @SuppressWarnings("unchecked")
        public B successful( final Long val )
        {
            successful += val;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B duration( final Duration duration )
        {
            this.duration = duration;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B errors( final Collection<LoadError> errors )
        {
            this.errors.addAll( errors );
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B error( final LoadError error )
        {
            this.errors.add( error );
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        protected T build()
        {
            this.endTime = System.currentTimeMillis();
            return (T) this;
        }

    }
}
